package co.nevisa.commonlib.admob.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;

import com.finalsoft.ui.adapter.DialogBottomMenuLayoutAdapter;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;

import org.telegram.messenger.MessageObject;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.LayoutHelper;

import java.util.List;

import co.nevisa.commonlib.Config;

public class MyChatMessageCell extends ChatMessageCell {
    public static final int size = 56;
    private static final String TAG = Config.TAG + "mcmc";

    private NativeAdView nativeAdView;

    public NativeAdView getNativeAdView() {
        return nativeAdView;
    }

    public MyChatMessageCell(Context context) {
        super(context);
        init(context);
    }

    public MyChatMessageCell(Context context, boolean canDrawBackgroundInParent, Theme.ResourcesProvider resourcesProvider) {
        super(context, canDrawBackgroundInParent, resourcesProvider);
        init(context);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    private void init(Context context) {

        nativeAdView = new NativeAdView(context);
        nativeAdView.setSelected(true);
        nativeAdView.setCallToActionView(nativeAdView);
        addView(nativeAdView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.START | Gravity.END | Gravity.TOP | Gravity.BOTTOM));

        setDelegate(new ChatMessageCellDelegate() {
        });
    }

    @Override
    protected void setMessageContent(MessageObject messageObject, MessageObject.GroupedMessages groupedMessages, boolean bottomNear, boolean topNear) {
        super.setMessageContent(messageObject, groupedMessages, bottomNear, topNear);
        //customized
        setImage();
        setAvatar(messageObject);
    }

    private Drawable adDrawable;

    private void setImage() {
        if (adDrawable != null) {
            if (getPhotoImage() != null) {
                getPhotoImage().setImageBitmap(adDrawable);
                getPhotoImage().setVisible(true, false);
            }

        }
    }

    private void setAvatar(MessageObject messageObject) {
        try {
            if (messageObject.getDialogType() == DialogBottomMenuLayoutAdapter.DialogType.GROUP) {
                messageObject.customAvatarDrawable = adDrawable;
                if (getAvatarImage() != null) {
                    if (adDrawable != null) {
                        getAvatarImage().setImageBitmap(adDrawable);
                        getAvatarImage().setVisible(true, false);
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "setMessageContent: ", e);
        }
    }

    public void setAd(ChatActivity.MyChatActivityAdapter.Result result) {
        if (result == null || result.getNativeAd() == null) {
            Log.e(TAG, "setAd: native ad is null");
            return;
        }
        NativeAd nativeAd = result.getNativeAd();
        getPhotoImage().setVisible(true, false);

        List<NativeAd.Image> images = nativeAd.getImages();
        if (images.size() > 0) {
            NativeAd.Image nativeImage = images.get(0);

            if (nativeImage != null && nativeImage.getDrawable() != null) {
                adDrawable = nativeImage.getDrawable();

                setImage();

                setAvatar(result.getMessageObject());
            }
        }


        nativeAdView.setNativeAd(nativeAd);
    }
}

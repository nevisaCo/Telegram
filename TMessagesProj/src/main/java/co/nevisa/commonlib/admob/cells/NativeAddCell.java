package co.nevisa.commonlib.admob.cells;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;


import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;

import java.util.List;

import co.nevisa.commonlib.Config;

public class NativeAddCell extends LinearLayout {
    public static final int size = 56;
    private static final String TAG = Config.TAG + "nac";

    private final NativeAdView nativeAdView;
    private final BackupImageView avatarImageView;
    private final TextView titleTextView;
    private final TextView descTextView;


    public NativeAddCell(@NonNull Context context) {
        this(context, false, true);
    }

    public NativeAddCell(@NonNull Context context, boolean topDivider, boolean bottomDivider) {
        super(context);

        setOrientation(VERTICAL);

        nativeAdView = new NativeAdView(context);
        nativeAdView.setSelected(true);
        addView(nativeAdView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.LEFT | Gravity.TOP));
        nativeAdView.setCallToActionView(nativeAdView);


        avatarImageView = new BackupImageView(context);
        avatarImageView.setRoundRadius(AndroidUtilities.dp(size >> 1));
//        avatarImageView.getImageReceiver().setRoundRadius(AndroidUtilities.dp(2));
        nativeAdView.addView(avatarImageView, LayoutHelper.createFrame(56, 56, LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT, LocaleController.isRTL ? 16 : 10, 8, LocaleController.isRTL ? 10 : 16, 8));
//        Sew Neh Mariamen betam new meemechegn abo

        titleTextView = new TextView(context);
//        titleTextView.setText("Tesfamariam Gebre Amazingnewww Tesfamariam Gebre Amazing new");
//        titleTextView.setBackgroundColor(Theme.getColor(Theme.key_dialogTextBlue2));
        titleTextView.setMaxLines(1);
        titleTextView.setPadding(0, 0, AndroidUtilities.dp(5), 0);
        titleTextView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        titleTextView.setTextSize(15);
        titleTextView.setGravity(LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT);
//        unifiedNativeAdView.addView(titleTextView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 20, Gravity.LEFT | Gravity.TOP, 80, 10, 0 , 0));
        nativeAdView.addView(titleTextView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT, LocaleController.isRTL ? 70 : 80, 14, LocaleController.isRTL ? 80 : 70, 0));


        descTextView = new TextView(context);
//        descTextView.setBackgroundColor(Theme.getColor(Theme.key_wallet_redText));
        descTextView.setTextSize(13);
        descTextView.setMaxLines(3);
        descTextView.setGravity(LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT);
        descTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
        nativeAdView.addView(descTextView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT, LocaleController.isRTL ? 20 : 80, 40, LocaleController.isRTL ? 80 : 20, 0));


        TextView contactChange = new TextView(context);
        contactChange.setTypeface(AndroidUtilities.getTypeface("fonts/rmono.ttf"));
        contactChange.setTextSize(8);
        contactChange.setTextColor(Theme.getColor(Theme.key_dialogBadgeText));
        contactChange.setGravity(Gravity.CENTER);
        contactChange.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(12.5f), Theme.getColor(Theme.key_dialogBadgeBackground)));
        contactChange.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(5), Theme.getColor(Theme.key_windowBackgroundWhiteGrayText)));
        int pad = AndroidUtilities.dp(3);
        contactChange.setPadding(pad, pad, pad, pad);
        contactChange.setText("Ad");
        contactChange.setTypeface(contactChange.getTypeface(), Typeface.BOLD);
        nativeAdView.addView(contactChange, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, (LocaleController.isRTL ? Gravity.LEFT : Gravity.RIGHT) | Gravity.TOP, LocaleController.isRTL ? 14 : 0, 12, LocaleController.isRTL ? 0 : 14, 0));


        if (bottomDivider) {
            View view = new View(context);
            view.setBackgroundColor((Theme.dividerPaint.getColor()));
            addView(view, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 0.5f, Gravity.BOTTOM, LocaleController.isRTL ? 0 : 72, 0, LocaleController.isRTL ? 72 : 0, 0));
        }


    }


    public void setAd(NativeAd nativeAd) {
        if  (nativeAd==null){
            Log.e(TAG, "setAd: nativeAd is null !");
            return;
        }
        List<NativeAd.Image> images = nativeAd.getImages();
        if (images.size() > 0) {
            NativeAd.Image nativeImage = images.get(0);

            if (nativeImage != null && nativeImage.getDrawable() != null) {
                avatarImageView.setImageDrawable(nativeImage.getDrawable());
            }
        }
        if (nativeAd.getHeadline() == null) {
            titleTextView.setVisibility(INVISIBLE);
        } else {
            titleTextView.setVisibility(VISIBLE);
            titleTextView.setText(nativeAd.getHeadline());
        }

        if (nativeAd.getBody() == null) {
            descTextView.setVisibility(View.INVISIBLE);
        } else {
            descTextView.setVisibility(View.VISIBLE);
            descTextView.setText(nativeAd.getBody());
        }
        nativeAdView.setNativeAd(nativeAd);
    }


}

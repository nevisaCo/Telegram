package com.finalsoft.tabhost;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.finalsoft.ui.tab.FolderSettingController;
import com.finalsoft.ui.tab.FolderLayoutAdapter;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BaseController;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.DialogsActivity;

import java.util.ArrayList;

public class FilterPopup extends BaseController {
    private static volatile FilterPopup[] Instance = new FilterPopup[UserConfig.MAX_ACCOUNT_COUNT];
    private ActionBarPopupWindow scrimPopupWindow;
    public ArrayList<FolderLayoutAdapter.Item> items = new ArrayList<>();

    public FilterPopup(int num) {
        super(num);
    }

    public static FilterPopup getInstance(int num) {
        FilterPopup localInstance = Instance[num];
        if (localInstance == null) {
            synchronized (FilterPopup.class) {
                localInstance = Instance[num];
                if (localInstance == null) {
                    Instance[num] = localInstance = new FilterPopup(num);
                }
            }
        }
        return localInstance;
    }





/*    public boolean hasHiddenArchive(int type) {
        if (!SharedConfig.archiveHidden)
            return false;
        ArrayList<TLRPC.Dialog> dialogs = getDialogs(type, 0);
        if (dialogs == null)
            return getMessagesController().hasHiddenArchive();
        for (TLRPC.Dialog dialog : dialogs) {
            if (dialog instanceof TLRPC.TL_dialogFolder) {
                return true;
            }
        }
        return false;
    }*/

    private ArrayList<TLRPC.Dialog> filterUnmutedDialogs(ArrayList<TLRPC.Dialog> allDialogs) {
        ArrayList<TLRPC.Dialog> dialogs = new ArrayList<>();
        for (TLRPC.Dialog dialog : allDialogs) {
            if (dialog instanceof TLRPC.TL_dialogFolder) {
                continue;
            }
            if (!getMessagesController().isDialogMuted(dialog.id)) {
                dialogs.add(dialog);
            }
        }
        return dialogs;
    }


    public int getTotalUnreadCount() {
        ArrayList<TLRPC.Dialog> allDialogs = new ArrayList<>(getMessagesController().getDialogs(0));
        return getDialogsUnreadCount(allDialogs);
    }

    private int getDialogsUnreadCount(ArrayList<TLRPC.Dialog> dialogs) {
        int count = 0;
        for (TLRPC.Dialog dialog : dialogs) {
            if (!(dialog instanceof TLRPC.TL_dialogFolder)
                    && !getMessagesController().isDialogMuted(dialog.id)) {
                count += dialog.unread_count;
            }
        }
        return count;
    }

    FolderLayoutAdapter adapter;

    @SuppressLint("ClickableViewAccessibility")
    public void createMenu(DialogsActivity dialogsActivity, int x, int y, int folderId, boolean fab, DialogsActivity activity) {
        ArrayList<Integer> unreadCounts = new ArrayList<>();
        adapter = new FolderLayoutAdapter(activity, ApplicationLoader.applicationContext);
        ArrayList<FolderLayoutAdapter.Item> items = getItems();
        ArrayList<TLRPC.Dialog> allDialogs = new ArrayList<>(getMessagesController().getDialogs(folderId));
        unreadCounts.add(getDialogsUnreadCount(allDialogs));

        if (scrimPopupWindow != null) {
            scrimPopupWindow.dismiss();
            scrimPopupWindow = null;
            return;
        }

        Rect rect = new Rect();

        ActionBarPopupWindow.ActionBarPopupWindowLayout popupLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(dialogsActivity.getParentActivity());
        popupLayout.setOnTouchListener(new View.OnTouchListener() {

            private int[] pos = new int[2];

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    if (scrimPopupWindow != null && scrimPopupWindow.isShowing()) {
                        View contentView = scrimPopupWindow.getContentView();
                        contentView.getLocationInWindow(pos);
                        rect.set(pos[0], pos[1], pos[0] + contentView.getMeasuredWidth(), pos[1] + contentView.getMeasuredHeight());
                        if (!rect.contains((int) event.getX(), (int) event.getY())) {
                            scrimPopupWindow.dismiss();
                        }
                    }
                } else if (event.getActionMasked() == MotionEvent.ACTION_OUTSIDE) {
                    if (scrimPopupWindow != null && scrimPopupWindow.isShowing()) {
                        scrimPopupWindow.dismiss();
                    }
                }
                return false;
            }
        });
        popupLayout.setDispatchKeyEventListener(keyEvent -> {
            if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_BACK && keyEvent.getRepeatCount() == 0 && scrimPopupWindow != null && scrimPopupWindow.isShowing()) {
                scrimPopupWindow.dismiss();
            }
        });
        Rect backgroundPaddings = new Rect();
        Drawable shadowDrawable = dialogsActivity.getParentActivity().getResources().getDrawable(R.drawable.popup_fixed_alert).mutate();
        shadowDrawable.getPadding(backgroundPaddings);
        popupLayout.setBackgroundDrawable(shadowDrawable);
        popupLayout.setBackgroundColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuBackground));

        GridLayout gridLayout = new GridLayout(dialogsActivity.getParentActivity());

        RelativeLayout cascadeLayout = new RelativeLayout(dialogsActivity.getParentActivity()) {
            @Override
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                setMeasuredDimension(gridLayout.getMeasuredWidth(), getMeasuredHeight());
            }
        };
        cascadeLayout.addView(gridLayout, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.START | Gravity.END));

        ScrollView scrollView;
        if (Build.VERSION.SDK_INT >= 21) {
            scrollView = new ScrollView(dialogsActivity.getParentActivity(), null, 0, R.style.scrollbarShapeStyle) {
                @Override
                protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                    setMeasuredDimension(cascadeLayout.getMeasuredWidth(), getMeasuredHeight());
                }
            };
        } else {
            scrollView = new ScrollView(dialogsActivity.getParentActivity());
        }
        scrollView.setClipToPadding(false);
        popupLayout.addView(scrollView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

        gridLayout.setColumnCount(2);
        gridLayout.setMinimumWidth(AndroidUtilities.dp(200));
        for (int a = 0, N = items.size(); a < N; a++) {
            int[] arr = adapter.procUnreadCount(a);
            int total = arr[0];
            if (total <= 0 && FolderSettingController.getInstance().getShowUnreadOnly(currentAccount)) {
                continue;
            }
            ActionBarMenuSubItem cell = new ActionBarMenuSubItem(  dialogsActivity.getParentActivity(),true,false);
            cell.setTextAndIcon(items.get(a).text, items.get(a).icon);
            cell.setMinimumWidth(AndroidUtilities.dp(210));
            UnreadCountBadgeView badge = new UnreadCountBadgeView(dialogsActivity.getParentActivity(), total + "", arr[1] == 1);

            if (LocaleController.isRTL) {
                gridLayout.addView(badge);
                gridLayout.addView(cell);
            } else {
                gridLayout.addView(cell, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT));
                gridLayout.addView(badge, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.RIGHT));
            }

            badge.setVisibility(total == 0 ? View.INVISIBLE : View.VISIBLE);

            final int i = a;
            cell.setOnClickListener(v1 -> {
                dialogsActivity.updateDialogsType(items.get(i).id, getTitle(i));
                if (scrimPopupWindow != null) {
                    scrimPopupWindow.dismiss();
                }
            });
        }

        scrollView.addView(cascadeLayout, LayoutHelper.createScroll(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT | Gravity.TOP,
                LocaleController.isRTL ? 10 : 0, 0, 0, 0));
        scrimPopupWindow = new ActionBarPopupWindow(popupLayout, LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT) {
            @Override
            public void dismiss() {
                super.dismiss();
                if (scrimPopupWindow != this) {
                    return;
                }
                scrimPopupWindow = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    dialogsActivity.getParentActivity().getWindow().getDecorView().setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_AUTO);
                }
            }
        };
        scrimPopupWindow.setDismissAnimationDuration(220);
        scrimPopupWindow.setOutsideTouchable(true);
        scrimPopupWindow.setClippingEnabled(true);
        scrimPopupWindow.setAnimationStyle(R.style.PopupContextAnimation);
        scrimPopupWindow.setFocusable(true);
        popupLayout.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000), View.MeasureSpec.AT_MOST), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000), View.MeasureSpec.AT_MOST));
        scrimPopupWindow.setInputMethodMode(ActionBarPopupWindow.INPUT_METHOD_NOT_NEEDED);
        scrimPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED);
        scrimPopupWindow.getContentView().setFocusableInTouchMode(true);
        int popupX = x - popupLayout.getMeasuredWidth() + backgroundPaddings.left - AndroidUtilities.dp(28);
        if (popupX < AndroidUtilities.dp(6)) {
            popupX = AndroidUtilities.dp(6);
        } else if (popupX > dialogsActivity.getFragmentView().getMeasuredWidth() - AndroidUtilities.dp(6) - popupLayout.getMeasuredWidth()) {
            popupX = dialogsActivity.getFragmentView().getMeasuredWidth() - AndroidUtilities.dp(6) - popupLayout.getMeasuredWidth();
        }
        int totalHeight = dialogsActivity.getFragmentView().getHeight();
        int height = popupLayout.getMeasuredHeight();
        int popupY = height < totalHeight ? y - (fab ? height : 0) : AndroidUtilities.statusBarHeight;
        scrimPopupWindow.showAtLocation(dialogsActivity.getFragmentView(), Gravity.LEFT | Gravity.TOP, popupX, popupY);
    }

    private ArrayList<FolderLayoutAdapter.Item> getItems() {
        return adapter.getItems();
    }

    public String getTitle(int index) {
/*        if (adapter != null) {
            return adapter.getTitle(index);
        }*/
        return "";
    }




}

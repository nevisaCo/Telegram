/*
 * This is the source code of Telegram for Android v. 5.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2018.
 */

package com.finalsoft.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Switch;

public class TabItemActionCell extends FrameLayout {

    private TextView textView;
    private Switch checkBox;
    ImageView icon;
    TextView badge;
    View selected_shape;

    public TabItemActionCell(Context context, boolean isEditMode) {
        super(context);

        if (isEditMode) {
            textView = new TextView(context);
            textView.setTextColor(Theme.getColor(Theme.key_chats_menuItemText));
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
            textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            textView.setLines(1);
            textView.setMaxLines(1);
            textView.setSingleLine(true);
            textView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
            textView.setCompoundDrawablePadding(AndroidUtilities.dp(29));
            addView(textView,
                    LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.MATCH_PARENT,
                            Gravity.LEFT | Gravity.TOP, 19, 0, 16, 0));

            checkBox = new Switch(context);
            checkBox.setColors(Theme.key_switchTrack, Theme.key_switchTrackChecked,
                    Theme.key_windowBackgroundWhite, Theme.key_windowBackgroundWhite);
            addView(checkBox, LayoutHelper.createFrame(37, 20,
                    Gravity.RIGHT | Gravity.CENTER_VERTICAL, 22, 0,
                    22, 0));
        } else {
            icon = new ImageView(context);
            icon.setColorFilter(Theme.getColor(Theme.key_actionBarDefaultIcon));
            addView(icon, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT,
                    Gravity.TOP, 12, 4, 12, 2));

            badge = new TextView(context);
            badge.setTextSize(11);
            badge.setVisibility(GONE);
            badge.setGravity(Gravity.CENTER);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                badge.setTextAlignment(TEXT_ALIGNMENT_CENTER);
            }
            badge.setBackgroundResource(R.drawable.badge_bg);
            badge.setTextColor(Color.WHITE);
            addView(badge, LayoutHelper.createFrame(17, 17,
                    Gravity.TOP | Gravity.RIGHT, 0, 2, 0, 0));

            selected_shape = new View(context);
            selected_shape.setBackgroundResource(R.drawable.tab_selected);
            selected_shape.setVisibility(GONE);
            addView(selected_shape, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 4,
                    Gravity.BOTTOM, 0, 0, 0, 12));
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(
                MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48), MeasureSpec.EXACTLY));
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (textView != null) {
            textView.setTextColor(Theme.getColor(Theme.key_chats_menuItemText));
        }
    }

    public void setChecked(boolean checked) {
        checkBox.setChecked(checked, true);
    }

    public boolean isChecked() {
        return checkBox.isChecked();
    }

    public void setTextAndIcon(String text, int resId, boolean selected) {
        try {
            if (textView != null) {
                textView.setText(text);
                Drawable drawable = getResources().getDrawable(resId).mutate();
                drawable.setColorFilter(
                        new PorterDuffColorFilter(Theme.getColor(Theme.key_chats_menuItemIcon),
                                PorterDuff.Mode.MULTIPLY));
                textView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
            } else {
                icon.setImageResource(resId);
                if (selected) {
//                    icon.setColorFilter(Theme.getColor(Theme.key_actionBarDefaultIcon));
                    selected_shape.setVisibility(VISIBLE);
                } else {
//                    icon.setColorFilter(Theme.getColor(Theme.key_chats_menuItemIcon));
                    selected_shape.setVisibility(GONE);
                }

            }
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    public void setImageResource(int resId) {
        if (icon != null) {
            icon.setImageResource(resId);
        }
    }


    public void setBadge(int total, boolean un_mute) {
        if (badge != null)
            if (total > 0) {
                badge.setVisibility(View.VISIBLE);
                badge.setText(total > 99 ? "99+" : String.valueOf(total));
                if (un_mute) {
                    badge.getBackground().setColorFilter(Theme.getColor(Theme.key_chats_unreadCounter), PorterDuff.Mode.SRC_IN);
                } else {
                    badge.getBackground().setColorFilter(Theme.getColor(Theme.key_chats_unreadCounterMuted), PorterDuff.Mode.SRC_IN);
                }
                return;
            } else
                badge.setVisibility(View.GONE);

    }

}

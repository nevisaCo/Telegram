/*
 * This is the source code of Telegram for Android v. 5.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2018.
 */

package com.finalsoft.ui;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Switch;

public class DialogBottomActionCell extends FrameLayout {

    private TextView textView;
    private Switch checkBox;
    ImageView icon;

    public DialogBottomActionCell(Context context, boolean isEditMode) {
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
            icon.setColorFilter(Theme.getColor(Theme.key_chats_menuItemIcon));
            icon.setImageResource(R.drawable.profile_voice);
            //int mrg = AndroidUtilities.dp(1);
            addView(icon, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT,
                    Gravity.CENTER, 12, 0, 12, 0));
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

    public void setTextAndIcon(String text, int resId) {
        setTextAndIcon(text, resId);
    }

    public void setTextAndIcon(String text, int resId, boolean checked) {
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
                if (checked) {
                    icon.setColorFilter(Theme.getColor(Theme.key_switchTrackChecked));
                } else {
                    icon.setColorFilter(Theme.getColor(Theme.key_chats_menuItemIcon));
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
}

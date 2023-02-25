/*
 * This is the source code of Telegram for Android v. 5.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2018.
 */

package com.finalsoft.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.Property;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimationProperties;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;

import java.util.ArrayList;

public class ShareToolTextImageCell extends FrameLayout {

    private TextView textView;
    private ImageView imageView;
    private boolean needDivider;
    private boolean isMultiline;
    private int height = 50;
    private int animatedColorBackground;
    private float animationProgress;
    private Paint animationPaint;
    private float lastTouchX;
    private ObjectAnimator animator;
    private boolean drawCheckRipple;

    public static final Property<ShareToolTextImageCell, Float> ANIMATION_PROGRESS = new AnimationProperties.FloatProperty<ShareToolTextImageCell>("animationProgress") {
        @Override
        public void setValue(ShareToolTextImageCell object, float value) {
            object.invalidate();
        }

        @Override
        public Float get(ShareToolTextImageCell object) {
            return object.animationProgress;
        }
    };

    public ShareToolTextImageCell(Context context) {
        this(context, 21);
    }

    public ShareToolTextImageCell(Context context, int padding) {
        this(context, padding, false);
    }

    public ShareToolTextImageCell(Context context, int padding, boolean dialog) {
        super(context);
        imageView = new ImageView(context);
        imageView.setColorFilter(Theme.getColor(Theme.key_switchTrackChecked));
        addView(imageView, LayoutHelper.createFrame(37, 20,
                Gravity.LEFT | Gravity.CENTER_VERTICAL,
                5+ padding, 0, 5, 0));


        textView = new TextView(context);
        textView.setTextColor(Theme.getColor(dialog ? Theme.key_dialogTextBlack : Theme.key_windowBackgroundWhiteBlackText));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        textView.setLines(1);
        textView.setMaxLines(1);
        textView.setSingleLine(true);
        textView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        addView(textView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT,
                LayoutHelper.MATCH_PARENT,
                Gravity.LEFT | Gravity.CENTER_VERTICAL,
                40 + padding,
                0, padding, 0));


        setClipChildren(false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (isMultiline) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        } else {
            super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(height) + (needDivider ? 1 : 0), MeasureSpec.EXACTLY));
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        lastTouchX = event.getX();
        return super.onTouchEvent(event);
    }

    public void setTextAndIcon(String text, int icon, boolean divider) {
        textView.setText(text);
        isMultiline = false;
        imageView.setImageResource(icon);
        needDivider = divider;
        LayoutParams layoutParams = (LayoutParams) textView.getLayoutParams();
        layoutParams.height = LayoutParams.MATCH_PARENT;
        layoutParams.topMargin = 0;
        textView.setLayoutParams(layoutParams);
        setWillNotDraw(!divider);
    }

    public void setColors(String key, String switchKey, String switchKeyChecked, String switchThumb, String switchThumbChecked) {
        textView.setTextColor(Theme.getColor(key));
//        imageView.setColors(switchKey, switchKeyChecked, switchThumb, switchThumbChecked);
        textView.setTag(key);
    }

    public void setTypeface(Typeface typeface) {
        textView.setTypeface(typeface);
    }

    public void setHeight(int value) {
        height = value;
    }

    public void setDrawCheckRipple(boolean value) {
        drawCheckRipple = value;
    }





    public void setEnabled(boolean value, ArrayList<Animator> animators) {
        super.setEnabled(value);
        if (animators != null) {
            animators.add(ObjectAnimator.ofFloat(textView, "alpha", value ? 1.0f : 0.5f));
            animators.add(ObjectAnimator.ofFloat(imageView, "alpha", value ? 1.0f : 0.5f));

        } else {
            textView.setAlpha(value ? 1.0f : 0.5f);
            imageView.setAlpha(value ? 1.0f : 0.5f);
        }
    }


    @Override
    public void setBackgroundColor(int color) {
        clearAnimation();
        animatedColorBackground = 0;
        super.setBackgroundColor(color);
    }

    public void setBackgroundColorAnimated(boolean checked, int color) {
        if (animator != null) {
            animator.cancel();
            animator = null;
        }
        if (animatedColorBackground != 0) {
            setBackgroundColor(animatedColorBackground);
        }
        if (animationPaint == null) {
            animationPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        }
        animatedColorBackground = color;
        animationPaint.setColor(animatedColorBackground);
        animationProgress = 0.0f;
        animator = ObjectAnimator.ofFloat(this, ANIMATION_PROGRESS, 0.0f, 1.0f);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                setBackgroundColor(animatedColorBackground);
                animatedColorBackground = 0;
                invalidate();
            }
        });
        animator.setInterpolator(CubicBezierInterpolator.EASE_OUT);
        animator.setDuration(240).start();
    }



    @Override
    protected void onDraw(Canvas canvas) {
        if (animatedColorBackground != 0) {
            float rad = Math.max(lastTouchX, getMeasuredWidth() - lastTouchX) + AndroidUtilities.dp(40);
            float cx = lastTouchX;
            int cy = getMeasuredHeight() / 2;
            float animatedRad = rad * animationProgress;
            canvas.drawCircle(cx, cy, animatedRad, animationPaint);
        }
        if (needDivider) {
            canvas.drawLine(LocaleController.isRTL ? 0 : AndroidUtilities.dp(20), getMeasuredHeight() - 1, getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(20) : 0), getMeasuredHeight() - 1, Theme.dividerPaint);
        }
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName("android.widget.Switch");
        info.setCheckable(true);
    }
}

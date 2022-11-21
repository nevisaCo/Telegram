/*
 * This is the source code of Telegram for Android v. 5.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2018.
 */

package com.finalsoft.ui;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.IntDef;

import com.finalsoft.SharedStorage;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.browser.Browser;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.LoginActivity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

@TargetApi(23)
public class PrivacyActivity extends BaseFragment {

    private ImageView imageView;
    private TextView buttonTextView;
    private TextView titleTextView;
    private TextView linkTextView;
    private TextView descriptionText;


    @ActionType
    private final int currentType;


    public static final int ACTION_TYPE_PRIVACY_AGREEMENT = 0;


    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
            ACTION_TYPE_PRIVACY_AGREEMENT,
    })
    public @interface ActionType {
    }


    public PrivacyActivity(@ActionType int type) {
        super();
        currentType = type;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View createView(Context context) {
        if (actionBar != null) {
            actionBar.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));

            actionBar.setBackButtonImage(R.drawable.ic_ab_back);
            actionBar.setItemsColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2), false);
            actionBar.setItemsBackgroundColor(Theme.getColor(Theme.key_actionBarWhiteSelector), false);
            actionBar.setCastShadows(false);
            actionBar.setAddToContainer(false);
            if (!AndroidUtilities.isTablet()) {
                actionBar.showActionModeTop();
            }
            actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
                @Override
                public void onItemClick(int id) {
                    if (id == -1) {
                        finishFragment();
                    }
                }
            });
        }

        fragmentView = new ViewGroup(context) {

            @Override
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int width = MeasureSpec.getSize(widthMeasureSpec);
                int height = MeasureSpec.getSize(heightMeasureSpec);

                if (actionBar != null) {
                    actionBar.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), heightMeasureSpec);
                }
                if (currentType == ACTION_TYPE_PRIVACY_AGREEMENT) {
                    if (width > height) {
                        titleTextView.measure(MeasureSpec.makeMeasureSpec((int) (width * 0.6f), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.UNSPECIFIED));
                        linkTextView.measure(MeasureSpec.makeMeasureSpec((int) (width * 0.6f), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.UNSPECIFIED));
                        descriptionText.measure(MeasureSpec.makeMeasureSpec((int) (width * 0.6f), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.UNSPECIFIED));
                        buttonTextView.measure(MeasureSpec.makeMeasureSpec((int) (width * 0.6f), MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(42), MeasureSpec.EXACTLY));
                    } else {
                        titleTextView.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.UNSPECIFIED));
                        linkTextView.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.UNSPECIFIED));
                        descriptionText.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.UNSPECIFIED));
                        buttonTextView.measure(MeasureSpec.makeMeasureSpec((int) (width * 0.4f), MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(42), MeasureSpec.EXACTLY));
                    }
                    imageView.measure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(100), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(100), MeasureSpec.EXACTLY));

                }

                setMeasuredDimension(width, height);
            }

            @Override
            protected void onLayout(boolean changed, int l, int t, int r, int b) {
                if (actionBar != null) {
                    actionBar.layout(0, 0, r, actionBar.getMeasuredHeight());
                }

                int width = r - l;
                int height = b - t;

                if (currentType == ACTION_TYPE_PRIVACY_AGREEMENT) {
                    int y;
                    int x;
                    if (r > b) {
                        //landscape
                        y = (height - imageView.getMeasuredHeight()) / 2;
                        x = (int) (width * 0.5f - imageView.getMeasuredWidth()) / 2;
                        imageView.layout(x, y, x + imageView.getMeasuredWidth(), y + imageView.getMeasuredHeight());
                        x = (int) (width * 0.4f);
                        y = (int) (height * 0.14f);
                        titleTextView.layout(x, y, x + titleTextView.getMeasuredWidth(), y + titleTextView.getMeasuredHeight());
                        y = (int) (height * 0.26f);
                        linkTextView.layout(x, y, x + linkTextView.getMeasuredWidth(), y + linkTextView.getMeasuredHeight());
                        y = (int) (height * 0.41f);
                        descriptionText.layout(x, y, x + descriptionText.getMeasuredWidth(), y + descriptionText.getMeasuredHeight());
                        x = (int) (width * 0.4f + (width * 0.6f - buttonTextView.getMeasuredWidth()) / 2);
                        y = (int) (height * 0.78f);
                    } else {
                        y = (int) (height * 0.214f);
                        x = (width - imageView.getMeasuredWidth()) / 2;
                        imageView.layout(x, y, x + imageView.getMeasuredWidth(), y + imageView.getMeasuredHeight());
                        y = (int) (height * 0.414f);
                        titleTextView.layout(0, y, titleTextView.getMeasuredWidth(), y + titleTextView.getMeasuredHeight());
                        y = (int) (height * 0.450f);
                        linkTextView.layout(0, y, linkTextView.getMeasuredWidth(), y + linkTextView.getMeasuredHeight());
                        y = (int) (height * 0.593f);
                        descriptionText.layout(0, y, descriptionText.getMeasuredWidth(), y + descriptionText.getMeasuredHeight());
                        x = (width - buttonTextView.getMeasuredWidth()) / 2;
                        y = (int) (height * 0.71f);
                    }
                    buttonTextView.layout(x, y, x + buttonTextView.getMeasuredWidth(), y + buttonTextView.getMeasuredHeight());
                }
            }
        };
        fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        ViewGroup viewGroup = (ViewGroup) fragmentView;
        viewGroup.setOnTouchListener((v, event) -> true);

        if (actionBar != null) {
            viewGroup.addView(actionBar);
        }

        imageView = new ImageView(context);
        viewGroup.addView(imageView);

        titleTextView = new TextView(context);
        titleTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        titleTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        titleTextView.setPadding(AndroidUtilities.dp(32), 0, AndroidUtilities.dp(32), 0);
        titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
        viewGroup.addView(titleTextView);

        linkTextView = new TextView(context);
        linkTextView.setTextColor(Theme.getColor(Theme.key_dialogTextLink));
        linkTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        linkTextView.setPadding(AndroidUtilities.dp(32), AndroidUtilities.dp(32), AndroidUtilities.dp(32), AndroidUtilities.dp(32));
        linkTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        viewGroup.addView(linkTextView);
        linkTextView.setOnClickListener(view ->
                Browser.openUrl(getParentActivity(), LocaleController.getString("PrivacyPolicyUrl", R.string.PrivacyPolicyUrl))
        );


        descriptionText = new TextView(context);
        descriptionText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText6));
        descriptionText.setGravity(Gravity.CENTER_HORIZONTAL);
        descriptionText.setLineSpacing(AndroidUtilities.dp(2), 1);
        descriptionText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);

        descriptionText.setPadding(AndroidUtilities.dp(32), 0, AndroidUtilities.dp(32), 0);

        viewGroup.addView(descriptionText);

        buttonTextView = new TextView(context) {
            @Override
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);

            }
        };

        buttonTextView.setPadding(AndroidUtilities.dp(34), 0, AndroidUtilities.dp(34), 0);
        buttonTextView.setGravity(Gravity.CENTER);
        buttonTextView.setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText));
        buttonTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        buttonTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        int buttonRadiusDp = 4;
        buttonTextView.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(buttonRadiusDp), Theme.getColor(Theme.key_featuredStickers_addButton), Theme.getColor(Theme.key_featuredStickers_addButtonPressed)));
        viewGroup.addView(buttonTextView);
        buttonTextView.setOnClickListener(v -> {
            if (getParentActivity() == null) {
                return;
            }
            SharedStorage.privacyAgreementShown(true);
            presentFragment(new LoginActivity());
        });

        if (currentType == ACTION_TYPE_PRIVACY_AGREEMENT) {
            imageView.setBackgroundDrawable(Theme.createCircleDrawable(AndroidUtilities.dp(100), Theme.getColor(Theme.key_chats_archiveBackground)));
            imageView.setImageResource(R.drawable.msg_policy);
            imageView.setScaleType(ImageView.ScaleType.CENTER);

            titleTextView.setText(LocaleController.getString("PrivacyPolicy", R.string.PrivacyPolicy));
            linkTextView.setText(LocaleController.getString("TermsOfService", R.string.TermsOfService));
            descriptionText.setText(R.string.PrivacyAgreement);
            buttonTextView.setText(LocaleController.getString("Continue", R.string.Continue));
        }

        return fragmentView;
    }


    @Override
    public boolean hasForceLightStatusBar() {
        return true;
    }




    public int getType() {
        return currentType;
    }

    @Override
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();

        themeDescriptions.add(new ThemeDescription(fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));

        if (actionBar != null) {
            themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
            themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText2));
            themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarWhiteSelector));
        }

        themeDescriptions.add(new ThemeDescription(titleTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(linkTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(descriptionText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText6));
        themeDescriptions.add(new ThemeDescription(buttonTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_featuredStickers_buttonText));
        themeDescriptions.add(new ThemeDescription(buttonTextView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE, null, null, null, null, Theme.key_featuredStickers_addButton));
        themeDescriptions.add(new ThemeDescription(buttonTextView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_featuredStickers_addButtonPressed));

        return themeDescriptions;
    }
}

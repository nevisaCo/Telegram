package com.finalsoft.helper;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.finalsoft.Config;
import com.finalsoft.SharedStorage;

import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildConfig;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TextViewHelper {

    private static TextViewHelper textViewHelper;

    public static TextViewHelper getInstance() {
        if (textViewHelper == null) {
            textViewHelper = new TextViewHelper();
        }
        return textViewHelper;
    }

    public void setIcons(TextView textView, int icon) {
        setIcons(textView, icon, 0.9f, R.dimen.app_widget_background_corner_radius);
    }

    public void setIcons(TextView textView, int icon, float scale, int padding) {
        if (icon == 0) {
            return;
        }

        try {
            Drawable drawable = ContextCompat.getDrawable(ApplicationLoader.applicationContext, icon);
            if (drawable == null) {
                return;
            }
            int pixelDrawableSize = (int) Math.round(textView.getLineHeight() * scale); // Or the percentage you like (0.8, 0.9, etc.)
            drawable.setBounds(0, 0, pixelDrawableSize, pixelDrawableSize);
            textView.setCompoundDrawables(drawable, null, null, null);
            textView.setCompoundDrawablePadding(ApplicationLoader.applicationContext.getResources().getDimensionPixelOffset(padding));
        } catch (Resources.NotFoundException e) {
            Log.e(Config.TAG, "setFilter: ", e);
        }
    }
}

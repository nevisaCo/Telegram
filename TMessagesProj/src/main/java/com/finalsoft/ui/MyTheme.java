package com.finalsoft.ui;

import static com.finalsoft.Config.TAG;
import static org.telegram.ui.ActionBar.Theme.chat_docNamePaint;
import static org.telegram.ui.ActionBar.Theme.chat_locationTitlePaint;
import static org.telegram.ui.ActionBar.Theme.key_chats_muteIcon;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.Log;

import com.finalsoft.Config;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.Theme;

public class MyTheme extends Theme {
    private static final String TAG = Config.TAG + "mth";
    public static Typeface typeface = AndroidUtilities.getTypeface("");
    public static Drawable avatar_ghostDrawable;
    public static Drawable dialogs_ghostDrawable;
    public static Drawable dialogs_mutualContactDrawable;
    //region Customized:
    public static Drawable chat_markDrawable;
    public static Drawable chat_markIconDrawable, chat_markFilledIconDrawable;
    //endregion


    public static void createDialogsResources(Context context) {
        try {
            Resources resources = context.getResources();
            for (int a = 0; a < 2; a++) {
                dialogs_namePaint[a].setTypeface(typeface);
                dialogs_nameEncryptedPaint[a].setTypeface(typeface);
                dialogs_messagePaint[a].setTypeface(typeface); //Customized: set dialogs description messages font
            }
            dialogs_searchNamePaint.setTypeface(typeface);
            dialogs_searchNameEncryptedPaint.setTypeface(typeface);
            dialogs_messageNamePaint.setTypeface(typeface);
            dialogs_countTextPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));

            //customized:
            dialogs_ghostDrawable = resources.getDrawable(BuildVars.GHOST_ON_ICON).mutate();
            dialogs_mutualContactDrawable = resources.getDrawable(R.drawable.ic_outline_contact_phone).mutate();
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "createDialogsResources: ", e);
        }

    }

    public static void applyDialogsTheme() {

        try {
            setDrawableColorByKey(dialogs_ghostDrawable, key_chats_muteIcon);
            setDrawableColorByKey(dialogs_mutualContactDrawable, key_chats_muteIcon);
        } catch (Exception e) {
            Log.e(TAG, "applyDialogsTheme: ", e);
        }
    }

    public static void createCommonResources(Context context) {
        try {
            ThemeInfo themeInfo = new ThemeInfo();
            themeInfo.name = "Default";
            themeInfo.assetName = "theme.attheme";
            themeInfo.isDark = 1;
            themeInfo.previewBackgroundColor = 0x6086630;
            themeInfo.previewInColor = 0xffffffff;
            themeInfo.previewOutColor = 0xffd0e6ff;
            themeInfo.sortIndex = 0;
            Theme.themes.add(0, currentDayTheme = currentTheme = defaultTheme = themeInfo);
            Theme.themesDict.put("Default", themeInfo);

            Resources resources = context.getResources();
            avatar_ghostDrawable = resources.getDrawable(R.drawable.ghost);


            //customized:
            chat_markDrawable = createRoundRectDrawable(AndroidUtilities.dp(16), 0xffffffff);
            chat_markIconDrawable = resources.getDrawable(R.drawable.msg_unfave);
            chat_markFilledIconDrawable = resources.getDrawable(R.drawable.msg_fave);
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "createCommonResources: ", e);
        }
    }

    public static void createCommonMessageResources() {
        try {
            chat_msgBotButtonPaint.setTypeface(typeface);
            chat_namePaint.setTypeface(typeface);
            chat_replyNamePaint.setTypeface(typeface);
            chat_topicTextPaint.setTypeface(typeface);
            //region Customized: set font for messages text
            chat_msgGameTextPaint.setTypeface(typeface);
            chat_msgTextPaint.setTypeface(typeface);
            //endregion
        } catch (Exception e) {
            Log.e(TAG, "createCommonMessageResources: ", e);
        }
    }

    public static void createCommonChatResources() {
        try {
            Theme.chat_docNamePaint.setTypeface(typeface);
            Theme.chat_locationTitlePaint.setTypeface(typeface);

            Theme.chat_livePaint.setTypeface(typeface);
            Theme.chat_audioTitlePaint.setTypeface(typeface);
            Theme.chat_botButtonPaint.setTypeface(typeface);
            Theme.chat_contactNamePaint.setTypeface(typeface);
            Theme.chat_gamePaint.setTypeface(typeface);
            Theme.chat_namePaint.setTypeface(typeface);
            Theme.chat_replyNamePaint.setTypeface(typeface);
            Theme.chat_topicTextPaint.setTypeface(typeface);
            Theme.chat_instantViewPaint.setTypeface(typeface);

            Theme.chat_contextResult_titleTextPaint.setTypeface(typeface);
        } catch (Exception e) {
            Log.e(TAG, "createCommonChatResources: ", e);
        }

    }
}

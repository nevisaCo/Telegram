package com.finalsoft.helper;

import android.content.Context;
import android.widget.TextView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;


public class LabelHelper {
    public static TextView CreateLabel(Context context, String str, int strint){
        TextView textView=new TextView(context);
        textView.setText(LocaleController.getString(str,strint));
        textView.setPadding(10,20,10,10);
        textView.setTextColor(Theme.getColor(Theme.key_actionBarDefaultTitle));
        textView.setTypeface(AndroidUtilities.getTypeface("fonts/IRANSansLight.ttf"));
        textView.setTextSize(15);
        return textView ;
    }
}

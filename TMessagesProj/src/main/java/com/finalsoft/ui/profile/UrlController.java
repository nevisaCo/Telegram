package com.finalsoft.ui.profile;

import android.content.Context;
import android.content.DialogInterface;

import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.AlertDialog;


public class UrlController {

    public static String APPFILENAME = "Mobo Golden.apk";
    public static String Email = "";

    public static String SERVERADD = "https://api.telegram.org/";
    public static final String APP_OWNER = "";
    public static final String DefaultTheme = "طلایی";
    public static final String PICURL = "";
    public static String ThemeChannel = "AndroidThemes";

    public static String SupportUsername = "best";
    public static boolean SupportEnabeld = true;
    public static String[] JoinAtStart = new String[]{"mobooo"};
    public static final boolean GHOSTACTIVE = false;
    //addad
    public static boolean AdadActive = false;

    //adjust
    public static final boolean ADJASTENABLED = false;
    public static final String AdjustToken = "";


    //dialog settings
    public static final String WELLCOME_TITLE = "";
    public static final String WELLCOME_TEXT = "";
    public static final boolean SHOWMSGWELCOME = false;


    public static void ShowDialog(Context context) {
        String text = WELLCOME_TEXT;
        String title = WELLCOME_TITLE;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(text);

        builder.setNegativeButton(LocaleController.getString("OK", R.string.OK).toUpperCase(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        //show();
        if (UrlController.SHOWMSGWELCOME) builder.show();
    }
}

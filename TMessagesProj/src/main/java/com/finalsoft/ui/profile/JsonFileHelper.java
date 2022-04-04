package com.finalsoft.ui.profile;

import org.telegram.messenger.ApplicationLoader;

import java.io.IOException;
import java.io.InputStream;

public class JsonFileHelper {
    public static String loadJSONFromAsset(String path) {
        String json = null;
        try {
            InputStream is = ApplicationLoader.applicationContext.getAssets().open(path);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}

package com.finalsoft.translator;

import android.text.TextUtils;
import android.util.Log;

import com.finalsoft.Config;
import com.finalsoft.SharedStorage;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.telegram.messenger.FileLog;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class GoogleWebTranslator extends Translator {

    private static final String TAG = Config.TAG + "Trans";
    private static GoogleWebTranslator instance;
    private List<String> targetLanguages = Arrays.asList(
            "sq", "ar", "am", "az", "ga", "et", "eu", "be", "bg", "is", "pl", "bs", "fa",
            "af", "da", "de", "ru", "fr", "tl", "fi", "fy", "km", "ka", "gu", "kk", "ht",
            "ko", "ha", "nl", "ky", "gl", "ca", "cs", "kn", "co", "hr", "ku", "la", "lv",
            "lo", "lt", "lb", "ro", "mg", "mt", "mr", "ml", "ms", "mk", "mi", "mn", "bn",
            "my", "hmn", "xh", "zu", "ne", "no", "pa", "pt", "ps", "ny", "ja", "sv", "sm",
            "sr", "st", "si", "eo", "sk", "sl", "sw", "gd", "ceb", "so", "tg", "te", "ta",
            "th", "tr", "cy", "ur", "uk", "uz", "es", "iw", "el", "haw", "sd", "hu", "sn",
            "hy", "ig", "it", "yi", "hi", "su", "id", "jw", "en", "yo", "vi", "zh-TW", "zh-CN", "zh");
//    private long[] tkk;

    static GoogleWebTranslator getInstance() {
        if (instance == null) {
            synchronized (GoogleWebTranslator.class) {
                if (instance == null) {
                    instance = new GoogleWebTranslator();
                }
            }
        }
        return instance;
    }

    @Override
    protected String translate(String query, String tl) {
        Log.i(TAG, "googleweb > translate: " + query + ", tl:" + tl);
        String result = translateImpl(query, tl);
     /*   if (result == null) {
//            tkk = null;
            return translateImpl(query, tl);
        }*/
        return result;
    }

    @Override
    protected List<String> getTargetLanguages() {
        return targetLanguages;
    }


    private String translateImpl(String query, String tl) {
/*        if (tkk == null) {
            initTkk();
        }
        if (tkk == null) {
            return null;
        }*/
//        String tk = Utils.signWeb(query, tkk[0], tkk[1]);
        String url = String.format("https://translate.google.%s/translate_a/single?dj=1&client=at&dt=t&sl=auto&tl=%s&q=%s",
                SharedStorage.translationProvider() == 2 ? "cn" : "com",
                tl,
                Utils.encodeURIComponent(query)
        );


        String response = request(url);
        Log.i(TAG, "translateImpl: " + url + ", response:" + response);

        if (TextUtils.isEmpty(response)) {
            return null;
        }
        try {
            return getResult(response);
        } catch (JSONException e) {
            FileLog.e(response + e);
            return null;
        }
    }

    private String getResult(String string) throws JSONException {
        StringBuilder sb = new StringBuilder();
        JSONObject jsonObject = new JSONObject(string);

        JSONObject jo = jsonObject.getJSONArray("sentences").getJSONObject(0);



/*        for (int i = 0; i < array.length(); i++) {
            sb.append(array.getJSONArray(i).getString(0));
        }*/
        return jo.getString("trans");
    }

/*    private void initTkk() {
        String response = request("https://translate.google." + (SharedStorage.translationProvider() == 2 ? "cn" : "com"));
        Log.i(TAG, "initTkk: " + response);
        if (TextUtils.isEmpty(response)) {
            FileLog.e("Tkk init failed");
            return;
        }
        tkk = matchTKK(response);
        if (tkk == null) {
            FileLog.e("Tkk init failed");
        }
    }*/

/*    private long[] matchTKK(String src) {
        Matcher matcher = Pattern.compile("tkk\\s*[:=]\\s*['\"]([0-9]+)\\.([0-9]+)['\"]",
                Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE).matcher(src);
        if (matcher.find()) {
            if (matcher.group(1) == null || matcher.group(2) == null) {
                return null;
            }
            //noinspection ConstantConditions
            return new long[]{Long.parseLong(matcher.group(1)), Long.parseLong(matcher.group(2))};
        }
        return null;
    }*/

    private String request(String url) {

        try {
            ByteArrayOutputStream outbuf;
            InputStream httpConnectionStream;
            URL downloadUrl = new URL(url);
            URLConnection httpConnection = downloadUrl.openConnection();
            httpConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 10_0 like Mac OS X) AppleWebKit/602.1.38 (KHTML, like Gecko) Version/10.0 Mobile/14A5297c Safari/602.1");
            httpConnection.setConnectTimeout(10000);
            httpConnection.setReadTimeout(20000);
            httpConnection.connect();
            httpConnectionStream = httpConnection.getInputStream();

            outbuf = new ByteArrayOutputStream();

            byte[] data = new byte[1024 * 32];
            while (true) {
                int read = httpConnectionStream.read(data);
                if (read > 0) {
                    outbuf.write(data, 0, read);
                } else if (read == -1) {
                    break;
                } else {
                    break;
                }
            }
            String result = new String(outbuf.toByteArray());
            try {
                httpConnectionStream.close();
            } catch (Throwable e) {
                FileLog.e(e);
            }
            try {
                outbuf.close();
            } catch (Exception ignore) {

            }
            return result;
        } catch (Throwable e) {
            FileLog.e(e);
            return null;
        }
    }
}

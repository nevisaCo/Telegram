package com.finalsoft.translator;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import com.finalsoft.Config;
import com.finalsoft.SharedStorage;

import org.telegram.messenger.LocaleController;

import java.util.List;

abstract public class Translator {
    private static final String TAG = Config.TAG + "Trans";

    public static void translate(String query, TranslateCallBack translateCallBack) {
        translate(query, translateCallBack, "");
    }

    public static void translate(String query, TranslateCallBack translateCallBack, String locale) {
        if (locale.isEmpty()) {
            locale = LocaleController.getInstance().currentLocale.getLanguage();
        }
        int translationProvider = SharedStorage.translationProvider();
        String toLang;
        if (translationProvider != 3 && locale.equals("zh") && (locale.toUpperCase().equals("CN") || locale.toUpperCase().equals("TW"))) {
            toLang = locale + "-" + locale.toUpperCase();
        } else {
            toLang = locale;
        }
        Log.i(TAG, "translate: toLang:" + toLang  + ", query:"+ query + ", local:"+ locale + ", translationProvider:"+ translationProvider);
        Translator translator = translationProvider == 3 ? LingoTranslator.getInstance() : GoogleWebTranslator.getInstance();
        if (!translator.getTargetLanguages().contains(toLang)) {
            translateCallBack.onUnsupported();
        } else {
            translator.startTask(query, toLang, translateCallBack);
        }
    }

    private void startTask(String query, String toLang, TranslateCallBack translateCallBack) {
        new MyAsyncTask().request(query, toLang, translateCallBack).execute();
    }

    abstract protected String translate(String query, String tl);

    abstract protected List<String> getTargetLanguages();

    public interface TranslateCallBack {
        void onSuccess(String translation);

        void onError();

        void onUnsupported();
    }

    @SuppressLint("StaticFieldLeak")
    private class MyAsyncTask extends AsyncTask<Void, Integer, String> {
        TranslateCallBack translateCallBack;
        String query;
        String tl;

        public MyAsyncTask request(String query, String tl, TranslateCallBack translateCallBack) {
            this.query = query;
            this.tl = tl;
            this.translateCallBack = translateCallBack;
            return this;
        }

        @Override
        protected String doInBackground(Void... params) {
            return translate(query, tl);
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i(TAG, "onPostExecute: " + result);
            if (result == null) {
                translateCallBack.onError();
            } else {
                translateCallBack.onSuccess(result);
            }
        }

    }

}

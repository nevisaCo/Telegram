package com.finalsoft.helper;

import android.util.Log;

import com.finalsoft.Config;
//import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;

public class FireBaseHelper {

    private static final String TAG = Config.TAG + "fh";

    public void initialize(ApplicationLoader applicationLoader) {
        try {
            FirebaseMessaging.getInstance().subscribeToTopic("all");
            if (BuildVars.DEBUG_VERSION)
                FirebaseMessaging.getInstance().subscribeToTopic("debug");
        } catch (Exception e) {
            e.printStackTrace();
        }


/*    FirebaseApp.initializeApp(this, new FirebaseOptions.Builder()
        .setApiKey("<VAL>")
        .setApplicationId("<VAL>")
        .setDatabaseUrl("<VAL>")
        .setGcmSenderId("<VAL>")
        .setStorageBucket("<VAL>")
        .build());*/


   /*     if (BuildVars.DEBUG_VERSION) {
            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        if (task.getResult() != null) {
                            String token = task.getResult().getToken();
                            Log.d(TAG, token);
                        }
                    });
        }*/


    }


    //region Customized:
    private void firebaseRemoteConfig() {

        FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(1)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);

        //mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);

        // cacheExpirationSeconds is set to cacheExpiration here, indicating the next fetch request
        // will use fetch data from the Remote Config service, rather than cached parameter values,
        // if cached parameter values are more than cacheExpiration seconds old.
        // See Best Practices in the README for more information.
        mFirebaseRemoteConfig.fetch(0)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
//                        Toast.makeText(getBaseContext(), "Fetch and activate succeeded",
//                                Toast.LENGTH_SHORT).show();
                    } else {
//                        Toast.makeText(getBaseContext(), "Fetch failed",
//                                Toast.LENGTH_SHORT).show();
                    }
                    displayWelcomeMessage();
                    String welcomeMessage = mFirebaseRemoteConfig.getString("app_name");
//                    Toast.makeText(ApplicationLoader.this, welcomeMessage, Toast.LENGTH_SHORT).show();
                });
    }

    private void displayWelcomeMessage() {

    }
}

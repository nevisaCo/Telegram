package com.finalsoft.update;

import static com.google.android.play.core.install.model.AppUpdateType.FLEXIBLE;
import static com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE;

import android.app.Activity;
import android.content.IntentSender;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.appupdate.AppUpdateOptions;
import com.google.android.play.core.appupdate.testing.FakeAppUpdateManager;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;
import com.google.gson.Gson;

import co.nevisa.commonlib.Config;

public class GoogleUpdateHelper {
    private Activity context;
    private static final String TAG = Config.TAG + "guh";
    private static GoogleUpdateHelper _gooGoogleUpdateHelper;

    public static GoogleUpdateHelper getInstance() {
        if (_gooGoogleUpdateHelper == null) {
            _gooGoogleUpdateHelper = new GoogleUpdateHelper();
        }
        return _gooGoogleUpdateHelper;
    }

    private static final Integer DAYS_FOR_FLEXIBLE_UPDATE = 4;
    private static final int MY_REQUEST_CODE = 100035;
    InstallStateUpdatedListener listener;
    private IUpdateCompleted iUpdateCompleted;
    AppUpdateManager appUpdateManager;


    public void init(Activity context, @NonNull IUpdateCompleted iUpdateCompleted) {
        this.context = context;
        this.iUpdateCompleted = iUpdateCompleted;
        appUpdateManager = AppUpdateManagerFactory.create(context);

        // Create a listener to track request state updates.

        listener = state -> {
//            if (Config.DEBUG_VERSION) {
            Log.i(TAG, "init: " + new Gson().toJson(state));
//            }
            // (Optional) Provide a download progress bar.
            if (state.installStatus() == InstallStatus.DOWNLOADING) {
                long bytesDownloaded = state.bytesDownloaded();
                long totalBytesToDownload = state.totalBytesToDownload();
                // Implement progress bar.
                Log.i(TAG, "init: " + bytesDownloaded);
            }

            if (state.installStatus() == InstallStatus.DOWNLOADED) {
                // After the update is downloaded, show a notification
                // and request user confirmation to restart the app.
                iUpdateCompleted.onCompleted();
                Log.i(TAG, "init: DOWNLOADED");
            }

            if (state.installStatus() == InstallStatus.INSTALLED
                    || state.installStatus() == InstallStatus.CANCELED
                    || state.installStatus() == InstallStatus.FAILED
                    || state.installStatus() == InstallStatus.UNKNOWN) {
                // When status updates are no longer needed, unregister the listener.
                appUpdateManager.unregisterListener(listener);
                Log.i(TAG, "init: unregisterListener");
            }
            // Log state or install the update.
        };


        // Returns an intent object that you use to check for an update.
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {

//            if (Config.DEBUG_VERSION) {
            Log.d(TAG, "packageName :" + appUpdateInfo.packageName() + ", " + "availableVersionCode :" + appUpdateInfo.availableVersionCode() + ", " + "updateAvailability :" + appUpdateInfo.updateAvailability() + ", " + "installStatus :" + appUpdateInfo.installStatus());
            Log.i(TAG, "init: " + new Gson().toJson(appUpdateInfo));
//            }
            appUpdateManager.registerListener(listener);


            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE

                /*&& appUpdateInfo.isUpdateTypeAllowed(IMMEDIATE)*/) {
                // Request the update.
                int type = FLEXIBLE;
                if (appUpdateInfo.updatePriority() >= 3 /* high priority*/
                        || (appUpdateInfo.clientVersionStalenessDays() != null && appUpdateInfo.clientVersionStalenessDays() >= DAYS_FOR_FLEXIBLE_UPDATE)) {
                    type = IMMEDIATE;
                }
                try {
                    Log.i(TAG, "init: registerListener");

                    appUpdateManager.startUpdateFlowForResult(
                            // Pass the intent that is returned by 'getAppUpdateInfo()'.
                            appUpdateInfo,
                            // The current activity making the update request.
                            context,
                            // Or pass 'AppUpdateType.FLEXIBLE' to newBuilder() for
                            // flexible updates.
                            AppUpdateOptions.newBuilder(type)
                                    .setAllowAssetPackDeletion(false)
                                    .build(),
                            // Include a request code to later monitor this update request.
                            MY_REQUEST_CODE);
                    // Before starting an update, register a listener for updates.
                } catch (IntentSender.SendIntentException e) {
                    Log.e(TAG, "initInAppUpdate: ", e);
                }
            }
        });

        appUpdateInfoTask.addOnFailureListener(e -> Log.e(TAG, "onFailure: ", e));
    }

    // Displays the snackbar notification and call to action.

    public void completeUpdate() {
        if (appUpdateManager == null) {
            return;
        }
        appUpdateManager.completeUpdate();

    }

    public void resume() {
        if (appUpdateManager == null) {
            return;
        }
        appUpdateManager
                .getAppUpdateInfo()
                .addOnSuccessListener(
                        appUpdateInfo -> {

                            if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                                if (iUpdateCompleted != null) {
                                    iUpdateCompleted.onCompleted();
                                }
                            }

                            if (appUpdateInfo.updateAvailability()
                                    == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                                // If an in-app update is already running, resume the update.
                                try {
                                    appUpdateManager.startUpdateFlowForResult(
                                            appUpdateInfo,
                                            IMMEDIATE,
                                            context,
                                            MY_REQUEST_CODE);
                                } catch (IntentSender.SendIntentException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
    }

    public void fakeUpload(Activity activity) {
        FakeAppUpdateManager fakeAppUpdateManager = new FakeAppUpdateManager(activity);
        fakeAppUpdateManager.setUpdateAvailable(23, AppUpdateType.FLEXIBLE); // add app version code greater than current version.
        fakeAppUpdateManager.setUpdatePriority(4);
        fakeAppUpdateManager.setTotalBytesToDownload(5000000);
        fakeAppUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                Log.i(TAG, "fakeUpload: ");
            }
        });
    }
}

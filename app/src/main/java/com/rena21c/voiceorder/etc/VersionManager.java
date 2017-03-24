package com.rena21c.voiceorder.etc;

import android.app.Activity;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.rena21c.voiceorder.network.NetworkUtil;
import com.rena21c.voiceorder.view.dialogs.Dialogs;

public class VersionManager {

    public interface MeetRequiredVersionListener {
        void onMeetRequiredVersion();
    }

    public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private final String REQUIRED_VERSION_KEY = "required_version";

    private long deviceVersion;
    private Activity activity;

    public VersionManager(Activity activity) {
        this.activity = activity;

        try {
            deviceVersion = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            FirebaseCrash.report(e);
        }
    }

    public boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                Dialog dialog = apiAvailability.getErrorDialog(activity, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST);
                dialog.setCancelable(false);
                dialog.show();
            } else {
                Dialogs.createNoSupportDeviceDialog(activity).show();
            }
            return false;
        }
        return true;
    }

    public void checkAppVersion(final MeetRequiredVersionListener listener) {

        if (!NetworkUtil.isInternetConnected(activity.getApplicationContext())) {
            AlertDialog blockingDialog = Dialogs.createNoInternetConnectivityAlertDialog(activity);
            blockingDialog.show();
            return;
        }

        final FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        firebaseRemoteConfig.fetch()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            Log.e("VersionManager", "success");
                            firebaseRemoteConfig.activateFetched();
                        } else {
                            Log.e("VersionManager", "fail");
                            FirebaseCrash.report(task.getException());
                        }

                        if (firebaseRemoteConfig.getLong(REQUIRED_VERSION_KEY) > deviceVersion) {
                            Dialogs.createVersionUpdateAlertDialog(activity).show();
                        } else {
                            listener.onMeetRequiredVersion();
                        }
                    }
                });
    }
}

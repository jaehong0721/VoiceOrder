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

public class PlayServiceManager {

    public interface CheckPlayServiceListener {
        void onNext();
    }

    public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    public static void checkPlayServices(Activity activity, CheckPlayServiceListener listener) {
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
        }else{
            listener.onNext();
        }
    }

}

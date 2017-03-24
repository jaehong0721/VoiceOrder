package com.rena21c.voiceorder.etc;


import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.rena21c.voiceorder.view.dialogs.Dialogs;

public class VersionManager {


    public interface MeetRequiredVersionListener {
        void onMeetRequiredVersion();
    }

    private static final String REQUIRED_VERSION_KEY = "required_version";

    public static void checkAppVersion(final Activity activity, final MeetRequiredVersionListener listener) {
        try {
            final long deviceVersion = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionCode;

            final FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

            firebaseRemoteConfig.fetch()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                Log.e("PlayServiceManager", "success");
                                firebaseRemoteConfig.activateFetched();
                            } else {
                                Log.e("PlayServiceManager", "fail");
                                FirebaseCrash.report(task.getException());
                            }

                            if (firebaseRemoteConfig.getLong(REQUIRED_VERSION_KEY) > deviceVersion) {
                                Dialogs.createVersionUpdateAlertDialog(activity).show();
                            } else {
                                listener.onMeetRequiredVersion();
                            }
                        }
                    });
        } catch (PackageManager.NameNotFoundException e) {
            FirebaseCrash.report(e);
        }

    }
}

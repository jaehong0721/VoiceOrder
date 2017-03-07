package com.rena21c.voiceorder.etc;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

public class PermissionManager {

    public interface PermissionsPermittedListener {
        void onAllPermissionsPermitted();
    }

    private AlertDialog notiDialog;
    private AlertDialog settingForceDialog;

    private static final int REQUST_READ_PHONE_STATE = 500;

    private final Activity activity;
    private String[] permissions;
    private final PermissionsPermittedListener listener;

    boolean isRequestBefore = false;

    public PermissionManager(final Activity activity, final String[] permissions, String permissionRationale, String settingRationale, PermissionsPermittedListener listener) {
        this.activity = activity;
        this.permissions = permissions;
        this.listener = listener;
        notiDialog = new AlertDialog
                .Builder(activity)
                .setCancelable(false)
                .setMessage(permissionRationale)
                .setPositiveButton("다음", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(activity, permissions, REQUST_READ_PHONE_STATE);
                    }
                })
                .setNeutralButton("설정", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        goToSetting();
                    }
                })
                .create();

        /** Never show again을 하나 이상 선택했을 때 보여주는 다이얼로그 (반드시 설정에서 해제 해 주어야 함) */
        settingForceDialog = new AlertDialog
                .Builder(activity)
                .setCancelable(false)
                .setMessage(settingRationale)
                .setNeutralButton("설정", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        goToSetting();
                    }
                })
                .create();
    }

    public void requestPermission() {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            checkPermission();
        } else {
            listener.onAllPermissionsPermitted();
        }
    }

    private void checkPermission() {
        if (!isAllPermitted()) {
            if (!isRequestBefore || existShouldShowRequestPermissionRationale()) {
                isRequestBefore = true;
                notiDialog.show();
            } else { /** 하나 이상 다시 보지 않기를 선택한 경우 */
                settingForceDialog.show();
            }
        } else {
            listener.onAllPermissionsPermitted();
        }
    }

    private void goToSetting() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", activity.getPackageName(), null));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    private boolean existShouldShowRequestPermissionRationale() {
        for (String permission : permissions) {
            /** 처음 실행시 무조건 false를 리턴함. 대신, 처음 실행시 "다시 보지 않기" 체크박스도 보이지 않음.
             * 즉, "이후 보이지 않기" 체크 박스는 한 번이상 거절 한 경우에만 나타남)
             * 다시 보지 않기를 선택한 경우, 무조건 false를 리턴함 */

            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                return true;
            }
        }
        return false;
    }

    private boolean isAllPermitted() {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }

}

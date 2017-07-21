package com.rena21c.voiceorder.etc;

import android.Manifest;
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

    public static PermissionManager newInstance(Activity activity) {
        return new PermissionManager(activity, new String[]{Manifest.permission.READ_PHONE_STATE,
                                                            Manifest.permission.RECORD_AUDIO,
                                                            Manifest.permission.ACCESS_FINE_LOCATION,
                                                            Manifest.permission.CALL_PHONE,
                                                            Manifest.permission.READ_CONTACTS},
                "회원가입을 위한 전화번호,\n납품업체 추천을 위한 위치,\n납품업체와 통화를 위한 전화걸기,\n주문을 위한 녹음 권한을 요청합니다.",
                "앱에서 필요한 권한을 요청을 할 수 없습니다.\n\n" + "서비스를 계속 사용하기 위해서 \"설정\" 버튼을 누르신 후, 권한 탭에서 직접 권한을 허락해 주세요.");
    }

    public interface PermissionsPermittedListener {
        void onAllPermissionsPermitted();
    }

    private AlertDialog notiDialog;
    private AlertDialog settingForceDialog;

    private static final int REQUST_READ_PHONE_STATE = 500;

    private final Activity activity;
    private String[] permissions;

    boolean isRequestBefore = false;

    private PermissionManager(final Activity activity, final String[] permissions, String permissionRationale, String settingRationale) {
        this.activity = activity;
        this.permissions = permissions;
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

    public void requestPermission(PermissionsPermittedListener listener) {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            checkPermission(listener);
        } else {
            listener.onAllPermissionsPermitted();
        }
    }

    private void checkPermission(PermissionsPermittedListener listener) {
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

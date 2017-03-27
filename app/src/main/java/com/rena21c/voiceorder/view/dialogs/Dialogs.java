package com.rena21c.voiceorder.view.dialogs;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;

public class Dialogs {
    public static void showNoInternetConnectivityAlertDialog(final Activity activity, DialogInterface.OnClickListener listener) {
        if (listener == null) listener = new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int which) { }
        };
        new android.support.v7.app.AlertDialog
                .Builder(activity)
                .setCancelable(false)
                .setMessage("인터넷이 연결 되어 있지 않습니다. 연결을 확인 후, 다시 실행해 주세요.")
                .setPositiveButton("종료", listener)
                .create()
                .show();
    }

    public static AlertDialog createVersionUpdateAlertDialog(final Activity activity) {
        AlertDialog blockingDialog = new android.support.v7.app.AlertDialog
                .Builder(activity)
                .setCancelable(false)
                .setMessage("새로운 버전이 업데이트되었습니다. 업데이트 후, 다시 실행해 주세요.")
                .setPositiveButton("업데이트", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("http://play.google.com/store/apps/details?id=com.rena21c.vendor"));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        activity.startActivity(intent);
                        activity.finish();
                    }
                })
                .setNegativeButton("종료", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        activity.finish();
                    }
                })
                .create();

        return blockingDialog;
    }

    public static AlertDialog createNoSupportDeviceDialog(final Activity activity) {
        AlertDialog blockingDialog = new android.support.v7.app.AlertDialog
                .Builder(activity)
                .setCancelable(false)
                .setMessage("죄송합니다. 해당 기기는 지원하지 않습니다.")
                .setPositiveButton("종료", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        activity.finish();
                    }
                })
                .create();

        return blockingDialog;
    }

    public static AlertDialog createPlayServiceUpdateWarningDialog(final Activity activity, final DialogInterface.OnClickListener listener) {
        AlertDialog blockingDialog = new android.support.v7.app.AlertDialog
                .Builder(activity)
                .setCancelable(false)
                .setMessage("로그인이 실패하였습니다. 상태를 확인 후 다시 실행 해 주세요.")
                .setPositiveButton("종료", listener)
                .create();

        return blockingDialog;
    }

    public static void showNoAvailableInternalMemoryDialog(Activity activity, DialogInterface.OnClickListener listener) {
        new android.support.v7.app.AlertDialog
                .Builder(activity)
                .setCancelable(false)
                .setMessage("녹음파일을 저장할 여유공간이 부족합니다. 여유공간 확보 후 다시 시도해주세요.")
                .setPositiveButton("확인", listener)
                .create()
                .show();
    }
}

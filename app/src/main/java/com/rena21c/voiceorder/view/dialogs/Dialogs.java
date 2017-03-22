package com.rena21c.voiceorder.view.dialogs;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

public class Dialogs {
    public static AlertDialog createNoInternetConnectivityAlertDialog(final Activity activity) {
        AlertDialog blockingDialog = new android.support.v7.app.AlertDialog
                .Builder(activity)
                .setCancelable(false)
                .setMessage("인터넷이 연결 되어 있지 않습니다. 연결을 확인 후, 다시 실행해 주세요.")
                .setPositiveButton("종료", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        activity.finish();
                    }
                })
                .create();
        return blockingDialog;
    }
}

package com.rena21c.voiceorder.view.widgets;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

import com.rena21c.voiceorder.App;
import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.etc.AppPreferenceManager;

public class CallButton extends android.support.v7.widget.AppCompatImageView implements View.OnClickListener {

    private String phoneNumber;
    private String name;

    public CallButton(Context context) {
        this(context, null);
    }

    public CallButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        setImageResource(R.drawable.call);
        setOnClickListener(this);
    }

    @Override public void onClick(View v) {
        if (phoneNumber == null || name == null) {
            throw new RuntimeException("you must set both field - phoneNumber, name");
        }

        AppPreferenceManager prefManager = App.getApplication(getContext().getApplicationContext()).getPreferenceManager();
        prefManager.setCallTime(phoneNumber, System.currentTimeMillis());
        prefManager.addCalledVendor(phoneNumber, name);

        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phoneNumber));

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
            Toast.makeText(getContext(), "전화를 하려면 전화 권한을 허용해야 합니다", Toast.LENGTH_SHORT).show();
            return;
        }

        getContext().startActivity(intent);
    }

    public void setCalleeInfo(String phoneNumber, String name) {
        this.phoneNumber = phoneNumber;
        this.name = name;
    }
}

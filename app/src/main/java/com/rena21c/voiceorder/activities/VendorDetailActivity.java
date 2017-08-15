package com.rena21c.voiceorder.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.util.StringUtil;


public class VendorDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_detail);

        String phoneNumber = getIntent().getStringExtra("vendorPhoneNumber");
        phoneNumber = StringUtil.removeSpecialLetter(phoneNumber);

        //정보 로드
        //방문자 수 올리기
        //정보 표시
    }
}

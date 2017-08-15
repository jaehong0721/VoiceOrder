package com.rena21c.voiceorder.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.rena21c.voiceorder.App;
import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.firebase.FirebaseDbManager;
import com.rena21c.voiceorder.firebase.ToastErrorHandlingListener;
import com.rena21c.voiceorder.util.StringUtil;
import com.rena21c.voiceorder.view.actionbar.NavigateBackActionBar;
import com.rena21c.voiceorder.view.components.BusinessInfoContainer;
import com.rena21c.voiceorder.view.components.ContactInfoContainer;


public class VendorDetailActivity extends AppCompatActivity {

    private ContactInfoContainer contactInfoContainer;
    private BusinessInfoContainer businessInfoContainer;
    private FirebaseDbManager dbManager;

    private String phoneNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_detail);

        NavigateBackActionBar.createWithActionBar(this, getSupportActionBar())
                .setBackButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                })
                .setTitle("업체상세");

        phoneNumber = StringUtil.removeSpecialLetter(getIntent().getStringExtra("vendorPhoneNumber"));

        dbManager = App.getApplication(getApplicationContext()).getDbMangaer();

        contactInfoContainer = (ContactInfoContainer)findViewById(R.id.contactInfoContainer);
        businessInfoContainer = (BusinessInfoContainer)findViewById(R.id.businessInfoContainer);
        //정보 로드
        //방문자 수 올리기
        //정보 표시
    }

    @Override protected void onStart() {
        super.onStart();
        dbManager.getVendorContactInfo(phoneNumber, new ToastErrorHandlingListener(this) {
            @Override public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()) return;
                contactInfoContainer.setContactInfo(dataSnapshot);
            }
        });

        dbManager.getVendorBusinessInfo(phoneNumber, new ToastErrorHandlingListener(this) {
            @Override public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()) return;
                businessInfoContainer.setBusinessInfo(dataSnapshot);
            }
        });
    }
}

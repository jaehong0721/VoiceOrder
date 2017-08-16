package com.rena21c.voiceorder.activities;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.rena21c.voiceorder.App;
import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.firebase.FirebaseDbManager;
import com.rena21c.voiceorder.firebase.ToastErrorHandlingListener;
import com.rena21c.voiceorder.util.StringUtil;
import com.rena21c.voiceorder.view.actionbar.NavigateBackActionBar;
import com.rena21c.voiceorder.view.adapters.VendorImageAdapter;
import com.rena21c.voiceorder.view.components.BusinessInfoContainer;
import com.rena21c.voiceorder.view.components.ContactInfoContainer;
import com.rena21c.voiceorder.view.components.ViewPagerWithIndicator;

import java.util.List;


public class VendorDetailActivity extends AppCompatActivity {

    private ContactInfoContainer contactInfoContainer;
    private BusinessInfoContainer businessInfoContainer;
    private ViewPagerWithIndicator imageViewPager;
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
                        ActivityCompat.finishAfterTransition(VendorDetailActivity.this);
                    }
                })
                .setTitle("업체상세");

        phoneNumber = StringUtil.removeSpecialLetter(getIntent().getStringExtra("vendorPhoneNumber"));

        dbManager = App.getApplication(getApplicationContext()).getDbMangaer();

        contactInfoContainer = (ContactInfoContainer)findViewById(R.id.contactInfoContainer);
        businessInfoContainer = (BusinessInfoContainer)findViewById(R.id.businessInfoContainer);
        imageViewPager = (ViewPagerWithIndicator)findViewById(R.id.imageViewPager);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) return;
        ImageView ivCall = (ImageView) contactInfoContainer.findViewById(R.id.ivCall);
        ivCall.setTransitionName("vendor_detail");
    }

    @Override protected void onStart() {
        super.onStart();
        dbManager.getVendorImages(phoneNumber, new ToastErrorHandlingListener(this) {
            @Override public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()) return;
                VendorImageAdapter vendorImageAdapter = new VendorImageAdapter(LayoutInflater.from(VendorDetailActivity.this));
                imageViewPager.setAdapter(vendorImageAdapter);
                vendorImageAdapter.setVendorImages((List)dataSnapshot.getValue());
                imageViewPager.setVisibility(View.VISIBLE);
            }
        });

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

        dbManager.getVisitCount(phoneNumber, new ToastErrorHandlingListener(this) {
            @Override public void onDataChange(DataSnapshot dataSnapshot) {
                long visitCount = 0;
                if(dataSnapshot.exists())
                    visitCount = (Long)dataSnapshot.getValue();
                dbManager.setVisitCount(phoneNumber, visitCount + 1);
            }
        });
    }
}

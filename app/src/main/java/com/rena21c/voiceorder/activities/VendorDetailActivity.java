package com.rena21c.voiceorder.activities;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.google.firebase.database.DataSnapshot;
import com.rena21c.voiceorder.App;
import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.firebase.FirebaseDbManager;
import com.rena21c.voiceorder.firebase.ToastErrorHandlingListener;
import com.rena21c.voiceorder.network.FileTransferUtil;
import com.rena21c.voiceorder.util.StringUtil;
import com.rena21c.voiceorder.view.actionbar.NavigateBackActionBar;
import com.rena21c.voiceorder.view.adapters.VendorImageAdapter;
import com.rena21c.voiceorder.view.components.BusinessInfoContainer;
import com.rena21c.voiceorder.view.components.ContactInfoContainer;
import com.rena21c.voiceorder.view.components.ViewPagerWithIndicator;

import java.util.ArrayList;
import java.util.List;


public class VendorDetailActivity extends AppCompatActivity {

    private ContactInfoContainer contactInfoContainer;
    private BusinessInfoContainer businessInfoContainer;
    private ViewPagerWithIndicator imageViewPager;
    private FirebaseDbManager dbManager;

    private String phoneNumber;

    private AmazonS3 s3;
    private String s3BucketName;
    private String s3Address;

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

        s3 = FileTransferUtil.getS3Client(this);
        s3BucketName = getResources().getString(R.string.s3_image_bucket_name);
        s3Address = getResources().getString(R.string.s3_base_address) + "/" + s3BucketName;

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

        setVendorImage();

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

    private void setVendorImage() {
        final Handler handler = new Handler(getMainLooper());
        final List<String> imageUrls = new ArrayList<>();
        new Thread(new Runnable() {
            @Override public void run() {
                for(final S3ObjectSummary file : s3.listObjects(s3BucketName, "image/" + phoneNumber + "/").getObjectSummaries()) {
                    if(file.getKey().equals("image/" + phoneNumber)) continue;
                    imageUrls.add(s3Address + "/" + file.getKey());
                }
                if(imageUrls.size() == 0) return;
                handler.post(new Runnable() {
                    @Override public void run() {
                        VendorImageAdapter vendorImageAdapter = new VendorImageAdapter(LayoutInflater.from(VendorDetailActivity.this));
                        imageViewPager.setAdapter(vendorImageAdapter);
                        vendorImageAdapter.setVendorImages(imageUrls);
                        imageViewPager.setVisibility(View.VISIBLE);
                    }
                });
            }
        }).start();
    }
}

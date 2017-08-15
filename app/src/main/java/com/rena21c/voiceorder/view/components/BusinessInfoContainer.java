package com.rena21c.voiceorder.view.components;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;
import com.rena21c.voiceorder.R;

public class BusinessInfoContainer extends FrameLayout {

    private TextView tvPartnerNum;
    private TextView tvOrderNum;
    private TextView tvAge;

    private FlexboxLayout deliveryAreasLayout;
    private TextView tvClosedDay;
    private TextView tvDeliveryTime;
    private TextView tvBusinessLicenseNumber;


    public BusinessInfoContainer(@NonNull Context context) {
        this(context, null);
    }

    public BusinessInfoContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        inflate(context, R.layout.component_vendor_business_info, this);

        tvPartnerNum = (TextView) findViewById(R.id.tvPartnerNum);
        tvOrderNum = (TextView) findViewById(R.id.tvOrderNum);
        tvAge = (TextView) findViewById(R.id.tvAge);
        tvClosedDay = (TextView) findViewById(R.id.tvClosedDay);
        tvDeliveryTime = (TextView) findViewById(R.id.tvDeliveryTime);
        tvBusinessLicenseNumber = (TextView) findViewById(R.id.tvBusinessLicenseNumber);
        deliveryAreasLayout = (FlexboxLayout) findViewById(R.id.deliveryAreasLayout);
    }
}

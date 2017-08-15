package com.rena21c.voiceorder.view.components;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;
import com.google.firebase.database.DataSnapshot;
import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.model.BusinessInfoData;
import com.rena21c.voiceorder.util.TransformDataUtil;
import com.rena21c.voiceorder.view.widgets.DeliveryAreaView;

public class BusinessInfoContainer extends FrameLayout {

    private TextView tvPartnerNum;
    private TextView tvOrderNum;
    private TextView tvAge;

    private FlexboxLayout deliveryAreasLayout;
    private TextView tvMajorItems;
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
        tvMajorItems = (TextView) findViewById(R.id.tvMajorItems);
        tvClosedDay = (TextView) findViewById(R.id.tvClosedDay);
        tvDeliveryTime = (TextView) findViewById(R.id.tvDeliveryTime);
        tvBusinessLicenseNumber = (TextView) findViewById(R.id.tvBusinessLicenseNumber);
        deliveryAreasLayout = (FlexboxLayout) findViewById(R.id.deliveryAreasLayout);
    }

    public void setBusinessInfo(DataSnapshot dataSnapshot) {
        BusinessInfoData businessInfoData = dataSnapshot.getValue(BusinessInfoData.class);

        tvPartnerNum.setText(String.valueOf(businessInfoData.partnerNum)+"개");
        tvOrderNum.setText(String.valueOf(businessInfoData.orderNum)+"회");
        tvAge.setText(String.valueOf(businessInfoData.age)+"년");

        tvMajorItems.setText(TransformDataUtil.makeMajorItemsString(businessInfoData.majorItems));

        tvClosedDay.setText(businessInfoData.closedDay);
        tvDeliveryTime.setText(businessInfoData.deliveryTime);
        tvBusinessLicenseNumber.setText(businessInfoData.businessLicenseNumber);

        if(businessInfoData.deliveryAreas == null) return;
        deliveryAreasLayout.removeAllViews();
        for (String deliveryArea : businessInfoData.deliveryAreas) {
            DeliveryAreaView deliveryAreaView = new DeliveryAreaView(getContext());
            deliveryAreaView.setArea(deliveryArea);
            deliveryAreasLayout.addView(deliveryAreaView);
        }
    }
}

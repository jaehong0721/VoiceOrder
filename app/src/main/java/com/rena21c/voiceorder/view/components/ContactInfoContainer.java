package com.rena21c.voiceorder.view.components;


import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.model.VendorInfo;
import com.rena21c.voiceorder.view.widgets.CallButton;

@SuppressWarnings("MissingPermission")
public class ContactInfoContainer extends FrameLayout {

    private TextView tvName;
    private TextView tvAddress;
    private CallButton ivCall;

    public ContactInfoContainer(Context context) {
        this(context,null);
    }

    public ContactInfoContainer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        inflate(context, R.layout.component_vendor_contact_info,this);
    }

    @Override protected void onFinishInflate() {
        super.onFinishInflate();

        tvName = (TextView)findViewById(R.id.tvName);
        tvAddress = (TextView)findViewById(R.id.tvAddress);
        ivCall = (CallButton)findViewById(R.id.ivCall);
    }

    public void setContactInfo(final VendorInfo vendorInfo) {
        tvName.setText(vendorInfo.vendorName);
        tvAddress.setText(vendorInfo.address);
        ivCall.setCalleeInfo(vendorInfo.phoneNumber, vendorInfo.vendorName);
    }
}

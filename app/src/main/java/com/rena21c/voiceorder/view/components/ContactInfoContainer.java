package com.rena21c.voiceorder.view.components;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.model.VendorInfo;

@SuppressWarnings("MissingPermission")
public class ContactInfoContainer extends FrameLayout {

    private TextView tvName;
    private TextView tvAddress;
    private ImageView ivCall;

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
        ivCall = (ImageView)findViewById(R.id.ivCall);
    }

    public void setContactInfo(final VendorInfo vendorInfo) {
        tvName.setText(vendorInfo.vendorName);
        tvAddress.setText(vendorInfo.address);
        ivCall.setOnClickListener(new OnClickListener() {
            @Override public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + vendorInfo.phoneNumber));
                getContext().startActivity(intent);
            }
        });
    }
}

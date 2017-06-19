package com.rena21c.voiceorder.viewmodel;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.view.widgets.AddPartnerButton;

public class MyPartnerGuideViewModel {

    public View getView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_my_partner_guide, null);
        AddPartnerButton addPartnerButton = (AddPartnerButton) view.findViewById(R.id.btnAddPartner);

        addPartnerButton.setAddPartnerListener((AddPartnerButton.AddPartnerListener)context);

        return view;
    }
}

package com.rena21c.voiceorder.viewmodel;


import android.view.LayoutInflater;
import android.view.View;

import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.view.widgets.AddPartnerButton;

public class MyPartnerGuideViewModel {

    private final AddPartnerButton.AddPartnerListener addPartnerListener;

    public MyPartnerGuideViewModel(AddPartnerButton.AddPartnerListener addPartnerListener) {
        this.addPartnerListener = addPartnerListener;
    }

    public View getView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.layout_my_partner_guide, null);
        AddPartnerButton addPartnerButton = (AddPartnerButton) view.findViewById(R.id.btnAddPartner);

        if(addPartnerListener != null) addPartnerButton.setAddPartnerListener(addPartnerListener);

        return view;
    }
}

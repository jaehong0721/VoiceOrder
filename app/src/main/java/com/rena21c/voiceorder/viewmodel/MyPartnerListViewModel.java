package com.rena21c.voiceorder.viewmodel;


import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.activities.MyPartnerActivity;
import com.rena21c.voiceorder.etc.TimeSortComparator;
import com.rena21c.voiceorder.model.Partner;
import com.rena21c.voiceorder.view.DividerItemDecoration;
import com.rena21c.voiceorder.view.adapters.MyPartnersRecyclerViewAdapter;
import com.rena21c.voiceorder.view.widgets.AddPartnerButton;
import com.rena21c.voiceorder.view.widgets.CallDialogFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MyPartnerListViewModel implements View.OnAttachStateChangeListener,
                                                MyPartnersRecyclerViewAdapter.ItemClickListener,
                                                CallDialogFragment.CallDialogClickListener {

    private View view;

    private AddPartnerButton.AddPartnerListener addPartnerListener;
    private RecyclerView rvMyPartner;

    private MyPartnersRecyclerViewAdapter myPartnersRecyclerViewAdapter;

    private HashMap<String, String> calledVendors;
    private HashMap<String, String> myPartners;
    private final HashMap<String, Long> callTimeMap;

    private CallDialogFragment callDialogFragment;

    public MyPartnerListViewModel(AddPartnerButton.AddPartnerListener addPartnerListener,
                                  HashMap<String, String> calledVendors,
                                  HashMap<String, String> myPartners,
                                  HashMap<String, Long> callTimeMap) {

        this.addPartnerListener = addPartnerListener;
        this.calledVendors = calledVendors;
        this.myPartners = myPartners;
        this.callTimeMap = callTimeMap;
    }

    public View getView(Context context) {
        view = LayoutInflater.from(context).inflate(R.layout.layout_my_partner_list, null);

        AddPartnerButton addPartnerButton = (AddPartnerButton) view.findViewById(R.id.btnAddPartner);
        addPartnerButton.setAddPartnerListener(addPartnerListener);

        rvMyPartner = (RecyclerView) view.findViewById(R.id.rvMyPartner);

        myPartnersRecyclerViewAdapter = new MyPartnersRecyclerViewAdapter(myPartners.size());
        myPartnersRecyclerViewAdapter.setItemClickListener(this);

        rvMyPartner.setLayoutManager(new LinearLayoutManager(context));
        rvMyPartner.addItemDecoration(new DividerItemDecoration(context, R.drawable.shape_divider_recycler_view));
        rvMyPartner.setAdapter(myPartnersRecyclerViewAdapter);

        view.addOnAttachStateChangeListener(this);
        return view;
    }

    @Override public void onViewAttachedToWindow(View v) {
        if(v != view) return;

        ArrayList<Partner> partners = new ArrayList<>();
        ArrayList<Partner> calledVendorsOnRecommend = new ArrayList<>();

        TimeSortComparator timeSortComparator = new TimeSortComparator();

        for(Map.Entry<String,String> entry : myPartners.entrySet()) {
            long callTime = callTimeMap.get(entry.getKey()) != null ? callTimeMap.get(entry.getKey()) : 0;
            partners.add(new Partner(entry.getKey(), entry.getValue(), callTime));
        }
        Collections.sort(partners, timeSortComparator);

        for(Map.Entry<String,String> entry : calledVendors.entrySet()) {
            long callTime = callTimeMap.get(entry.getKey()) != null ? callTimeMap.get(entry.getKey()) : 0;
            calledVendorsOnRecommend.add(new Partner(entry.getKey(), entry.getValue(), callTime));
        }
        Collections.sort(calledVendorsOnRecommend, timeSortComparator);

        partners.addAll(calledVendorsOnRecommend);
        myPartnersRecyclerViewAdapter.setPartners(partners);
    }

    @Override public void onViewDetachedFromWindow(View v) {}

    @Override public void onItemClick(String phoneNumber, String vendorName) {
        callDialogFragment = CallDialogFragment.newInstance(phoneNumber, vendorName);
        callDialogFragment.setCallDialogClickListener(this);
        callDialogFragment.show(((FragmentActivity)view.getContext()).getSupportFragmentManager(), "dialog");
    }

    @Override public void onClickCall(String phoneNumber) {
        callDialogFragment.dismiss();
        ((MyPartnerActivity)view.getContext()).moveToCallApp(phoneNumber);
    }
}


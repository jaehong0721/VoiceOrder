package com.rena21c.voiceorder.viewmodel;


import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.GenericTypeIndicator;
import com.rena21c.voiceorder.R;
import com.rena21c.voiceorder.activities.MyPartnerActivity;
import com.rena21c.voiceorder.etc.AppPreferenceManager;
import com.rena21c.voiceorder.etc.TimeSortComparator;
import com.rena21c.voiceorder.firebase.FirebaseDbManager;
import com.rena21c.voiceorder.firebase.ToastErrorHandlingListener;
import com.rena21c.voiceorder.model.DisplayedMyPartner;
import com.rena21c.voiceorder.view.DividerItemDecoration;
import com.rena21c.voiceorder.view.adapters.MyPartnersRecyclerViewAdapter;
import com.rena21c.voiceorder.view.widgets.AddPartnerButton;
import com.rena21c.voiceorder.view.widgets.CallDialogFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public class MyPartnerListViewModel implements  View.OnAttachStateChangeListener,
                                                MyPartnersRecyclerViewAdapter.ItemClickListener,
                                                CallDialogFragment.CallDialogClickListener {

    public interface DataSetSizeChangedListener {
        void onDataSetSizeChanged(int size);
    }

    private DataSetSizeChangedListener dataSetSizeChangedListener;

    private View view;

    private AddPartnerButton.AddPartnerListener addPartnerListener;
    private final AppPreferenceManager appPreferenceManager;
    private final FirebaseDbManager dbManager;

    private TimeSortComparator timeSortComparator;

    private MyPartnersRecyclerViewAdapter myPartnersRecyclerViewAdapter;

    private CallDialogFragment callDialogFragment;

    private HashMap<String, String> calledVendorMap;
    private HashMap<String, Long> callTimeMap;
    private ArrayList<DisplayedMyPartner> displayedMyPartners;

    private Observer callTimeMapObserver;
    private  Observer calledVendorMapObserver;
    private ToastErrorHandlingListener dbListener;

    private int numberOfMyPartners;

    private int clickedItemPosition;

    public MyPartnerListViewModel(AddPartnerButton.AddPartnerListener addPartnerListener,
                                  DataSetSizeChangedListener dataSetSizeChangedListener,
                                  AppPreferenceManager appPreferenceManager,
                                  FirebaseDbManager dbManager) {

        this.addPartnerListener = addPartnerListener;
        this.dataSetSizeChangedListener = dataSetSizeChangedListener;
        this.appPreferenceManager = appPreferenceManager;
        this.dbManager = dbManager;

        timeSortComparator = new TimeSortComparator();

        initDataSet(appPreferenceManager);
    }

    private void initDataSet(AppPreferenceManager appPreferenceManager) {
        this.displayedMyPartners = new ArrayList<>();
        this.callTimeMap = appPreferenceManager.getAllCallTime();
        this.calledVendorMap = appPreferenceManager.getCalledVendors();

        if(calledVendorMap.size() == 0) return;

        for(Map.Entry<String,String> entry : calledVendorMap.entrySet()) {
            long callTime = callTimeMap.get(entry.getKey()) != null ? callTimeMap.get(entry.getKey()) : 0;
            displayedMyPartners.add(new DisplayedMyPartner(entry.getValue(), entry.getKey(), callTime));
        }
        Collections.sort(displayedMyPartners, timeSortComparator);
    }

    public View getView(Context context) {
        view = LayoutInflater.from(context).inflate(R.layout.layout_my_partner_list, null);

        AddPartnerButton addPartnerButton = (AddPartnerButton) view.findViewById(R.id.btnAddPartner);
        addPartnerButton.setAddPartnerListener(addPartnerListener);

        RecyclerView rvMyPartner = (RecyclerView) view.findViewById(R.id.rvMyPartner);

        myPartnersRecyclerViewAdapter = new MyPartnersRecyclerViewAdapter();
        myPartnersRecyclerViewAdapter.setDisplayedMyPartners(displayedMyPartners);
        myPartnersRecyclerViewAdapter.setItemClickListener(this);

        rvMyPartner.setLayoutManager(new LinearLayoutManager(context));
        rvMyPartner.addItemDecoration(new DividerItemDecoration(context, R.drawable.shape_divider_recycler_view));
        rvMyPartner.setAdapter(myPartnersRecyclerViewAdapter);

        view.addOnAttachStateChangeListener(this);
        return view;
    }

    @Override public void onViewAttachedToWindow(View v) {
        callTimeMapObserver = new Observer() {

            @Override public void update(Observable o, Object arg) {
                if(!(arg.equals("callTimes"))) return;

                callTimeMap = ((AppPreferenceManager)o).getAllCallTime();
            }
        };

        calledVendorMapObserver = new Observer() {

            @Override public void update(Observable o, Object arg) {
                if(!(arg.equals("calledVendors"))) return;

                if(displayedMyPartners.size() != 0){
                    for(int i = displayedMyPartners.size()-1; i>=numberOfMyPartners; i--) {
                        displayedMyPartners.remove(i);
                    }
                }

                calledVendorMap = ((AppPreferenceManager)o).getCalledVendors();

                if(calledVendorMap.size() != 0) {
                    ArrayList<DisplayedMyPartner> calledVendorsOnRecommend = new ArrayList<>();

                    for(Map.Entry<String,String> entry : calledVendorMap.entrySet()) {
                        long callTime = callTimeMap.get(entry.getKey()) != null ? callTimeMap.get(entry.getKey()) : 0;
                        calledVendorsOnRecommend.add(new DisplayedMyPartner(entry.getValue(), entry.getKey(), callTime));
                    }

                    Collections.sort(calledVendorsOnRecommend, timeSortComparator);
                    displayedMyPartners.addAll(numberOfMyPartners, calledVendorsOnRecommend);
                }
                myPartnersRecyclerViewAdapter.setDisplayedMyPartners(displayedMyPartners);
                dataSetSizeChangedListener.onDataSetSizeChanged(displayedMyPartners.size());
            }
        };

        dbListener = new ToastErrorHandlingListener(view.getContext()) {

            @Override public void onDataChange(DataSnapshot dataSnapshot) {
                if(numberOfMyPartners != 0) {
                    for(int i = numberOfMyPartners-1; i>=0; i--) {
                        displayedMyPartners.remove(i);
                    }
                    numberOfMyPartners = 0;
                }

                if(dataSnapshot.exists()) {
                    numberOfMyPartners = (int)dataSnapshot.getChildrenCount();

                    ArrayList<DisplayedMyPartner> alreadyStoredMyPartners = new ArrayList<>();

                    GenericTypeIndicator partnerMapType = new GenericTypeIndicator<HashMap<String, DisplayedMyPartner>>() {};
                    HashMap<String, DisplayedMyPartner> partnerMap = (HashMap)dataSnapshot.getValue(partnerMapType);


                    for(Map.Entry<String,DisplayedMyPartner> entry : partnerMap.entrySet() ) {

                        String phoneNumber = entry.getKey();

                        entry.getValue().callTime = (callTimeMap.get(phoneNumber) != null) ? callTimeMap.get(phoneNumber) : 0;
                        entry.getValue().phoneNumber = phoneNumber;

                        alreadyStoredMyPartners.add(entry.getValue());
                    }
                    Collections.sort(displayedMyPartners, timeSortComparator);
                    displayedMyPartners.addAll(0, alreadyStoredMyPartners);
                }
                myPartnersRecyclerViewAdapter.setNumberOfMyPartners(numberOfMyPartners);
                myPartnersRecyclerViewAdapter.setDisplayedMyPartners(displayedMyPartners);
                dataSetSizeChangedListener.onDataSetSizeChanged(displayedMyPartners.size());
            }
        };

        appPreferenceManager.addObserver(callTimeMapObserver);
        appPreferenceManager.addObserver(calledVendorMapObserver);
        dbManager.subscribeMyPartner(appPreferenceManager.getPhoneNumber(), dbListener);
    }

    @Override public void onViewDetachedFromWindow(View v) {
        appPreferenceManager.deleteObserver(callTimeMapObserver);
        appPreferenceManager.deleteObserver(calledVendorMapObserver);
        dbManager.cancelSubscriptionMyPartner(appPreferenceManager.getPhoneNumber(), dbListener);
    }

    @Override public void onItemClick(int position, String phoneNumber, String vendorName) {
        clickedItemPosition = position;

        callDialogFragment = CallDialogFragment.newInstance(phoneNumber, vendorName);
        callDialogFragment.setCallDialogClickListener(this);
        callDialogFragment.show(((FragmentActivity)view.getContext()).getSupportFragmentManager(), "dialog");
    }

    @Override public void onClickCall(String phoneNumber) {
        callDialogFragment.dismiss();

        long currentTimeMillis = System.currentTimeMillis();

        appPreferenceManager.setCallTime(phoneNumber, currentTimeMillis);

        if(clickedItemPosition == 0 || clickedItemPosition == numberOfMyPartners) {
            displayedMyPartners.get(clickedItemPosition).callTime = currentTimeMillis;
            int changePosition = clickedItemPosition < numberOfMyPartners ? clickedItemPosition : clickedItemPosition+1;
            myPartnersRecyclerViewAdapter.notifyItemChanged(changePosition);
            ((MyPartnerActivity)view.getContext()).moveToCallApp(phoneNumber);
            return;
        }

        DisplayedMyPartner displayedMyPartner = displayedMyPartners.remove(clickedItemPosition);
        int removePosition = clickedItemPosition < numberOfMyPartners ? clickedItemPosition : clickedItemPosition+1;
        myPartnersRecyclerViewAdapter.notifyItemRemoved(removePosition);

        int newPosition = clickedItemPosition < numberOfMyPartners ? 0 : numberOfMyPartners;
        displayedMyPartner.callTime = currentTimeMillis;
        displayedMyPartners.add(newPosition, displayedMyPartner);
        myPartnersRecyclerViewAdapter.notifyItemInserted(newPosition);

        ((MyPartnerActivity)view.getContext()).moveToCallApp(phoneNumber);
    }

    @Override public void onClickVoiceOrder() {
        callDialogFragment.dismiss();
        ((MyPartnerActivity)view.getContext()).moveToVoiceOrderTab();
    }
}


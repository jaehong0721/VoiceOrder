package com.rena21c.voiceorder.network;

import android.content.Context;
import android.renderscript.Sampler;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.rena21c.voiceorder.etc.PreferenceManager;
import com.rena21c.voiceorder.model.VendorInfo;
import com.rena21c.voiceorder.model.VoiceRecord;

import java.util.HashMap;

public class FirebaseDbManager {

    private static final String INFO = "info";
    private static final String FCM_ID = "fcmId";
    private static final String RECORDED_ORDERS = "recordedOrders";
    private static final String RESTAURANTS = "restaurants";

    private final Context context;
    private final FirebaseDatabase instance;

    public FirebaseDbManager(Context context, FirebaseDatabase instance) {
        this.context = context;
        this.instance = instance;
    }

    public void getFcmToken(String phoneNumber, final String fcmToken, OnCompleteListener listener) {
        FirebaseDatabase.getInstance().getReference().child(RESTAURANTS)
                .child(phoneNumber)
                .child(INFO)
                .child(FCM_ID)
                .setValue(fcmToken)
                .addOnCompleteListener(listener);
    }

    public void getRecordOrder(String phoneNumber, ValueEventListener listener) {
        //오퍼레이터 접수 전 데이터 로드
        FirebaseDatabase.getInstance().getReference().child(RESTAURANTS)
                .child(phoneNumber)
                .child(RECORDED_ORDERS)
                .addListenerForSingleValueEvent(listener);
    }

    public void getAcceptedOrder(String phoneNumber, ValueEventListener listener) {
        FirebaseDatabase.getInstance().getReference().child("orders")
                .child(RESTAURANTS)
                .orderByKey()
                .startAt(phoneNumber + "_00000000000000")
                .endAt(phoneNumber + "_99999999999999")
                .addListenerForSingleValueEvent(listener);
    }

    public void getVendorInfo(String vendorPhoneNumber, ValueEventListener listener) {
        FirebaseDatabase.getInstance().getReference().child("vendors")
                .child(vendorPhoneNumber)
                .child("info")
                .addListenerForSingleValueEvent(listener);
    }
}

package com.rena21c.voiceorder.firebase;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rena21c.voiceorder.pojo.MyPartner;

import java.util.HashMap;
import java.util.Map;

public class FirebaseDbManager {

    private static final String INFO = "info";
    private static final String FCM_ID = "fcmId";
    private static final String RECORDED_ORDERS = "recordedOrders";
    private static final String ORDERS = "orders";
    private static final String RESTAURANTS = "restaurants";
    private static final String VENDORS = "vendors";

    private final FirebaseDatabase instance;

    public FirebaseDbManager(FirebaseDatabase instance) {
        this.instance = instance;
    }

    public void getFcmToken(String phoneNumber, final String fcmToken, SimpleAuthListener listener) {
        instance.getReference().child(RESTAURANTS)
                .child(phoneNumber)
                .child(INFO)
                .child(FCM_ID)
                .setValue(fcmToken)
                .addOnSuccessListener(listener)
                .addOnFailureListener(listener);
    }

    public void getRecordedOrder(String phoneNumber, ValueEventListener listener) {
        //오퍼레이터 접수 전 데이터 로드
        instance.getReference().child(RESTAURANTS)
                .child(phoneNumber)
                .child(RECORDED_ORDERS)
                .orderByKey()
                .startAt(phoneNumber + "_00000000000000")
                .endAt(phoneNumber + "_99999999999999")
                .addListenerForSingleValueEvent(listener);
    }

    public Query subscribeRecordedOrder(String phoneNumber, ChildEventListener listener) {
        Query query  = instance.getReference().child(RESTAURANTS)
                .child(phoneNumber)
                .child(RECORDED_ORDERS)
                .orderByKey()
                .startAt(phoneNumber + "_00000000000000")
                .endAt(phoneNumber + "_99999999999999");

                query.addChildEventListener(listener);
        return query;
    }

    public void removeRecordedOrder(String phoneNumber, String key) {
        instance.getReference().child(RESTAURANTS)
                .child(phoneNumber)
                .child(RECORDED_ORDERS)
                .child(key)
                .removeValue();
    }

    public Query subscribeAcceptedOrder(String phoneNumber, ChildEventListener listener) {
        Query query = instance.getReference().child(ORDERS)
                .child(RESTAURANTS)
                .orderByKey()
                .startAt(phoneNumber + "_00000000000000")
                .endAt(phoneNumber + "_99999999999999");
        query.addChildEventListener(listener);
        return query;
    }

    public void getVendorInfo(String vendorPhoneNumber, ValueEventListener listener) {
        instance.getReference().child("vendors")
                .child(vendorPhoneNumber)
                .child("info")
                .addListenerForSingleValueEvent(listener);
    }

    public void addFileName(String phoneNumber, final String fileName, OnCompleteListener listener) {
        instance.getReference().child(RESTAURANTS)
                .child(phoneNumber)
                .child("recordedOrders")
                .child(fileName)
                .setValue(false)
                .addOnCompleteListener(listener);
    }

    public DatabaseReference subscribeMyPartner(String phoneNumber, ValueEventListener listener) {
        DatabaseReference reference = instance.getReference()
                                        .child(RESTAURANTS)
                                        .child(phoneNumber)
                                        .child(VENDORS);
        reference.addValueEventListener(listener);
        return reference;
    }

    public void cancelSubscriptionMyPartner(String phoneNumber, ToastErrorHandlingListener dbListener) {
        instance.getReference()
                .child(RESTAURANTS)
                .child(phoneNumber)
                .child(VENDORS)
                .removeEventListener(dbListener);
    }

    public void getMyPartnersOnce(String phoneNumber, ToastErrorHandlingListener listener) {
        instance.getReference()
                .child(RESTAURANTS)
                .child(phoneNumber)
                .child(VENDORS)
                .addListenerForSingleValueEvent(listener);
    }

    public void uploadMyPartner(String phoneNumber, HashMap<String, MyPartner> myPartnerMap, OnCompleteListener listener) {
        instance.getReference()
                .child(RESTAURANTS)
                .child(phoneNumber)
                .child(VENDORS)
                .setValue(myPartnerMap)
                .addOnCompleteListener(listener);
    }

    public void getAllVendors(ToastErrorHandlingListener listener) {
        instance.getReference()
                .child(VENDORS)
                .addListenerForSingleValueEvent(listener);
    }

    public void updateVendors(Map<String, Object> uploadPathMap, DatabaseReference.CompletionListener listener) {
        instance.getReference()
                .child(VENDORS)
                .updateChildren(uploadPathMap, listener);
    }
}

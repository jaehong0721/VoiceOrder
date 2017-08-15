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
    private static final String SIGN_UP_TIME = "signUpTime";
    private static final String RESTAURANT_NAME = "restaurantName";

    private final FirebaseDatabase instance;

    public FirebaseDbManager(FirebaseDatabase instance) {
        this.instance = instance;
    }

    public void setFcmToken(String phoneNumber, final String fcmToken, SimpleAuthListener listener) {
        getKeepSyncedRootRef()
                .child(RESTAURANTS)
                .child(phoneNumber)
                .child(INFO)
                .child(FCM_ID)
                .setValue(fcmToken)
                .addOnSuccessListener(listener)
                .addOnFailureListener(listener);
    }

    public Query subscribeRecordedOrder(String phoneNumber, ChildEventListener listener) {
        Query query  = getKeepSyncedRootRef()
                .child(RESTAURANTS)
                .child(phoneNumber)
                .child(RECORDED_ORDERS)
                .orderByKey()
                .startAt(phoneNumber + "_00000000000000")
                .endAt(phoneNumber + "_99999999999999")
                .limitToLast(10);

                query.addChildEventListener(listener);
        return query;
    }

    public void removeRecordedOrder(String phoneNumber, String key) {
        getKeepSyncedRootRef()
                .child(RESTAURANTS)
                .child(phoneNumber)
                .child(RECORDED_ORDERS)
                .child(key)
                .removeValue();
    }

    public Query subscribeAcceptedOrder(String phoneNumber, ChildEventListener listener) {
        Query query = getKeepSyncedRootRef()
                .child(ORDERS)
                .child(RESTAURANTS)
                .orderByKey()
                .startAt(phoneNumber + "_00000000000000")
                .endAt(phoneNumber + "_99999999999999");
        query.addChildEventListener(listener);
        return query;
    }

    public void getVendorName(String restaurantPhoneNumber, String vendorPhoneNumber, ValueEventListener listener) {
        getKeepSyncedRootRef()
                .child(RESTAURANTS)
                .child(restaurantPhoneNumber)
                .child(VENDORS)
                .child(vendorPhoneNumber)
                .child("name")
                .addListenerForSingleValueEvent(listener);
    }

    public void addFileName(String phoneNumber, final String fileName, OnCompleteListener listener) {
        getKeepSyncedRootRef()
                .child(RESTAURANTS)
                .child(phoneNumber)
                .child("recordedOrders")
                .child(fileName)
                .setValue(false)
                .addOnCompleteListener(listener);
    }

    public DatabaseReference subscribeMyPartner(String phoneNumber, ValueEventListener listener) {
        DatabaseReference reference = getKeepSyncedRootRef()
                                        .child(RESTAURANTS)
                                        .child(phoneNumber)
                                        .child(VENDORS);
        reference.addValueEventListener(listener);
        return reference;
    }

    public void cancelSubscriptionMyPartner(String phoneNumber, ToastErrorHandlingListener dbListener) {
        getKeepSyncedRootRef()
                .child(RESTAURANTS)
                .child(phoneNumber)
                .child(VENDORS)
                .removeEventListener(dbListener);
    }

    public void getMyPartnersOnce(String phoneNumber, ToastErrorHandlingListener listener) {
        getKeepSyncedRootRef()
                .child(RESTAURANTS)
                .child(phoneNumber)
                .child(VENDORS)
                .addListenerForSingleValueEvent(listener);
    }

    public void uploadMyPartner(String phoneNumber, HashMap<String, MyPartner> myPartnerMap, OnCompleteListener listener) {
        getKeepSyncedRootRef()
                .child(RESTAURANTS)
                .child(phoneNumber)
                .child(VENDORS)
                .setValue(myPartnerMap)
                .addOnCompleteListener(listener);
    }

    public void getAllVendors(ToastErrorHandlingListener listener) {
        getKeepSyncedRootRef()
                .child(VENDORS)
                .addListenerForSingleValueEvent(listener);
    }

    public void updateVendors(Map<String, Object> uploadPathMap, DatabaseReference.CompletionListener listener) {
        getKeepSyncedRootRef()
                .child(VENDORS)
                .updateChildren(uploadPathMap, listener);
    }

    public void checkUserAlreadyCreated(String phoneNumber, ToastErrorHandlingListener listener) {
        getKeepSyncedRootRef()
                .child(RESTAURANTS)
                .child(phoneNumber)
                .child(INFO)
                .child(SIGN_UP_TIME)
                .addListenerForSingleValueEvent(listener);
    }

    public void setInitialSignUpTime(String phoneNumber, String signUpTime, SimpleAuthListener listener) {
        getKeepSyncedRootRef()
                .child(RESTAURANTS)
                .child(phoneNumber)
                .child(INFO)
                .child(SIGN_UP_TIME)
                .setValue(signUpTime)
                .addOnSuccessListener(listener)
                .addOnFailureListener(listener);
    }

    public void getRestaurantName(String phoneNumber, ToastErrorHandlingListener listener) {
        getKeepSyncedRootRef()
                .child(RESTAURANTS)
                .child(phoneNumber)
                .child(INFO)
                .child(RESTAURANT_NAME)
                .addListenerForSingleValueEvent(listener);
    }

    private DatabaseReference getKeepSyncedRootRef() {
        DatabaseReference rootRef = instance.getReference();
        rootRef.keepSynced(true);
        return rootRef;
    }
}

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
    private static final String BUSINESS_INFO = "businessInfo";

    private final FirebaseDatabase instance;

    public FirebaseDbManager(FirebaseDatabase instance) {
        this.instance = instance;
        this.instance.setPersistenceEnabled(true);
        this.instance.getReference().keepSynced(true);
    }

    public void setFcmToken(String phoneNumber, final String fcmToken, SimpleAuthListener listener) {
        getRootRef()
                .child(RESTAURANTS)
                .child(phoneNumber)
                .child(INFO)
                .child(FCM_ID)
                .setValue(fcmToken)
                .addOnSuccessListener(listener)
                .addOnFailureListener(listener);
    }

    public Query subscribeRecordedOrder(String phoneNumber, ChildEventListener listener) {
        Query query  = getRootRef()
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
        getRootRef()
                .child(RESTAURANTS)
                .child(phoneNumber)
                .child(RECORDED_ORDERS)
                .child(key)
                .removeValue();
    }

    public Query subscribeAcceptedOrder(String phoneNumber, ChildEventListener listener) {
        Query query = getRootRef()
                .child(ORDERS)
                .child(RESTAURANTS)
                .orderByKey()
                .startAt(phoneNumber + "_00000000000000")
                .endAt(phoneNumber + "_99999999999999");
        query.addChildEventListener(listener);
        return query;
    }

    public void getVendorName(String restaurantPhoneNumber, String vendorPhoneNumber, ValueEventListener listener) {
        getRootRef()
                .child(RESTAURANTS)
                .child(restaurantPhoneNumber)
                .child(VENDORS)
                .child(vendorPhoneNumber)
                .child("name")
                .addListenerForSingleValueEvent(listener);
    }

    public void addFileName(String phoneNumber, final String fileName, OnCompleteListener listener) {
        getRootRef()
                .child(RESTAURANTS)
                .child(phoneNumber)
                .child("recordedOrders")
                .child(fileName)
                .setValue(false)
                .addOnCompleteListener(listener);
    }

    public DatabaseReference subscribeMyPartner(String phoneNumber, ValueEventListener listener) {
        DatabaseReference reference = getRootRef()
                                        .child(RESTAURANTS)
                                        .child(phoneNumber)
                                        .child(VENDORS);
        reference.addValueEventListener(listener);
        return reference;
    }

    public void cancelSubscriptionMyPartner(String phoneNumber, ToastErrorHandlingListener dbListener) {
        getRootRef()
                .child(RESTAURANTS)
                .child(phoneNumber)
                .child(VENDORS)
                .removeEventListener(dbListener);
    }

    public void getMyPartnersOnce(String phoneNumber, ToastErrorHandlingListener listener) {
        getRootRef()
                .child(RESTAURANTS)
                .child(phoneNumber)
                .child(VENDORS)
                .addListenerForSingleValueEvent(listener);
    }

    public void uploadMyPartner(String phoneNumber, HashMap<String, MyPartner> myPartnerMap, OnCompleteListener listener) {
        getRootRef()
                .child(RESTAURANTS)
                .child(phoneNumber)
                .child(VENDORS)
                .setValue(myPartnerMap)
                .addOnCompleteListener(listener);
    }

    public void getAllVendors(ToastErrorHandlingListener listener) {
        getRootRef()
                .child(VENDORS)
                .addListenerForSingleValueEvent(listener);
    }

    public void updateVendors(Map<String, Object> uploadPathMap, DatabaseReference.CompletionListener listener) {
        getRootRef()
                .child(VENDORS)
                .updateChildren(uploadPathMap, listener);
    }

    public void checkUserAlreadyCreated(String phoneNumber, ToastErrorHandlingListener listener) {
        getRootRef()
                .child(RESTAURANTS)
                .child(phoneNumber)
                .child(INFO)
                .child(SIGN_UP_TIME)
                .addListenerForSingleValueEvent(listener);
    }

    public void setInitialSignUpTime(String phoneNumber, String signUpTime, SimpleAuthListener listener) {
        getRootRef()
                .child(RESTAURANTS)
                .child(phoneNumber)
                .child(INFO)
                .child(SIGN_UP_TIME)
                .setValue(signUpTime)
                .addOnSuccessListener(listener)
                .addOnFailureListener(listener);
    }

    public void getRestaurantName(String phoneNumber, ToastErrorHandlingListener listener) {
        getRootRef()
                .child(RESTAURANTS)
                .child(phoneNumber)
                .child(INFO)
                .child(RESTAURANT_NAME)
                .addListenerForSingleValueEvent(listener);
    }

    public void hasVendor(String phoneNumber, HasDbListener hasDbListener) {
        getRootRef()
                .child(VENDORS)
                .child(phoneNumber)
                .addListenerForSingleValueEvent(hasDbListener);
    }

    public void getVendorContactInfo(String phoneNumber, ToastErrorHandlingListener listener) {
        getRootRef()
                .child(VENDORS)
                .child(phoneNumber)
                .child(INFO)
                .addListenerForSingleValueEvent(listener);
    }

    public void getVendorBusinessInfo(String phoneNumber, ToastErrorHandlingListener listener) {
        getRootRef()
                .child(VENDORS)
                .child(phoneNumber)
                .child(BUSINESS_INFO)
                .addListenerForSingleValueEvent(listener);
    }

    private DatabaseReference getRootRef() {
        return instance.getReference();
    }
}

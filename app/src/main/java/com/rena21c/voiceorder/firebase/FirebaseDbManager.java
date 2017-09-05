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
    private static final String RANKING_INFO = "rankingInfo";
    private static final String VISIT_COUNT = "visitCount";
    private static final String NAME = "name";

    private final FirebaseDatabase instance;

    public FirebaseDbManager(FirebaseDatabase instance) {
        this.instance = instance;
        this.instance.setPersistenceEnabled(true);
    }

    public void setFcmToken(String phoneNumber, final String fcmToken, SimpleAuthListener listener) {
        DatabaseReference dr = getRootRef()
                .child(RESTAURANTS)
                .child(phoneNumber)
                .child(INFO)
                .child(FCM_ID);
        dr.keepSynced(true);
        dr.setValue(fcmToken)
                .addOnSuccessListener(listener)
                .addOnFailureListener(listener);
    }

    public Query subscribeRecordedOrder(String phoneNumber, ChildEventListener listener) {
        DatabaseReference dr = getRootRef()
                .child(RESTAURANTS)
                .child(phoneNumber)
                .child(RECORDED_ORDERS);
        dr.keepSynced(true);
        Query query = dr.orderByKey()
                .startAt(phoneNumber + "_00000000000000")
                .endAt(phoneNumber + "_99999999999999")
                .limitToLast(10);
        query.addChildEventListener(listener);
        return query;
    }

    public void removeRecordedOrder(String phoneNumber, String key) {
        DatabaseReference dr = getRootRef()
                .child(RESTAURANTS)
                .child(phoneNumber)
                .child(RECORDED_ORDERS)
                .child(key);
        dr.keepSynced(true);
        dr.removeValue();
    }

    public Query subscribeAcceptedOrder(String phoneNumber, ChildEventListener listener) {
        DatabaseReference dr = getRootRef()
                .child(ORDERS)
                .child(RESTAURANTS);
        dr.keepSynced(true);
        Query query = dr.orderByKey()
                .startAt(phoneNumber + "_00000000000000")
                .endAt(phoneNumber + "_99999999999999");
        query.addChildEventListener(listener);
        return query;
    }

    public void getVendorName(String restaurantPhoneNumber, String vendorPhoneNumber, ValueEventListener listener) {
        DatabaseReference dr = getRootRef()
                .child(RESTAURANTS)
                .child(restaurantPhoneNumber)
                .child(VENDORS)
                .child(vendorPhoneNumber)
                .child(NAME);
        dr.keepSynced(true);
        dr.addListenerForSingleValueEvent(listener);
    }

    public void addFileName(String phoneNumber, final String fileName, OnCompleteListener listener) {
        DatabaseReference dr = getRootRef()
                .child(RESTAURANTS)
                .child(phoneNumber)
                .child(RECORDED_ORDERS)
                .child(fileName);
        dr.keepSynced(true);
        dr.setValue(false).addOnCompleteListener(listener);
    }

    public DatabaseReference subscribeMyPartner(String phoneNumber, ValueEventListener listener) {
        DatabaseReference dr = getRootRef()
                .child(RESTAURANTS)
                .child(phoneNumber)
                .child(VENDORS);
        dr.keepSynced(true);
        dr.addValueEventListener(listener);
        return dr;
    }

    public void cancelSubscriptionMyPartner(String phoneNumber, ToastErrorHandlingListener dbListener) {
        DatabaseReference dr = getRootRef()
                .child(RESTAURANTS)
                .child(phoneNumber)
                .child(VENDORS);
        dr.keepSynced(true);
        dr.removeEventListener(dbListener);
    }

    public void getMyPartnersOnce(String phoneNumber, ToastErrorHandlingListener listener) {
        DatabaseReference dr = getRootRef()
                .child(RESTAURANTS)
                .child(phoneNumber)
                .child(VENDORS);
        dr.keepSynced(true);
        dr.addListenerForSingleValueEvent(listener);
    }

    public void uploadMyPartner(String phoneNumber, HashMap<String, MyPartner> myPartnerMap, OnCompleteListener listener) {
        DatabaseReference dr = getRootRef()
                .child(RESTAURANTS)
                .child(phoneNumber)
                .child(VENDORS);
        dr.keepSynced(true);
        dr.setValue(myPartnerMap).addOnCompleteListener(listener);
    }

    public void getAllVendors(ToastErrorHandlingListener listener) {
        DatabaseReference dr = getRootRef()
                .child(VENDORS);
        dr.keepSynced(true);
        dr.addListenerForSingleValueEvent(listener);
    }

    public void updateVendors(Map<String, Object> uploadPathMap, DatabaseReference.CompletionListener listener) {
        DatabaseReference dr = getRootRef()
                .child(VENDORS);
        dr.keepSynced(true);
        dr.updateChildren(uploadPathMap, listener);
    }

    public void checkUserAlreadyCreated(String phoneNumber, ToastErrorHandlingListener listener) {
        DatabaseReference dr = getRootRef()
                .child(RESTAURANTS)
                .child(phoneNumber)
                .child(INFO)
                .child(SIGN_UP_TIME);
        dr.keepSynced(true);
        dr.addListenerForSingleValueEvent(listener);
    }

    public void setInitialSignUpTime(String phoneNumber, String signUpTime, SimpleAuthListener listener) {
        DatabaseReference dr = getRootRef()
                .child(RESTAURANTS)
                .child(phoneNumber)
                .child(INFO)
                .child(SIGN_UP_TIME);
        dr.keepSynced(true);
        dr.setValue(signUpTime)
                .addOnSuccessListener(listener)
                .addOnFailureListener(listener);
    }

    public void getRestaurantName(String phoneNumber, ToastErrorHandlingListener listener) {
        DatabaseReference dr = getRootRef()
                .child(RESTAURANTS)
                .child(phoneNumber)
                .child(INFO)
                .child(RESTAURANT_NAME);
        dr.keepSynced(true);
        dr.addListenerForSingleValueEvent(listener);
    }

    public void hasVendor(String phoneNumber, HasDbListener hasDbListener) {
        DatabaseReference dr = getRootRef()
                .child(VENDORS)
                .child(phoneNumber);
        dr.keepSynced(true);
        dr.addListenerForSingleValueEvent(hasDbListener);
    }

    public void getVendorContactInfo(String phoneNumber, ToastErrorHandlingListener listener) {
        DatabaseReference dr = getRootRef()
                .child(VENDORS)
                .child(phoneNumber)
                .child(INFO);
        dr.keepSynced(true);
        dr.addListenerForSingleValueEvent(listener);
    }

    public void getVendorBusinessInfo(String phoneNumber, ToastErrorHandlingListener listener) {
        DatabaseReference dr = getRootRef()
                .child(VENDORS)
                .child(phoneNumber)
                .child(BUSINESS_INFO);
        dr.keepSynced(true);
        dr.addListenerForSingleValueEvent(listener);
    }

    public void setVisitCount(final String phoneNumber, long visitCount) {
        DatabaseReference dr = getRootRef()
                .child(VENDORS)
                .child(phoneNumber)
                .child(RANKING_INFO)
                .child(VISIT_COUNT);
        dr.keepSynced(true);
        dr.setValue(visitCount);
    }

    public void getVisitCount(String phoneNumber, ToastErrorHandlingListener listener) {
        DatabaseReference dr = getRootRef()
                .child(VENDORS)
                .child(phoneNumber)
                .child(RANKING_INFO)
                .child(VISIT_COUNT);
        dr.keepSynced(true);
        dr.addListenerForSingleValueEvent(listener);
    }

    private DatabaseReference getRootRef() {
        return instance.getReference();
    }
}

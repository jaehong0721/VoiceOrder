package com.rena21c.voiceorder.network;


import com.rena21c.voiceorder.pojo.UserToken;
import com.rena21c.voiceorder.pojo.Vendor;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @GET("/requestTokenWithPhoneNumber")
    Call<UserToken> getToken(@Query("phoneNumber") String phoneNumber);

    @POST("/vendor/getNearbyVendors")
    Call<List<Vendor>> getNearbyVendors(@Body HashMap<String, Object> bodyMap);

    @GET("/api/vendor/{phoneNumber}")
    Call<HashMap<String,String>> getVendorInfo(@Path("phoneNumber") String phoneNumber);
}

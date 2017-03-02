package com.rena21c.voiceorder.network;


import com.rena21c.voiceorder.activities.SplashActivity;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    @GET("/requestTokenWithPhoneNumber")
    Call<SplashActivity.UserToken> getToken(@Query("phoneNumber") String phoneNumber);
}

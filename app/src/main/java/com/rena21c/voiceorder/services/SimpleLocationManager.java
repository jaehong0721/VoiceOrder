package com.rena21c.voiceorder.services;


import android.app.Activity;
import android.location.Location;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

@SuppressWarnings("MissingPermission")
public class SimpleLocationManager{

    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location location;

    public SimpleLocationManager(Activity activity) {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(activity, new OnSuccessListener<Location>() {
                    @Override public void onSuccess(Location location) {
                        if(location != null) {
                            SimpleLocationManager.this.location = location;
                        }
                    }
                });
    }

    public Location getLocation() {
        return location;
    }
}

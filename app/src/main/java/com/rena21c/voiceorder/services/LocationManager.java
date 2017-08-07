package com.rena21c.voiceorder.services;


import android.content.Context;
import android.content.IntentSender;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.io.IOException;
import java.util.List;

public class LocationManager implements GoogleApiClient.ConnectionCallbacks,
                                        GoogleApiClient.OnConnectionFailedListener,
                                        LocationListener {

    public interface LocationUpdateListener {
        void onLocationUpdateFailed(Status status) throws IntentSender.SendIntentException;

        void onLocationUpdated(double latitude, double longitude, String locality);
    }

    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    private static final float SMALLEST_DISPLACEMENT_METERS = 100;

    private GoogleApiClient googleApiClient;

    private LocationRequest locationRequest;

    private LocationSettingsRequest locationSettingsRequest;

    private Location currentLocation;

    private LocationUpdateListener listener;

    private boolean isTrackingLocation;

    private boolean isConnectedGoogleApi;

    private Geocoder geocoder;

    public LocationManager(Context context, Geocoder geocoder) {

        this.geocoder = geocoder;

        buildGoogleApiClient(context);
        createLocationRequest();
        buildLocationSettingsRequest();
    }

    public void connectGoogleApiClient() {
        if(isConnectedGoogleApi) return;
        googleApiClient.connect();
    }

    public void disconnectGoogleApiClient() {
        if(!isConnectedGoogleApi) return;
        googleApiClient.disconnect();
        isConnectedGoogleApi = false;
    }

    public void setLocationUpdateListener(LocationUpdateListener listener) {
        this.listener = listener;
    }

    public void startLocationUpdates() {

        if (!isConnectedGoogleApi || isTrackingLocation) {
            return;
        }

        Log.d("test:", "startLocationUpdates");
        LocationServices.SettingsApi.checkLocationSettings(
                googleApiClient,
                locationSettingsRequest
        ).setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @SuppressWarnings("MissingPermission")
            @Override
            public void onResult(LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();

                switch (status.getStatusCode()) {

                    case LocationSettingsStatusCodes.SUCCESS:
                        isTrackingLocation = true;
                        Log.d("test", "locationSettingsRequest success");
                        LocationServices.FusedLocationApi.requestLocationUpdates(
                                googleApiClient, locationRequest, LocationManager.this);
                        Log.d("test", "start location update");
                        break;

                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                        try {
                            listener.onLocationUpdateFailed(status);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                        }

                        break;

                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:

                        Log.d("LocationService", "위치 찾기 실패");

                        break;
                }
            }
        });
    }

    @SuppressWarnings("MissingPermission")
    @Override public void onConnected(@Nullable Bundle bundle) {
        Log.d("test:", "Connection success");
        isConnectedGoogleApi = true;

        if (currentLocation != null) return;

        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        if (lastLocation != null) {
            currentLocation = lastLocation;
            String locality = getLocalityFrom(currentLocation.getLatitude(), currentLocation.getLongitude());
            listener.onLocationUpdated(currentLocation.getLatitude(), currentLocation.getLongitude(), locality);
        }

        startLocationUpdates();
    }

    @Override public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        isConnectedGoogleApi = false;
        Log.d("test:", "Connection failed");
    }

    @Override public void onConnectionSuspended(int i) {
        isConnectedGoogleApi = false;
        Log.d("test:", "Connection suspended");
    }

    @Override public void onLocationChanged(Location location) {
        Log.d("test:", "finish location update");
        if (currentLocation == null || getDistance(currentLocation, location) > SMALLEST_DISPLACEMENT_METERS) {
            currentLocation = location;
            String locality = getLocalityFrom(currentLocation.getLatitude(), currentLocation.getLongitude());
            listener.onLocationUpdated(currentLocation.getLatitude(), currentLocation.getLongitude(), locality);
        }
    }

    public void stopLocationUpdates() {
        Log.d("test:", "stopLocationUpdates");
        if(!isConnectedGoogleApi || !isTrackingLocation) return;

        isTrackingLocation = false;
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
    }

    private void buildGoogleApiClient(Context context) {
        googleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS)
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS)
                .setSmallestDisplacement(SMALLEST_DISPLACEMENT_METERS)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        locationSettingsRequest = builder.build();
    }

    private double getDistance(Location location1, Location location2) {
        double latitude1 = location1.getLatitude();
        double longitude1 = location1.getLongitude();

        double latitude2 = location2.getLatitude();
        double longitude2 = location2.getLongitude();


        double theta = longitude1 - longitude2;
        double dist = Math.sin(Math.toRadians(latitude1)) * Math.sin(Math.toRadians(latitude2)) +
                        Math.cos(Math.toRadians(latitude1)) * Math.cos(Math.toRadians(latitude2)) * Math.cos(Math.toRadians(theta));

        dist = Math.acos(dist);
        dist = Math.toDegrees(dist);
        dist = dist * 60 * 1.1515;

        return dist * 1609.344;
    }

    private String getLocalityFrom(double latitude, double longitude) {
        List<Address> addressList = null;
        String locality = "알수없음";
        try {
            addressList = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(addressList != null && addressList.size() != 0)
            locality = addressList.get(0).getLocality();
        return locality != null ? locality : "알수없음";
    }

}

package com.tradingsupervisor.viewmodel;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.location.Location;
import android.util.Log;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class LocationLiveData extends LiveData<Location> {

    private FusedLocationProviderClient locationClient;
    private LocationRequest locationRequest;

    @SuppressLint("MissingPermission")
    public LocationLiveData(Context appContext) {
        locationClient = LocationServices.getFusedLocationProviderClient(appContext);
        locationRequest = new LocationRequest();
        locationRequest.setInterval(50000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY); //PRIORITY_HIGH_ACCURACY
    }


    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            setValue(locationResult.getLastLocation());
            Log.d("upd", locationResult.getLastLocation().toString());
        }
    };

    @SuppressLint("MissingPermission")
    @Override
    protected void onActive() {
        super.onActive();
        locationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        if (locationCallback != null)
            locationClient.removeLocationUpdates(locationCallback);
    }
}

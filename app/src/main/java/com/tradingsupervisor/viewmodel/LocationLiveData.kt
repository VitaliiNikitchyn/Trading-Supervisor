package com.tradingsupervisor.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.HandlerThread
import android.util.Log
import androidx.lifecycle.LiveData
import com.google.android.gms.location.*

class LocationLiveData private constructor(appContext: Context) : LiveData<Location>() {
    private val locationClient = LocationServices.getFusedLocationProviderClient(appContext)
    private var handlerThread: HandlerThread? = null
    private val locationRequest: LocationRequest = LocationRequest().apply {
        interval = (10 * 1000).toLong() //10 sec
        fastestInterval = (10 * 1000).toLong()
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult ?: return
            super.onLocationResult(locationResult)
            val location = locationResult.lastLocation
            //val lat = location.latitude.toString()
            //val lon = location.longitude.toString()
            //Log.d("myupd", "new location    " + this.hashCode() + "   " + lat + " : " + lon);
            postValue(location)
        }
    }

    @SuppressLint("MissingPermission")
    override fun onActive() {
        super.onActive()
        handlerThread = HandlerThread("locationHandlerThread")
        handlerThread!!.start()
        locationClient.requestLocationUpdates(locationRequest, locationCallback, handlerThread!!.looper)
    }

    override fun onInactive() {
        super.onInactive()
        locationClient.removeLocationUpdates(locationCallback)
        handlerThread!!.quitSafely()
        //setValue(new Location(""));
    }

    companion object {
        fun getInstance(appContext: Context): LocationLiveData {
            return LocationLiveData(appContext)
        }
    }
}
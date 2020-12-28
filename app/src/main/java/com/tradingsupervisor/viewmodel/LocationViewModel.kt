package com.tradingsupervisor.viewmodel

import android.app.Application
import android.location.Location
import androidx.arch.core.util.Function
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.tradingsupervisor.data.entity.Shop

class LocationViewModel(application: Application) : AndroidViewModel(application) {
    //extends AndroidViewModel; if Context needed, it can be acquired: getApplication()
    val location: LocationLiveData = LocationLiveData.getInstance(application)
    var distanceToShop: LiveData<Float> = MutableLiveData()
        private set

    private val currentShop: MutableLiveData<Shop> = MutableLiveData()

    fun setCurrentShop(shop: Shop) {
        currentShop.value = shop
        val shopLocation = Location("")
        shopLocation.latitude = shop.latitude
        shopLocation.longitude = shop.longitude
        distanceToShop = Transformations.map(location) { myLocation -> myLocation?.distanceTo(shopLocation) }
    }

    fun getCurrentShop(): LiveData<Shop> {
        return currentShop
    }
}
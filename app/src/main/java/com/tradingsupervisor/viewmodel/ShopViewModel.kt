package com.tradingsupervisor.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.tradingsupervisor.data.entity.Shop
import com.tradingsupervisor.repository.ShopRepository
import com.tradingsupervisor.webApi.ResponseStatus
import com.tradingsupervisor.webApi.SingleLiveEvent
import java.util.*

class ShopViewModel(application: Application) : AndroidViewModel(application) {
    private val shopRepository = ShopRepository(application)
    private val uploadImagesStatus: SingleLiveEvent<ResponseStatus> = SingleLiveEvent()

    //filtered by current location
    val nearbyShops: LiveData<List<Shop>> = Transformations.switchMap(
            LocationLiveData.getInstance(application)) { myLocation ->
        shopRepository.getNearbyShops(myLocation.latitude, myLocation.longitude, Date().time)
    }

    val allShops: LiveData<List<Shop>>
        get() = shopRepository.allShops

    val visitPercentage: LiveData<Int?>
        get() = shopRepository.getVisitPercentage(Date().time + 2000)

    val uploadPhotosStatus: LiveData<ResponseStatus>
        get() = uploadImagesStatus

    val photoCount: LiveData<Int>
        get() = shopRepository.photoCount

    fun downloadShopList() {
        shopRepository.loadShopList()
    }

    fun uploadAllPhotos() {
        shopRepository.uploadPhotos(uploadImagesStatus)
    }
}
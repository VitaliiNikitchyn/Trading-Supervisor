package com.tradingsupervisor.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tradingsupervisor.data.entity.Photo
import com.tradingsupervisor.repository.PhotoRepository
import com.tradingsupervisor.webApi.ResponseStatus
import com.tradingsupervisor.webApi.SingleLiveEvent
import java.nio.ByteBuffer

class PhotoViewModel(application: Application) : AndroidViewModel(application) {
    private val photoRepository = PhotoRepository(application)
    private val uploadPhotoStatus = SingleLiveEvent<ResponseStatus>()
    val currentPhoto = MutableLiveData<Photo>()

    fun getPhotosByShopID(id: Long?): LiveData<List<Photo>> {
        return photoRepository.getPhotosByShopID(id)
    }

    //called from async looper
    fun addPhoto(photo: Photo, /*byte[] bytes*/bytes: ByteBuffer) {
        photoRepository.addPhoto(photo.filename, bytes)
        photoRepository.addPhoto(photo)
    }

    fun uploadPhotos(shopID: Long) {
        photoRepository.uploadPhotos(shopID, uploadPhotoStatus)
    }

    fun getUploadPhotoStatus(): LiveData<ResponseStatus> {
        return uploadPhotoStatus
    }

    fun removePhoto(photo: Photo) {
        photoRepository.removePhoto(photo)
    }

    fun removePhotosByShopID(shopID: Long) {
        photoRepository.removePhotosByShopID(shopID)
    }

    //for SinglePhotoPreview fragment
    fun setCurrentPhoto(photo: Photo) {
        currentPhoto.value = photo
    }

    val photoCount: LiveData<Int>
        get() = photoRepository.photoCount
}
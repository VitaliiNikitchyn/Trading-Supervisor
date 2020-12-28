package com.tradingsupervisor.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tradingsupervisor.data.entity.Photo
import com.tradingsupervisor.webApi.ResponseStatus
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class PhotoRepository(application: Application) : BaseShopPhotoRepository(application) {
    private val executor: Executor = Executors.newFixedThreadPool(2)

    fun addPhoto(photo: Photo) {
        executor.execute { photoDao.insert(photo) }
    }

    fun addPhoto(fileName: String, data: ByteBuffer /*byte[] bytes*/) {
        executor.execute {
            try {
                val file = File(context.filesDir, fileName)
                val fileChannel = FileOutputStream(file).channel
                fileChannel.write(data)
                fileChannel.close()
            } catch (e: Exception) {
                e.printStackTrace()
                //Toast.makeText(getApplication(), "File saving error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*
    private float getImageViewRotation(File file) {
        try {
            ExifInterface exifInterface = new ExifInterface(file.getAbsolutePath());
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            float rotationDegrees = 0.0f;
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotationDegrees = 90.0f;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotationDegrees = 180.0f;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotationDegrees = 270.0f;
                    break;
            }
            return rotationDegrees;
        } catch (Exception e) {
            return 0.0f;
        }
    }*/

    fun removePhoto(photo: Photo) {
        executor.execute {
            val file = File(context.filesDir, photo.filename)
            if (file.delete()) photoDao.removePhoto(photo)
        }
    }

    fun removePhotosByShopID(shopID: Long?) {
        executor.execute {
            val photos = photoDao.getPhotosByShopIDSync(shopID)
            for (photo in photos) {
                val file = File(context.filesDir, photo.filename)
                if (file.delete()) photoDao.removePhoto(photo)
            }
        }
    }

    fun getPhotosByShopID(id: Long?): LiveData<List<Photo>> {
        return photoDao.getPhotosByShopIDAsync(id)
    }

    /*
     * Upload cashed photos by shopID and clear cash after uploading
     */
    fun uploadPhotos(shopID: Long, responseStatus: MutableLiveData<ResponseStatus>) {
        executor.execute {
            try {
                responseStatus.postValue(uploadPhotosByShopID(shopID))
            } catch (e: IOException) {
                //e.printStackTrace();
                responseStatus.postValue(ResponseStatus.CLIENT_ERROR)
            } catch (e: Exception) {
                responseStatus.postValue(ResponseStatus.ERROR)
            }
        }
    }
}
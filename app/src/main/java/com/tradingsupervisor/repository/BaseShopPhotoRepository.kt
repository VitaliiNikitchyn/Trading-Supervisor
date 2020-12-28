package com.tradingsupervisor.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.tradingsupervisor.R
import com.tradingsupervisor.data.AppDatabase
import com.tradingsupervisor.data.dao.PhotoDao
import com.tradingsupervisor.data.dao.ShopDao
import com.tradingsupervisor.webApi.HttpClient
import com.tradingsupervisor.webApi.ResponseStatus
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

open class BaseShopPhotoRepository(protected val context: Application) {
    private val database = AppDatabase.getDatabase(context)
    protected val shopDao: ShopDao = database.shopDao()
    protected val photoDao: PhotoDao = database.photoDao()
    private val sharedPref = context.getSharedPreferences(
            context.getString(R.string.appSharedPreferences), Application.MODE_PRIVATE)

    protected fun uploadPhotosByShopID(shopID: Long): ResponseStatus {
        //1. prepare request
        val photos = photoDao.getPhotosByShopIDSync(shopID) //ORDER BY timestamp ASC
        val header = sharedPref.getString(context.getString(R.string.authToken), null)
        val fileParts: MutableList<MultipartBody.Part> = ArrayList()
        val t_start = dateFormat.format(photos[0].creationTime)
        val t_finish = dateFormat.format(photos[photos.size - 1].creationTime)
        val diff = Math.abs(photos[photos.size - 1].creationTime.time - photos[0].creationTime.time)
        val diffHours = (diff / (60 * 60 * 1000)).toInt()
        val diffMin = (diff / (60 * 1000)).toInt() - diffHours * 60
        val diffSec = (diff / 1000).toInt() - diffMin * 60
        val t_delta = String.format(Locale.US, "%02d:%02d:%02d", diffHours, diffMin, diffSec)
        for (photo in photos) {
            val file = File(context.filesDir, photo.filename)
            val reqFile = RequestBody.create(MediaType.parse("image/*"), file)
            val multipartFile = MultipartBody.Part.createFormData("files", file.name, reqFile)
            fileParts.add(multipartFile)
        }

        //2. do request
        val response = HttpClient
                .getApi()
                .postPhotos(header, fileParts, t_start, t_finish, t_delta, shopID.toInt())
                .execute()

        return if (response.isSuccessful) { //the only success response here is 201
            for (photo in photos) {
                val file = File(context.filesDir, photo.filename)
                if (file.delete()) photoDao.removePhoto(photo)
            }
            //updateShopVisitTime
            shopDao.updateLastVisitDate(shopID, Date().time)
            ResponseStatus.SUCCESS
        } else { //server error or unauthorized
            when (response.code()) {
                401, 402 -> {
                    ResponseStatus.UNAUTHORIZED
                }
                else -> ResponseStatus.SERVER_ERROR
            }
        }
    }

    val photoCount: LiveData<Int>
        get() = photoDao.photoCount

    companion object {
        private val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.US)
    }
}
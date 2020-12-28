package com.tradingsupervisor.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tradingsupervisor.data.entity.Shop
import com.tradingsupervisor.webApi.ResponseStatus
import java.io.IOException
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class ShopRepository(application: Application) : BaseShopPhotoRepository(application) {
    private val executor = Executors.newSingleThreadExecutor()

    fun getNearbyShops(latitude: Double, longitude: Double, currentTimestamp: Long): LiveData<List<Shop>> {
        return shopDao.getNearestShops(latitude, longitude, currentTimestamp)
//        val ld = MutableLiveData<List<Shop>>()
//        ld.postValue(testData.subList(0, 2))
//        return ld
    }

    //return shopDao.getAllShops();
    val allShops: LiveData<List<Shop>>
        get() = shopDao.allShops

    fun getVisitPercentage(currentTimestamp: Long): LiveData<Int?> {
        return shopDao.getVisitPercentage(currentTimestamp)
    }

    fun loadShopList() {
        executor.execute {
            shopDao.deleteAll()
            shopDao.insert(genData())
        }

        /*
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.appSharedPreferences), Application.MODE_PRIVATE);
                */
        /*
        String header = sharedPref.getString(context.getString(R.string.authToken), null);
        //async request
        HttpClient.getApi().getShops(header).enqueue(new Callback<List<Shop>>() {
            @Override
            public void onResponse(Call<List<Shop>> call, final Response<List<Shop>> response) {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        if (response.isSuccessful()) {
                            shopDao.deleteAll();
                            shopDao.insert(response.body());
                        }
                    }
                });
            }

            @Override
            public void onFailure(Call<List<Shop>> call, Throwable t) {

            }
        });*/
    }

    /*
     * Upload all previously cashed photos and clear cash after uploading
     */
    fun uploadPhotos(responseStatus: MutableLiveData<ResponseStatus>) {
        executor.execute {
            try {
                var status: ResponseStatus = ResponseStatus.SUCCESS
                val shopIDs = photoDao.shopIDs
                for (shopID in shopIDs) {
                    status = uploadPhotosByShopID(shopID)
                }
                responseStatus.postValue(status)
            } catch (e: IOException) {
                responseStatus.postValue(ResponseStatus.CLIENT_ERROR)
            } catch (e: Exception) {
                responseStatus.postValue(ResponseStatus.ERROR)
            }
        }
    }

    private fun genData(): ArrayList<Shop> {
        val shopName = arrayOf("Shop 1", "Billa", "Central", "MyFriend", "The village")
        val shopAddress = arrayOf(
                "103 Chudnivska str",
                "108-V Chudnivska street",
                "some address",
                "some address",
                "my address in the village"
        )
        val assortmentItems = arrayOf(
                arrayOf("Gold Key", "Super Schocolate", "Plombir 100%"),
                arrayOf("Gold Key", "Super Schocolate", "assortment 3"),
                arrayOf("test1", "Super Schocolate", "Plombir 100%")
        )
        val list = ArrayList<Shop>()

//        List<Product> assortmentList1 = new ArrayList<>();
//        assortmentList1.add(new Product(assortmentItems[0][0]));
//        assortmentList1.add(new Product(assortmentItems[0][1]));
//        assortmentList1.add(new Product(assortmentItems[0][2]));
        val timeNow = Date()
        val timeYesterday = Date(timeNow.time - 86400000)

        val shop1 = Shop()
        shop1.id = 1L
        shop1.name = shopName[0]
        shop1.address = shopAddress[0]
        shop1.latitude = 50.24463 //Чуднівська 103
        shop1.longitude = 28.637525
        shop1.lastVisitedDate = timeYesterday
        shop1.workRadius = 80F
        //shop1.setAssortment(assortmentList1);

        val shop2 = Shop()
        shop2.id = 2L
        shop2.name = shopName[1]
        shop2.address = shopAddress[1]
        shop2.latitude = 50.246976 //Моя хата
        shop2.longitude = 28.629061
        shop2.lastVisitedDate = timeNow
        shop2.workRadius = 80F

        val shop3 = Shop()
        shop3.id = 3L
        shop3.name = shopName[2]
        shop3.address = shopAddress[2]
        shop3.latitude = 50.318844 //перехрестя Березівка
        shop3.longitude = 28.439109
        shop3.lastVisitedDate = timeYesterday
        shop3.workRadius = 80F

        val shop4 = Shop()
        shop4.id = 4L
        shop4.name = shopName[3]
        shop4.address = shopAddress[3]
        shop4.latitude = 50.31838195 //хата Макса в бр
        shop4.longitude = 28.43921383
        shop4.lastVisitedDate = timeYesterday
        shop4.workRadius = 80F

        val shop5 = Shop()
        shop5.id = 5L
        shop5.name = shopName[4]
        shop5.address = shopAddress[4]
        shop5.latitude = 50.32352299 //моя хата в бр
        shop5.longitude = 28.43437996
        shop5.lastVisitedDate = timeYesterday
        shop5.workRadius = 80F

        list.add(shop1)
        list.add(shop2)
        list.add(shop3)
        list.add(shop4)
        list.add(shop5)
        return list
    }
}
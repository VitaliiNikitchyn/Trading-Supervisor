package com.tradingsupervisor.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.tradingsupervisor.R
import com.tradingsupervisor.data.entity.Shop
import com.tradingsupervisor.ui.fragment.ShopDetailsFragment
import com.tradingsupervisor.viewmodel.LocationViewModel

class PhotoActivity : AppCompatActivity() {
    //private lateinit var alertDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo)
        val shop: Shop? = intent.getParcelableExtra(Shop.TAG)
        if (shop != null) {
            val locationVM = ViewModelProvider(this).get(LocationViewModel::class.java)
            locationVM.setCurrentShop(shop)
            var fragment = supportFragmentManager.findFragmentByTag(ShopDetailsFragment.TAG)
            if (fragment == null) fragment = ShopDetailsFragment.newInstance()
            supportFragmentManager.beginTransaction()
                    .replace(R.id.photo_activity_container, fragment, ShopDetailsFragment.TAG)
                    .commit()
        }
    }

//    override fun onResume() {
//        super.onResume()
//        //Log.d("myupd", "PhotoActivity onResume");
//        val shop: Shop? = intent.getParcelableExtra(Shop.TAG)
//        val locationVM = ViewModelProvider(this).get(LocationViewModel::class.java)
//
//        locationVM.distanceToShop.observe(this, Observer<Float> { distance ->
//            Log.d("myupd", "PhotoActivity onResume distance onChange : $distance")
//            if (distance <= 185.0f) {
//                if (alertDialog != null && alertDialog!!.isShowing) {
//                    alertDialog!!.cancel()
//                    alertDialog = null
//                }
//            } else if (distance <= 230.0f && distance > 185.0f && alertDialog == null) {
//                /*
//                    Fragment fragment = getSupportFragmentManager().findFragmentByTag(DistanceWarningFragment.TAG);
//                    if (fragment == null)
//                        fragment = DistanceWarningFragment.newInstance();
//                    getSupportFragmentManager().beginTransaction()
//                            .replace(R.id.photo_activity_container, fragment, DistanceWarningFragment.TAG)
//                            .addToBackStack(null)
//                            .commit();*/
//                val builder = AlertDialog.Builder(this@PhotoActivity)
//                builder.setCancelable(false)
//                builder.setTitle("Увага!!")
//                builder.setMessage("Ви віддаляєтесь від магазину " + shop.getName() + ". " +
//                        "Якщо бажаєте продовжити роботу, поверніться в магазин за адресою " + shop.getAddress())
//                builder.setPositiveButton("Я завершив роботу в цьому магазині (фото НЕ збережуться)") { dialog, which ->
//                    val photoVM = ViewModelProvider(this@PhotoActivity).get(PhotoViewModel::class.java)
//                    photoVM.removePhotosByShopID(shop.getId())
//                    finish()
//                }
//                alertDialog = builder.create()
//                alertDialog.show()
//            } else if (distance > 230.0f) {
//
//                //PhotoViewModel photoVM = ViewModelProviders.of(PhotoActivity.this).get(PhotoViewModel.class);
//                //photoVM.removePhotosByShopID(shop.getId());
//                //Toast.makeText(this@PhotoActivity, "Ви покинули магазин " + shop.getName(), Toast.LENGTH_SHORT).show()
//                finish()
//            }
//        })
//    }
}
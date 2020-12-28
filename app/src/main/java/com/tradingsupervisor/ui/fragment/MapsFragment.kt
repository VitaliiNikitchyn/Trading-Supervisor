package com.tradingsupervisor.ui.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ListView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.tradingsupervisor.R
import com.tradingsupervisor.adapter.SimpleShopAdapter
import com.tradingsupervisor.data.entity.Shop
import com.tradingsupervisor.ui.PhotoActivity
import com.tradingsupervisor.viewmodel.LocationViewModel
import com.tradingsupervisor.viewmodel.ShopViewModel
import java.util.*

class MapsFragment : Fragment(), OnMapReadyCallback, OnItemClickListener {
    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private lateinit var shopListView: ListView
    private val markerList = ArrayList<Marker>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_maps, container, false)
        shopListView = view.findViewById(R.id.shop_list)
        shopListView.onItemClickListener = this
        shopListView.adapter = SimpleShopAdapter(requireContext())
        mapView = view.findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
        return view
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        mapView.onPause()
        super.onPause()
    }

    override fun onStop() {
        mapView.onStop()
        super.onStop()
    }

    override fun onDestroy() {
        mapView.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        mapView.onLowMemory()
        super.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        mapView.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        this.googleMap = googleMap ?: return
        googleMap.setMinZoomPreference(10f)
        val startPosition = LatLng(50.25465, 28.6586669)
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(startPosition))
        val uiSettings = googleMap.uiSettings
        uiSettings.isIndoorLevelPickerEnabled = true
        uiSettings.isMyLocationButtonEnabled = true
        uiSettings.isMapToolbarEnabled = true
        uiSettings.isCompassEnabled = true
        uiSettings.isZoomControlsEnabled = true
        enableLocation()
        displayShopMarkers()
        setupShopList()
        //dismiss progressDialog here
    }

    private fun setupShopList() {
        val shopViewModel = ViewModelProvider(requireActivity()).get(ShopViewModel::class.java)
        shopViewModel.nearbyShops.observe(viewLifecycleOwner) { shops ->
            val adapter = (shopListView.adapter as SimpleShopAdapter)
            adapter.updateData(shops)
            adapter.notifyDataSetChanged()
        }
    }

    private fun displayShopMarkers() {
        val shopViewModel = ViewModelProvider(requireActivity()).get(ShopViewModel::class.java)
        shopViewModel.allShops.observe(viewLifecycleOwner) { shops ->
            for (marker in markerList) {
                marker.remove()
            }
            markerList.clear()
            for (shop in shops) {
                val marker = googleMap.addMarker(MarkerOptions()
                        .position(LatLng(shop.latitude, shop.longitude))
                        .title(shop.name)
                        .snippet(shop.address)
                        .icon(BitmapDescriptorFactory.defaultMarker(
                                if (Date().time - shop.lastVisitedDate.time < 86400000)
                                    BitmapDescriptorFactory.HUE_GREEN else BitmapDescriptorFactory.HUE_RED))
                )
                markerList.add(marker)
            }
        }
    }

//    private fun setupCurrentLocation() {
//        val locationViewModel = ViewModelProvider(requireActivity()).get(LocationViewModel::class.java)
//        locationViewModel.location.observe(viewLifecycleOwner) { location ->
//            googleMap.animateCamera(CameraUpdateFactory.newLatLng(LatLng(location.latitude, location.longitude)))
//        }
//    }

    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        val shop = shopListView.adapter.getItem(position) as Shop
        val intent = Intent(activity, PhotoActivity::class.java)
        intent.putExtra(Shop.TAG, shop)
        //intent.putExtra(Shop.SHOP_ID, shop.id)
        startActivity(intent)
        //startActivityForResult(intent, MainActivity.SHOP_RESULT);
        /*Map<String, String> listItem = (Map<String, String>) parent.getItemAtPosition(position);
        Long currentShopID = Long.valueOf(Objects.requireNonNull(listItem.get(Shop.SHOP_ID)));
        */
    }

    private fun enableLocation() {
        val activity = requireActivity()
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_PERMISSION_LOCATION)
        } else {
            googleMap.isMyLocationEnabled = true
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val activity = requireActivity()
        if (requestCode == REQUEST_PERMISSION_LOCATION) {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity,
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                googleMap.isMyLocationEnabled = true
            }
        }
    }

    companion object {
        const val TAG = "MapsFragment"
        const val REQUEST_PERMISSION_LOCATION = 1000
        fun newInstance(): MapsFragment {
            return MapsFragment()
        }
    }
}
package com.tradingsupervisor.ui.fragment;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.tradingsupervisor.R;
import com.tradingsupervisor.database.entity.Shop;
import com.tradingsupervisor.viewmodel.LocationViewModel;
import com.tradingsupervisor.viewmodel.ShopViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class NearbyShopsFragment extends Fragment implements
        OnMapReadyCallback, AdapterView.OnItemClickListener{

    private MapView mapView;
    private GoogleMap googleMap;    //used by mapView
    private ListView listView;

    public NearbyShopsFragment() {}

    public static NearbyShopsFragment newInstance() {
        return new NearbyShopsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_nearby_shops, container, false);
        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        listView = view.findViewById(R.id.shop_list);
        listView.setOnItemClickListener(this);
        //mapView.getMapAsync(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        mapView.getMapAsync(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onStop() {
        mapView.onStop();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.setMinZoomPreference(10);
        LatLng startPosition = new LatLng(50.25465, 28.6586669);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(startPosition));

        UiSettings uiSettings = googleMap.getUiSettings();
        uiSettings.setIndoorLevelPickerEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);
        uiSettings.setMapToolbarEnabled(true);
        uiSettings.setCompassEnabled(true);
        uiSettings.setZoomControlsEnabled(true);

        this.requestPermissions();
        this.setupShopList();
        //dismiss progressDialog here
    }

    private void setupShopList() {
        final String SHOP_NAME = "SHOP_NAME";
        final String SHOP_ADDRESS = "SHOP_ADDRESS";

        ShopViewModel shopViewModel = ViewModelProviders.of(getActivity()).get(ShopViewModel.class);
        shopViewModel.getShops().observe(getActivity(), new Observer<List<Shop>>() {
            @Override
            public void onChanged(@Nullable List<Shop> shops) {
                List<Map<String, String>> listData = new ArrayList<>();
                for (Shop shop: shops) {
                    Map<String, String> item = new HashMap<>();
                    item.put(SHOP_NAME, shop.getName());
                    item.put(SHOP_ADDRESS, shop.getAddress());
                    listData.add(item);
                }

                String from[] = {SHOP_NAME, SHOP_ADDRESS};
                int to[] = {R.id.shop_name, R.id.shop_address};
                SimpleAdapter adapter = new SimpleAdapter(getActivity(), listData, R.layout.list_item_shop, from, to);
                listView.setAdapter(adapter);
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.navigation_drawer_content, ShopDetailsFragment.newInstance(position))
                .addToBackStack(null)
                .commit();
    }

    private void setupCurrentLocation() {
        LocationViewModel locationViewModel = ViewModelProviders.of(getActivity()).get(LocationViewModel.class);
        locationViewModel.getLocation().observe(getActivity(), new Observer<Location>() {
            @Override
            public void onChanged(@Nullable Location location) {
                if (location != null) {
                    googleMap.animateCamera(CameraUpdateFactory.newLatLng(
                            new LatLng(location.getLatitude(), location.getLongitude())));
                }
            }
        });
    }

    private void requestPermissions() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION}, 1000);
        } else {
            googleMap.setMyLocationEnabled(true);
            setupCurrentLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                googleMap.setMyLocationEnabled(true);
                setupCurrentLocation();
            } //else getActivity().finish();
        }
    }
}

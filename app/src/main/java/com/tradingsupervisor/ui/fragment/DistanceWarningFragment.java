package com.tradingsupervisor.ui.fragment;


import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tradingsupervisor.R;
import com.tradingsupervisor.data.entity.Shop;
import com.tradingsupervisor.viewmodel.LocationViewModel;
import com.tradingsupervisor.viewmodel.PhotoViewModel;


public class DistanceWarningFragment extends Fragment {

    public static final String TAG = "DistanceWarningFragment";

    private TextView distanceTextView;

    public DistanceWarningFragment() { }

    public static DistanceWarningFragment newInstance() {
        return new DistanceWarningFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (getView() != null) return getView();
        View view = inflater.inflate(R.layout.fragment_distance_warning, container, false);
        TextView shopNameTextView = view.findViewById(R.id.shop_name);
        TextView shopAddressTextView = view.findViewById(R.id.shop_address);
        distanceTextView = view.findViewById(R.id.shop_distance);

        final Shop shop = getActivity().getIntent().getParcelableExtra(Shop.TAG);
        shopNameTextView.setText(shop.getName());
        shopAddressTextView.setText(shop.getAddress());

        view.findViewById(R.id.finish_work_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhotoViewModel photoVM = new ViewModelProvider(getActivity()).get(PhotoViewModel.class);
                photoVM.removePhotosByShopID(shop.getId());
                getActivity().finish();
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LocationViewModel locationVM = new ViewModelProvider(getActivity()).get(LocationViewModel.class);
        locationVM.getDistanceToShop().observe(getViewLifecycleOwner(), new Observer<Float>() {
            @Override
            public void onChanged(@Nullable Float distance) {
                Log.d("myupd","distanceTextView warning frg : " + distance.toString());
                distanceTextView.setText("Відстань до магазину : " + Integer.toString(distance.intValue()) + "м");
                if (distance <= 45.0f) {
                    getActivity().getSupportFragmentManager().popBackStackImmediate();
                    Log.d("myupd", "dismiss distanceTextView warning");
                }
            }
        });
    }

}

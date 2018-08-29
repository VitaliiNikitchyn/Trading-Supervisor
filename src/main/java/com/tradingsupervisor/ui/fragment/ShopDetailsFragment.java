package com.tradingsupervisor.ui.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.tradingsupervisor.R;
import com.tradingsupervisor.database.entity.Assortment;
import com.tradingsupervisor.ui.PhotoActivity;
import com.tradingsupervisor.viewmodel.ShopViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ShopDetailsFragment extends Fragment {

    private static final String ARG_SHOP_INDEX = "shopIndex";
    private int shopIndex;
    private ListView listView;

    public ShopDetailsFragment() {}

    public static ShopDetailsFragment newInstance(int shopIndex) {
        ShopDetailsFragment fragment = new ShopDetailsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SHOP_INDEX, shopIndex);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            shopIndex = getArguments().getInt(ARG_SHOP_INDEX);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shop_details, container, false);
        listView = view.findViewById(R.id.assortment_list);
        setupAssortmentList();

        FloatingActionButton fabCamera = view.findViewById(R.id.fab_camera);
        fabCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), PhotoActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    private void setupAssortmentList() {
        final String ASSORTMENT_NAME = "ASSORTMENT";

        ShopViewModel shopViewModel = ViewModelProviders.of(getActivity()).get(ShopViewModel.class);

        List<Map<String, String>> listData = new ArrayList<>();
        for (Assortment assortment: shopViewModel.getAssortment(shopIndex)) {
            Map<String, String> item = new HashMap<>();
            item.put(ASSORTMENT_NAME, assortment.getName());
            listData.add(item);
        }

        String from[] = {ASSORTMENT_NAME};
        int to[] = {R.id.assortment_name };

        SimpleAdapter adapter = new SimpleAdapter(getActivity(), listData, R.layout.list_item_assortment, from, to);
        listView.setAdapter(adapter);
    }



}

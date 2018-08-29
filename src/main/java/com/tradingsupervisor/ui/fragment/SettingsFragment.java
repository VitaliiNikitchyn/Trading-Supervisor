package com.tradingsupervisor.ui.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tradingsupervisor.R;


public class SettingsFragment extends Fragment {


    public SettingsFragment() {}


    public static SettingsFragment newInstance() {
        return new SettingsFragment();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button button = view.findViewById(R.id.btn_save_host);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et = getActivity().findViewById(R.id.server_ip);
                final String serverIP = et.getText().toString();
                SharedPreferences sharedPref = getActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("SERVER_IP",  serverIP);
                editor.apply();
                Toast.makeText(getActivity(), "Saved: " + serverIP, Toast.LENGTH_SHORT).show();
            }
        });
    }
}

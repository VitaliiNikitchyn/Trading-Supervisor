package com.tradingsupervisor.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.tradingsupervisor.R;
import com.tradingsupervisor.ui.fragment.CameraFragment;

public class PhotoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.photo_activity_container, CameraFragment.newInstance())
                    //.addToBackStack(null)
                    .commit();
        }
    }
}

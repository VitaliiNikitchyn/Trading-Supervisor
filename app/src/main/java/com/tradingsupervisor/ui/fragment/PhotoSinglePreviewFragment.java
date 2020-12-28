package com.tradingsupervisor.ui.fragment;


import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.tradingsupervisor.R;
import com.tradingsupervisor.data.entity.Photo;
import com.tradingsupervisor.viewmodel.PhotoViewModel;

import java.io.File;


public class PhotoSinglePreviewFragment extends Fragment {

    public static final String TAG = "PhotoSinglePreviewFragment";
    private ImageView photoPreview;


    public PhotoSinglePreviewFragment() { }

    public static PhotoSinglePreviewFragment newInstance() {
        return new PhotoSinglePreviewFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getView() != null) return getView();
        View view = inflater.inflate(R.layout.fragment_photo_single_preview, container, false);
        photoPreview = view.findViewById(R.id.single_photo_preview_id);
        view.findViewById(R.id.removeBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhotoViewModel photoViewModel = new ViewModelProvider(getActivity()).get(PhotoViewModel.class);
                photoViewModel.removePhoto(photoViewModel.getCurrentPhoto().getValue());
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        PhotoViewModel photoViewModel = new ViewModelProvider(getActivity()).get(PhotoViewModel.class);
        photoViewModel.getCurrentPhoto().observe(getViewLifecycleOwner(), new Observer<Photo>() {
            @Override
            public void onChanged(Photo photo) {
                File file = new File(getActivity().getFilesDir(), photo.getFilename());
                Glide.with(PhotoSinglePreviewFragment.this).load(file).into(photoPreview);
                //photoPreview.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
            }
        });
    }

}

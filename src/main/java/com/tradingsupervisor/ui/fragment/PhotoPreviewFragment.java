package com.tradingsupervisor.ui.fragment;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.tradingsupervisor.R;
import com.tradingsupervisor.adapter.PhotoPreviewAdapter;
import com.tradingsupervisor.database.entity.PhotoPreview;
import com.tradingsupervisor.viewmodel.PhotoPreviewViewModel;
import com.tradingsupervisor.webApi.HttpClient;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PhotoPreviewFragment extends Fragment {

    public PhotoPreviewFragment() {}

    public static PhotoPreviewFragment newInstance() {
        return new PhotoPreviewFragment();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_preview, container, false);
        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.photo_preview_recyclerView);
        final PhotoPreviewAdapter adapter = new PhotoPreviewAdapter();
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),3));

        PhotoPreviewViewModel viewModel = ViewModelProviders.of(getActivity()).get(PhotoPreviewViewModel.class);
        viewModel.getPhotoPreviewList().observe(getActivity(), new Observer<List<PhotoPreview>>() {
            @Override
            public void onChanged(@Nullable List<PhotoPreview> photoPreviews) {
                adapter.submitList(photoPreviews);
                recyclerView.setAdapter(adapter);
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.btn_upload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadData();
            }
        });
    }

    private void uploadData() {
        SharedPreferences sharedPref = getActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE);
        String url = sharedPref.getString("SERVER_IP", null);

        if (url == null) {
            Toast.makeText(getActivity(), "enter server IP", Toast.LENGTH_SHORT).show();
            return;
        }


            PhotoPreviewViewModel viewModel = ViewModelProviders.of(getActivity()).get(PhotoPreviewViewModel.class);
            File f = new File(getActivity().getCacheDir(), "tmp.jpg");
            try {
                f.createNewFile();

                Bitmap bitmap = viewModel.getFirstPreview().getBitmap();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                byte[] bitmapdata = bos.toByteArray();

                FileOutputStream fos = new FileOutputStream(f);
                fos.write(bitmapdata);
                fos.flush();
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), f);
            MultipartBody.Part body = MultipartBody.Part.createFormData("image", f.getName(), reqFile);
            RequestBody name = RequestBody.create(MediaType.parse("text/plain"), "upload_test");

            HttpClient.getApi(url).postImage(body, name, "3").enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call,
                                       Response<ResponseBody> response) {
                    Toast.makeText(getActivity(), "uploaded", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e("error", t.getMessage());
                    Toast.makeText(getActivity(), "onFailure", Toast.LENGTH_SHORT).show();
                }
            });

    }
}

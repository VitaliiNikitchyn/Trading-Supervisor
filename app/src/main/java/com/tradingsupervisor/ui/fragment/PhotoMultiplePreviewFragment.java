package com.tradingsupervisor.ui.fragment;


import android.app.AlertDialog;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.tradingsupervisor.R;
import com.tradingsupervisor.adapter.PhotosPreviewAdapter;
import com.tradingsupervisor.data.entity.Photo;
import com.tradingsupervisor.data.entity.Shop;
import com.tradingsupervisor.viewmodel.PhotoViewModel;
import com.tradingsupervisor.webApi.ResponseStatus;

import java.util.List;



public class PhotoMultiplePreviewFragment extends Fragment
        implements PhotosPreviewAdapter.OnListItemClickListener {

    public static final String TAG = "PhotoMultiplePreviewFragment";
    private ImageButton btnUpload;
    private ImageButton btnClose;
    private ProgressBar photoUploadPb;
    private PhotosPreviewAdapter listAdapter;


    public PhotoMultiplePreviewFragment() {}

    public static PhotoMultiplePreviewFragment newInstance() {
        return new PhotoMultiplePreviewFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getView() != null) return getView();
        View view = inflater.inflate(R.layout.fragment_photo_multiple_preview, container, false);

        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        float dpWidth = metrics.widthPixels / metrics.density;
        int nOfColumns = (int) (dpWidth / 110); //120 is the width of grid item

        listAdapter = new PhotosPreviewAdapter(getActivity().getFilesDir(), this);
        RecyclerView recyclerView = view.findViewById(R.id.photos_preview_recyclerView);
        //recyclerView.addItemDecoration(new PhotosPreviewAdapter.MyItemDecoration(30));
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), nOfColumns));
        recyclerView.setAdapter(listAdapter);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnUpload = view.findViewById(R.id.btn_upload);
        btnClose = view.findViewById(R.id.btn_close);
        photoUploadPb = view.findViewById(R.id.photoUpload_progressBar);
        photoUploadPb.setIndeterminate(true);
        photoUploadPb.setVisibility(ProgressBar.GONE);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final Shop shop = getActivity().getIntent().getParcelableExtra(Shop.TAG);
        final PhotoViewModel photoViewModel = new ViewModelProvider(getActivity()).get(PhotoViewModel.class);
        photoViewModel.getPhotosByShopID(shop.getId()).observe(getViewLifecycleOwner(), new Observer<List<Photo>>() {
            @Override
            public void onChanged(@Nullable List<Photo> photos) {
                listAdapter.submitList(photos);
            }
        });

        photoViewModel.getUploadPhotoStatus().observe(getViewLifecycleOwner(), new Observer<ResponseStatus>() {
            @Override
            public void onChanged(@Nullable ResponseStatus status) {
                btnUpload.setClickable(true);

                if (status == null) return;
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setCancelable(false);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        getActivity().finish();
                    }
                });
                switch (status) {
                    case CLIENT_ERROR: {
                        builder.setMessage("Відсутнє з'єднання з інтернетом. " +
                                "Фотозвіти збережені та будуть відправлені пізніше");
                        builder.create().show();
                        break;
                    }
                    case SUCCESS: {
                        builder.setMessage("Фотозвіти успішно відправлені");
                        builder.create().show();
                        break;
                    }
                    case UNAUTHORIZED: {
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .add(R.id.start_activity_container,
                                        AuthenticationFragment.newInstance(), AuthenticationFragment.TAG)
                                .commit();
                        break;
                    }
                    case SERVER_ERROR: {
                        builder.setMessage("Помилка сервера. Зверніться до адміністраторів");
                        builder.create().show();
                        break;
                    }
                    case ERROR: {
                        builder.setMessage("Unknown error. Зверніться до адміністраторів");
                        builder.create().show();
                        break;
                    }
                }
                photoUploadPb.setVisibility(ProgressBar.GONE);
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View btn) {
                btn.setClickable(false);
                photoUploadPb.setVisibility(ProgressBar.VISIBLE);
                //Upload photos only from current shop
                photoViewModel.uploadPhotos(shop.getId());

                //Upload all cashed files
                //photoViewModel.uploadPhotos();
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }


    @Override //from PhotosPreviewAdapter.OnListItemClickListener
    public void onPhotoItemClick(Photo photo) {
        PhotoViewModel photoViewModel = new ViewModelProvider(getActivity()).get(PhotoViewModel.class);
        photoViewModel.setCurrentPhoto(photo);
        //PhotoSinglePreviewFragment
        Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag(PhotoSinglePreviewFragment.TAG);
        if (fragment == null)
            fragment = PhotoSinglePreviewFragment.newInstance();
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.photo_activity_container, fragment, PhotoSinglePreviewFragment.TAG)
                .addToBackStack(null)
                .commit();
    }

    /*
    @Override //from PhotosPreviewAdapter.OnListItemClickListener
    public void onRemoveItemClick(Photo photo) {
        PhotoViewModel photoViewModel = ViewModelProviders.of(getActivity()).get(PhotoViewModel.class);
        photoViewModel.removePhoto(photo);
    }*/
}

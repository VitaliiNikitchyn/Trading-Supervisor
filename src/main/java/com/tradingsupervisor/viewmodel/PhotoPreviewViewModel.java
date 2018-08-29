package com.tradingsupervisor.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.support.annotation.NonNull;

import com.tradingsupervisor.database.entity.PhotoPreview;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PhotoPreviewViewModel extends ViewModel {
    private MutableLiveData<List<PhotoPreview>> photoPreviewList;

    public PhotoPreviewViewModel() {
        photoPreviewList = new MutableLiveData<>();
        photoPreviewList.postValue(new ArrayList<PhotoPreview>());
    }

    public void addPhoto(Image image) {
        PhotoPreview preview = new PhotoPreview();

        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        preview.setBitmap(BitmapFactory.decodeByteArray(bytes,0,bytes.length,null));

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm:ss");
        preview.setStrDate(mdformat.format(calendar.getTime()));

        List<PhotoPreview> list = photoPreviewList.getValue();
        list.add(preview);
        photoPreviewList.postValue(list);
    }

    public LiveData<List<PhotoPreview>> getPhotoPreviewList() {
        return photoPreviewList;
    }

    public PhotoPreview getFirstPreview() {
        return photoPreviewList.getValue().get(0);
    }
}

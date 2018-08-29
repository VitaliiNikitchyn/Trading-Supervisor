package com.tradingsupervisor.database.entity;

import android.graphics.Bitmap;

public class PhotoPreview {

    private String strDate;
    private Bitmap bitmap;

    public String getStrDate() {
        return strDate;
    }

    public void setStrDate(String strDate) {
        this.strDate = strDate;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof PhotoPreview)) return false;

        PhotoPreview pr = (PhotoPreview) obj;
        if (strDate.equals(pr.getStrDate()) && bitmap.equals(pr.getBitmap())) {
            return true;
        }
        else return false;
    }

    @Override
    public int hashCode() {
        return strDate.hashCode() + bitmap.hashCode();
    }
}

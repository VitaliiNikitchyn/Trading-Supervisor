package com.tradingsupervisor.data.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.tradingsupervisor.data.typeconverter.DateToLongConverter;

import java.util.Date;


@Entity(tableName = "Shops")
public class Shop implements Parcelable {

    @Ignore
    public final static String TAG = "SHOP_OBJ_TAG";
    @Ignore
    public final static String SHOP_ID = "SHOP_ID";
    @Ignore
    public final static String SHOP_NAME = "SHOP_NAME";
    @Ignore
    public final static String SHOP_ADDRESS = "SHOP_ADDRESS";


    @SerializedName("id")
    @Expose
    @PrimaryKey(autoGenerate = true)
    private Long id;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("address")
    @Expose
    private String address;

    @SerializedName("lat")
    @Expose
    private Double latitude;

    @SerializedName("lon")
    @Expose
    private Double longitude;

    @SerializedName("work_radius")
    @Expose
    private float workRadius;

    //@Expose(deserialize = false, serialize = false)
    @SerializedName("last_visited")
    @Expose
    @TypeConverters({DateToLongConverter.class})
    private Date lastVisitedDate;


    //getters and setters
    public Long getId() { return id; }
    public void setName(String name) { this.name = name; }
    public void setAddress(String address) { this.address = address; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public void setWorkRadius(float workRadius) { this.workRadius = workRadius; }
    public void setLastVisitedDate(Date lastVisited) { this.lastVisitedDate = lastVisited; }

    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
    public float getWorkRadius() { return workRadius; }
    public Date getLastVisitedDate() { return lastVisitedDate; }

    //Parcelable implementation
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        //order is important here
        dest.writeLong(this.id);
        dest.writeString(this.name);
        dest.writeString(this.address);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
    }

    @Ignore
    public static final Creator<Shop> CREATOR = new Creator<Shop>() {
        @Override
        public Shop createFromParcel(Parcel parcel) {
            //order is important here
            Shop shop = new Shop();
            shop.id = parcel.readLong();
            shop.name = parcel.readString();
            shop.address = parcel.readString();
            shop.latitude = parcel.readDouble();
            shop.longitude = parcel.readDouble();
            return shop;
        }

        @Override
        public Shop[] newArray(int size) {
            return new Shop[size];
        }
    };
}
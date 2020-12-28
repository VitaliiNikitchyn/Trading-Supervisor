package com.tradingsupervisor.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import com.tradingsupervisor.data.typeconverter.DateToStringConverter;
import java.util.Date;


@Entity(tableName = "Photos")
public class Photo {

    @PrimaryKey(autoGenerate = true)
    private Long id;

    private String filename;

    @TypeConverters({DateToStringConverter.class})
    private Date creationTime;

    private Long shopID;

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) { this.filename = filename; }

    public Date getCreationTime() { return creationTime; }

    public void setCreationTime(Date creationTime) { this.creationTime = creationTime; }

    public Long getShopID() { return shopID; }

    public void setShopID(Long shopID) { this.shopID = shopID; }

}

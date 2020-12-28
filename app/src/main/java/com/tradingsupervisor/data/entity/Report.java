package com.tradingsupervisor.data.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import com.tradingsupervisor.data.typeconverter.DateToStringConverter;
import java.util.Date;


@Entity(tableName = "Reports", foreignKeys =
@ForeignKey(entity = Shop.class, parentColumns = "id", childColumns = "shopID",onDelete = ForeignKey.CASCADE))
public class Report {

    @PrimaryKey(autoGenerate = true)
    private Long id;

    @TypeConverters({DateToStringConverter.class})
    private Date date;

    private Long shopID;

    private Integer status_percent;

    private String status_message;


    public Long getId() { return id; }
    public Date getDate() { return date; }
    public Long getShopID() { return shopID; }
    public Integer getStatus_percent() { return status_percent; }
    public String getStatus_message() { return status_message; }

    public void setId(Long id) { this.id = id; }
    public void setDate(Date date) { this.date = date; }
    public void setShopID(Long shopID) { this.shopID = shopID; }
    public void setStatus_percent(Integer status_percent) { this.status_percent = status_percent; }
    public void setStatus_message(String status_message) { this.status_message = status_message; }
}

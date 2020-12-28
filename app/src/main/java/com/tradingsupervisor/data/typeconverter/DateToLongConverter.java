package com.tradingsupervisor.data.typeconverter;

import androidx.room.TypeConverter;
import java.util.Date;

public class DateToLongConverter {

    @TypeConverter
    public Date stringToDate(Long time) {
        return new Date(time);
    }

    @TypeConverter
    public Long dateToString(Date date) {
        return date.getTime();
    }
}


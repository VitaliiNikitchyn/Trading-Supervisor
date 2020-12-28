package com.tradingsupervisor.data.typeconverter;

import androidx.room.TypeConverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateToStringConverter {
    private SimpleDateFormat dateFormat =
            new SimpleDateFormat("dd-MM-yyyy HH:mm:ss Z", Locale.getDefault());

    @TypeConverter
    public Date stringToDate(String strDate) {
        try {
            return dateFormat.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    @TypeConverter
    public String dateToString(Date date) {
        return dateFormat.format(date);
    }
}

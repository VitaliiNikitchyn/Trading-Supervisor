package com.tradingsupervisor.data;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import com.tradingsupervisor.data.dao.PhotoDao;
import com.tradingsupervisor.data.dao.ReportDao;
import com.tradingsupervisor.data.dao.ShopDao;
import com.tradingsupervisor.data.entity.Photo;
import com.tradingsupervisor.data.entity.Report;
import com.tradingsupervisor.data.entity.Shop;


@Database(entities = {Photo.class, Shop.class, Report.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase INSTANCE;
    private static final String DB_NAME = "TradingSupervisor.db";
    private static final Object lock = new Object();


    public static AppDatabase getDatabase(final Context context) {
        synchronized (lock) {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context, //.getApplicationContext()
                        AppDatabase.class, DB_NAME)
                        .fallbackToDestructiveMigration()
                        .build();
            }
            return INSTANCE;
        }
    }

    public abstract PhotoDao photoDao();
    public abstract ShopDao shopDao();
    public abstract ReportDao reportDao();
}

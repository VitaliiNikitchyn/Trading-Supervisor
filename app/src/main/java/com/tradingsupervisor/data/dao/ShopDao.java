package com.tradingsupervisor.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.tradingsupervisor.data.entity.Shop;
import java.util.List;

@Dao
public interface ShopDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Shop> shops);

    @Query("DELETE FROM Shops")
    void deleteAll();

    @Query("UPDATE Shops SET lastVisitedDate = :visited_at WHERE id = :shopID")
    void updateLastVisitDate(Long shopID, long visited_at);

    @Query("SELECT * FROM Shops")
    LiveData<List<Shop>> getAllShops();

    /*
     * double latitude; double longitude -- current location
     * long currTimestamp -- new Date().getTime()
     * 86400000 = 60 * 60 * 1000 * 24 = 24 hours
     * 300000 = 5 min
     *
     * 0.0016 = 177.6m       => latitude  0.0001  = 11.1m
     * 0.0025 = 177.5m       => longitude 0.0001  = 7.1m
     */
    @Query("SELECT * FROM Shops WHERE ABS(latitude - :latitude) < 0.0001 * workRadius/11.1 " +
            "AND ABS(longitude - :longitude) < 0.0001 * workRadius/7.1 " +
            "AND (:currTimestamp - lastVisitedDate > 300000)")
    LiveData<List<Shop>> getNearestShops(double latitude, double longitude, long currTimestamp);

    @Query("SELECT COUNT(*) * 100 / (SELECT COUNT(*) FROM Shops) FROM Shops WHERE :currTimestamp - lastVisitedDate < 86400000")
    LiveData<Integer> getVisitPercentage(long currTimestamp);
}
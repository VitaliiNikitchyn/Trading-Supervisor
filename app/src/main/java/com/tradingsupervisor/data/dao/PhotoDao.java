package com.tradingsupervisor.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.tradingsupervisor.data.entity.Photo;
import java.util.List;

@Dao
public interface PhotoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Photo photo);

    /*
    @Query("DELETE FROM Photos WHERE shopID = :shopID")
    void removePhotosByShopID(Long shopID);*/

    @Delete
    void removePhoto(Photo photo);

    @Query("SELECT * FROM Photos WHERE shopID IS :shopID")
    LiveData<List<Photo>> getPhotosByShopIDAsync(Long shopID);

    @Query("SELECT * FROM Photos WHERE shopID IS :shopID ORDER BY creationTime ASC")   //LiveData async inside
    List<Photo> getPhotosByShopIDSync(Long shopID);

    @Query("SELECT shopID FROM Photos GROUP BY shopID")
    List<Long> getShopIDs();

    @Query("SELECT COUNT(*) FROM Photos")
    LiveData<Integer> getPhotoCount();
}

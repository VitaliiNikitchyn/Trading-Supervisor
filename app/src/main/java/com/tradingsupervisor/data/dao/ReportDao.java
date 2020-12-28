package com.tradingsupervisor.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;

import com.tradingsupervisor.data.entity.Report;

@Dao
public interface ReportDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Report report);
}

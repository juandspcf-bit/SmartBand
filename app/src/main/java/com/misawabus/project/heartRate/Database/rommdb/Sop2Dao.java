package com.misawabus.project.heartRate.Database.rommdb;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.misawabus.project.heartRate.Database.entities.Sop2;

import java.util.Date;

@Dao
public interface Sop2Dao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertSingleRow(Sop2 sop2);

    @Query("DELETE FROM sop2_table")
    void deleteAllData();

    @Query("SELECT * FROM sop2_table WHERE sop2_table.date_data == :dateData AND sop2_table.mac_address == :macAddress")
    LiveData<Sop2> getSingleRow(Date dateData, String macAddress);

    @Update
    void updateSingleRow(Sop2 sop2);

    @Delete
    void deleteSingleRow(Sop2 sop2);
}

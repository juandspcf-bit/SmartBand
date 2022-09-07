package com.misawabus.project.heartRate.Database.rommdb;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.misawabus.project.heartRate.constans.IdTypeDataTable;
import com.misawabus.project.heartRate.Database.entities.HeartRate;

import java.util.Date;
import java.util.List;

@Dao
public interface HeartRateDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertSingleHeartRateRow(HeartRate heartRate);

    @Query("DELETE FROM heart_rate_table")
    void deleteAllHeartRateData();

    @Query("SELECT * FROM heart_rate_table ORDER BY date_data LIMIT 3")
    LiveData<List<HeartRate>> getAllHeartRateData();

    @Query("SELECT COUNT(*) FROM heart_rate_table")
    LiveData<Integer> getCountHeartRateData();

    @Query("SELECT * FROM heart_rate_table WHERE heart_rate_table.date_data == :dateData AND heart_rate_table.mac_address == :macAddress ")
    LiveData<HeartRate> getSingleHeartRateRow(Date dateData, String macAddress);

    @Query("SELECT * FROM heart_rate_table WHERE heart_rate_table.date_data == :dateData AND heart_rate_table.mac_address == :macAddress AND heart_rate_table.id_table == :idTable")
    LiveData<HeartRate> getSingleHeartRateRowForU(Date dateData, String macAddress, IdTypeDataTable idTable);

    @Query("SELECT * FROM heart_rate_table WHERE heart_rate_table.date_data == :dateData AND heart_rate_table.mac_address == :macAddress ")
    LiveData<HeartRate> getSinglePDayHeartRateRow(Date dateData, String macAddress);

    @Update
    void updateSingleHeartRateRow(HeartRate heartRateRow);

    @Delete
    void deleteSingleHeartRateRow(HeartRate heartRateRow);

}

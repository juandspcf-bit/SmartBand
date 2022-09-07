package com.misawabus.project.heartRate.Database.rommdb;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.misawabus.project.heartRate.Database.entities.SleepDataUI;

import java.util.Date;
import java.util.List;
@Dao
public interface SleepDataUIDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertSingleRow(SleepDataUI sleepDataUI);

    @Query("DELETE FROM SleepDataUI_table")
    void deleteAllData();

    @Query("SELECT * FROM SleepDataUI_table")
    LiveData<List<SleepDataUI>> getAllData();

    @Query("SELECT * FROM SleepDataUI_table " +
            "WHERE SleepDataUI_table.date_data == :dateData " +
            "AND SleepDataUI_table.mac_address == :macAddress ")
    LiveData<List<SleepDataUI>> getListRows(Date dateData, String macAddress);

    @Query("SELECT * FROM SleepDataUI_table " +
            "WHERE SleepDataUI_table.date_data == :dateData " +
            "AND SleepDataUI_table.mac_address == :macAddress " +
            "AND SleepDataUI_table.index_list == :index ")
    LiveData<SleepDataUI> getSingleRowForU(Date dateData, String macAddress, int index);

    @Query("SELECT * FROM SleepDataUI_table WHERE SleepDataUI_table.date_data == :dateData AND SleepDataUI_table.mac_address == :macAddress ")
    LiveData<SleepDataUI> getSinglePDayRow(Date dateData, String macAddress);

    @Update
    void updateSingleRow(SleepDataUI sleepDataUI);

    @Delete
    void deleteSingleRow(SleepDataUI sleepDataUI);
}

package com.misawabus.project.heartRate.Database.rommdb;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.misawabus.project.heartRate.Database.entities.Sports;
import com.misawabus.project.heartRate.constans.IdTypeDataTable;

import java.util.Date;
import java.util.List;

@Dao
public interface SportsDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertSingleRow(Sports sports);

    @Query("DELETE FROM sports_table")
    void deleteAllData();

    @Query("SELECT * FROM sports_table")
    LiveData<List<Sports>> getAllData();

    @Query("SELECT * FROM sports_table WHERE sports_table.date_data == :dateData AND sports_table.mac_address == :macAddress ")
    LiveData<Sports> getSingleRow(Date dateData, String macAddress);

    @Query("SELECT * FROM sports_table WHERE sports_table.date_data == :dateData AND sports_table.mac_address == :macAddress AND sports_table.id_table == :idTypeDataTable")
    LiveData<Sports> getSingleRowForU(Date dateData, String macAddress, IdTypeDataTable idTypeDataTable);

    @Query("SELECT * FROM sports_table WHERE sports_table.date_data == :dateData AND sports_table.mac_address == :macAddress ")
    LiveData<Sports> getSinglePDayRow(Date dateData, String macAddress);

    @Update
    void updateSingleRow(Sports sports);

    @Delete
    void deleteSingleRow(Sports sports);

}

package com.misawabus.project.heartRate.Database.rommdb;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.misawabus.project.heartRate.Database.entities.Temperature;
import com.misawabus.project.heartRate.constans.IdTypeDataTable;

import java.util.Date;
@Dao
public interface TemperatureDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertSingleRow(Temperature bloodPressure);

    @Query("DELETE FROM temperature_table")
    void deleteAllData();

    @Query("SELECT * FROM temperature_table WHERE temperature_table.date_data == :dateData AND temperature_table.mac_address == :macAddress AND temperature_table.id_table == :idTypeDataTable")
    LiveData<Temperature> getSingleRowForU(Date dateData, String macAddress, IdTypeDataTable idTypeDataTable);

    @Query("SELECT * FROM temperature_table WHERE temperature_table.date_data == :dateData AND temperature_table.mac_address == :macAddress ")
    LiveData<Temperature> getSinglePDayRow(Date dateData, String macAddress);

    @Update
    void updateSingleRow(Temperature bloodPressureRow);

    @Delete
    void deleteSingleRow(Temperature bloodPressureRow);
}

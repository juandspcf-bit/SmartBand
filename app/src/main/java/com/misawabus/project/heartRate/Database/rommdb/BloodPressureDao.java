package com.misawabus.project.heartRate.Database.rommdb;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.misawabus.project.heartRate.constans.IdTypeDataTable;
import com.misawabus.project.heartRate.Database.entities.BloodPressure;


import java.util.Date;
import java.util.List;
@Dao
public interface BloodPressureDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertSingleRow(BloodPressure bloodPressure);

    @Query("DELETE FROM bp_table")
    void deleteAllData();

    @Query("SELECT * FROM bp_table WHERE bp_table.date_data == :dateData AND bp_table.mac_address == :macAddress AND bp_table.id_table == :idTypeDataTable")
    LiveData<BloodPressure> getSingleRowForU(Date dateData, String macAddress, IdTypeDataTable idTypeDataTable);

    @Query("SELECT * FROM bp_table WHERE bp_table.date_data == :dateData AND bp_table.mac_address == :macAddress ")
    LiveData<BloodPressure> getSinglePDayRow(Date dateData, String macAddress);

    @Update
    void updateSingleRow(BloodPressure bloodPressureRow);

    @Delete
    void deleteSingleRow(BloodPressure bloodPressureRow);
}

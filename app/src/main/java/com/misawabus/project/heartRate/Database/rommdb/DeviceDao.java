package com.misawabus.project.heartRate.Database.rommdb;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.misawabus.project.heartRate.Database.entities.Device;

import java.util.List;

@Dao
public interface DeviceDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertSingleDeviceRow( Device device);

    @Query("DELETE FROM device_table")
    void deleteDeviceData();

    @Query("SELECT * FROM device_table")
    LiveData<List<Device>> getAllDeviceData();

    @Query("SELECT * FROM device_table WHERE device_table.mac_address == :macAddress")
    LiveData<Device> getSingleDeviceRow(String macAddress);

    @Update
    int updateSingleDeviceRow(Device deviceRow);

    @Delete
    void deleteSingleDeviceRow(Device deviceRow);
}

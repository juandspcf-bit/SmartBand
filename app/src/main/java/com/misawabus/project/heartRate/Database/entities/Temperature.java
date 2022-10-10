package com.misawabus.project.heartRate.Database.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.misawabus.project.heartRate.constans.IdTypeDataTable;

import java.util.Date;

@Entity(tableName = "temperature_table")
public class Temperature {
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    public long Id;

    @ColumnInfo(name = "id_table")
    public IdTypeDataTable idTypeDataTable;

    @ColumnInfo(name = "mac_address")
    public String macAddress;

    @ColumnInfo(name = "date_data")
    public Date dateData;

    @ColumnInfo(name = "data")
    public String data;

    @Override
    public String toString() {
        return "Temperature{" +
                "Id=" + Id +
                ", idTypeDataTable=" + idTypeDataTable +
                ", macAddress='" + macAddress + '\'' +
                ", dateData=" + dateData +
                ", data='" + data + '\'' +
                '}';
    }

    public long getId() {
        return Id;
    }

    public void setId(long id) {
        Id = id;
    }

    public IdTypeDataTable getIdTypeDataTable() {
        return idTypeDataTable;
    }

    public void setIdTypeDataTable(IdTypeDataTable idTypeDataTable) {
        this.idTypeDataTable = idTypeDataTable;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public Date getDateData() {
        return dateData;
    }

    public void setDateData(Date dateData) {
        this.dateData = dateData;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}

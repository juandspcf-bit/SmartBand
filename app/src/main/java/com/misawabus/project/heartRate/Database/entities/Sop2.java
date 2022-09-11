package com.misawabus.project.heartRate.Database.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.misawabus.project.heartRate.constans.IdTypeDataTable;

import java.util.Date;
@Entity(tableName = "sop2_table")
public class Sop2 {
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    private long Id;

    @ColumnInfo(name = "id_table")
    private IdTypeDataTable idTypeDataTable;

    @ColumnInfo(name = "mac_address")
    private String macAddress;

    @ColumnInfo(name = "date_data")
    private Date dateData;

    @ColumnInfo(name = "data")
    private String data;

    public Sop2() {
    }

    public Sop2(long id, IdTypeDataTable idTypeDataTable, String macAddress, Date dateData, String data) {
        Id = id;
        this.idTypeDataTable = idTypeDataTable;
        this.macAddress = macAddress;
        this.dateData = dateData;
        this.data = data;
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

    @Override
    public String toString() {
        return "Sop2{" +
                "Id=" + Id +
                ", idTypeDataTable=" + idTypeDataTable +
                ", macAddress='" + macAddress + '\'' +
                ", dateData=" + dateData +
                ", data='" + data + '\'' +
                '}';
    }
}

package com.misawabus.project.heartRate.Database.entities;

import androidx.room.ColumnInfo;

import com.misawabus.project.heartRate.constans.IdTypeDataTable;

import java.util.Date;


public class ListData {
    @ColumnInfo(name = "id")
    public long id;

    @ColumnInfo(name = "id_table")
    public IdTypeDataTable id_table;

    @ColumnInfo(name = "mac_address")
    public String macAddress;

    @ColumnInfo(name = "date_data")
    public Date dateData;

    @ColumnInfo(name = "data")
    public String data;

    public ListData(){

    }

    public ListData(long id, IdTypeDataTable idTypeDataTable, String macAddress, Date dateData, String data) {
        this.id = id;
        this.id_table = idTypeDataTable;
        this.macAddress = macAddress;
        this.dateData = dateData;
        this.data = data;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public IdTypeDataTable getIdTable() {
        return id_table;
    }

    public void setIdTable(IdTypeDataTable idTypeDataTable) {
        this.id_table = idTypeDataTable;
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
        return "ListData{" +
                "id=" + id +
                ", id_table=" + id_table +
                ", macAddress='" + macAddress + '\'' +
                ", dateData=" + dateData +
                ", data='" + data + '\'' +
                '}';
    }
}

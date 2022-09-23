package com.misawabus.project.heartRate.Database.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.misawabus.project.heartRate.constans.IdTypeDataTable;
import com.veepoo.protocol.model.datas.InsomniaTimeData;

import java.util.Date;
import java.util.List;

@Entity(tableName = "SleepDataUI_table")
public class SleepDataUI {
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    public long Id;

    @ColumnInfo(name = "id_table")
    public IdTypeDataTable idTypeDataTable;

    @ColumnInfo(name = "mac_address")
    public String macAddress;

    @ColumnInfo(name = "index_list")
    public int index;

    @ColumnInfo(name = "date_data")
    public Date dateData;

    @ColumnInfo(name = "caliFlag")
    public int caliFlag;

    @ColumnInfo(name = "sleepQuality")
    public int sleepQuality;

    @ColumnInfo(name = "wakeCount")
    public int wakeCount;

    @ColumnInfo(name = "deepSleepTime")
    public int deepSleepTime;

    @ColumnInfo(name = "lowSleepTime")
    public int lowSleepTime;

    @ColumnInfo(name = "allSleepTime")
    public int allSleepTime;

    @ColumnInfo(name = "data")
    public String data;

    @ColumnInfo(name = "sleepDown")
    public String sleepDown;

    @ColumnInfo(name = "sleepUp")
    public String sleepUp;

    //High precision sleep data

    @ColumnInfo(name = "sleepTag")
    private int sleepTag;

    @ColumnInfo(name = "getUpScore")
    private int getUpScore;

    @ColumnInfo(name = "deepScore")
    private int deepScore;

    @ColumnInfo(name = "sleepEfficiencyScore")
    private int sleepEfficiencyScore;

    @ColumnInfo(name = "fallAsleepScore")
    private int fallAsleepScore;

    @ColumnInfo(name = "sleepTimeScore")
    private int sleepTimeScore;

    @ColumnInfo(name = "exitSleepMode")
    private int exitSleepMode;

    @ColumnInfo(name = "deepAndLightMode")
    private int deepAndLightMode;

    @ColumnInfo(name = "otherDuration")
    private int otherDuration;

    @ColumnInfo(name = "firstDeepDuration")
    private int firstDeepDuration;

    @ColumnInfo(name = "getUpDuration")
    private int getUpDuration;

    @ColumnInfo(name = "getUpToDeepAve")
    private int getUpToDeepAve;

    @ColumnInfo(name = "onePointDuration")
    private int onePointDuration;

    @ColumnInfo(name = "accurateType")
    private int accurateType;

    @ColumnInfo(name = "insomniaTag")
    private int insomniaTag;

    @ColumnInfo(name = "insomniaScore")
    private int insomniaScore;

    @ColumnInfo(name = "insomniaTimes")
    private int insomniaTimes;

    @ColumnInfo(name = "insomniaLength")
    private int insomniaLength;

    @ColumnInfo(name = "insomniaBeanList")
    private List<InsomniaTimeData> insomniaBeanList;

    @ColumnInfo(name = "startInsomniaTime")
    public String startInsomniaTime;

    @ColumnInfo(name = "stopInsomniaTime")
    public String stopInsomniaTime;

    @ColumnInfo(name = "insomniaDuration")
    private int insomniaDuration;




    @NonNull
    @Override
    public String toString() {
        return "SleepDataUI{" +
                "Id=" + Id +
                ", idTypeDataTable=" + idTypeDataTable +
                ", macAddress='" + macAddress + '\'' +
                ", dateData=" + dateData +
                ", index=" + index +
                ", data='" + data + '\'' +
                ", caliFlag=" + caliFlag +
                ", sleepQuality=" + sleepQuality +
                ", wakeCount=" + wakeCount +
                ", deepSleepTime=" + deepSleepTime +
                ", lowSleepTime=" + lowSleepTime +
                ", allSleepTime=" + allSleepTime +
                ", sleepDown='" + sleepDown + '\'' +
                ", sleepUp='" + sleepUp + '\'' +
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

    public int getCaliFlag() {
        return caliFlag;
    }

    public void setCaliFlag(int caliFlag) {
        this.caliFlag = caliFlag;
    }

    public int getSleepQuality() {
        return sleepQuality;
    }

    public void setSleepQuality(int sleepQuality) {
        this.sleepQuality = sleepQuality;
    }

    public int getWakeCount() {
        return wakeCount;
    }

    public void setWakeCount(int wakeCount) {
        this.wakeCount = wakeCount;
    }

    public int getDeepSleepTime() {
        return deepSleepTime;
    }

    public void setDeepSleepTime(int deepSleepTime) {
        this.deepSleepTime = deepSleepTime;
    }

    public int getLowSleepTime() {
        return lowSleepTime;
    }

    public void setLowSleepTime(int lowSleepTime) {
        this.lowSleepTime = lowSleepTime;
    }

    public int getAllSleepTime() {
        return allSleepTime;
    }

    public void setAllSleepTime(int allSleepTime) {
        this.allSleepTime = allSleepTime;
    }

    public String getSleepDown() {
        return sleepDown;
    }

    public void setSleepDown(String sleepDown) {
        this.sleepDown = sleepDown;
    }

    public String getSleepUp() {
        return sleepUp;
    }

    public void setSleepUp(String sleepUp) {
        this.sleepUp = sleepUp;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}

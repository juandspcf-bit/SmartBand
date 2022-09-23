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


    @Override
    public String toString() {
        return "SleepDataUI{" +
                "Id=" + Id +
                ", idTypeDataTable=" + idTypeDataTable +
                ", macAddress='" + macAddress + '\'' +
                ", index=" + index +
                ", dateData=" + dateData +
                ", caliFlag=" + caliFlag +
                ", sleepQuality=" + sleepQuality +
                ", wakeCount=" + wakeCount +
                ", deepSleepTime=" + deepSleepTime +
                ", lowSleepTime=" + lowSleepTime +
                ", allSleepTime=" + allSleepTime +
                ", data='" + data + '\'' +
                ", sleepDown='" + sleepDown + '\'' +
                ", sleepUp='" + sleepUp + '\'' +
                ", sleepTag=" + sleepTag +
                ", getUpScore=" + getUpScore +
                ", deepScore=" + deepScore +
                ", sleepEfficiencyScore=" + sleepEfficiencyScore +
                ", fallAsleepScore=" + fallAsleepScore +
                ", sleepTimeScore=" + sleepTimeScore +
                ", exitSleepMode=" + exitSleepMode +
                ", deepAndLightMode=" + deepAndLightMode +
                ", otherDuration=" + otherDuration +
                ", firstDeepDuration=" + firstDeepDuration +
                ", getUpDuration=" + getUpDuration +
                ", getUpToDeepAve=" + getUpToDeepAve +
                ", onePointDuration=" + onePointDuration +
                ", accurateType=" + accurateType +
                ", insomniaTag=" + insomniaTag +
                ", insomniaScore=" + insomniaScore +
                ", insomniaTimes=" + insomniaTimes +
                ", insomniaLength=" + insomniaLength +
                ", insomniaBeanList=" + insomniaBeanList +
                ", startInsomniaTime='" + startInsomniaTime + '\'' +
                ", stopInsomniaTime='" + stopInsomniaTime + '\'' +
                ", insomniaDuration=" + insomniaDuration +
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

    public int getSleepTag() {
        return sleepTag;
    }

    public void setSleepTag(int sleepTag) {
        this.sleepTag = sleepTag;
    }

    public int getGetUpScore() {
        return getUpScore;
    }

    public void setGetUpScore(int getUpScore) {
        this.getUpScore = getUpScore;
    }

    public int getDeepScore() {
        return deepScore;
    }

    public void setDeepScore(int deepScore) {
        this.deepScore = deepScore;
    }

    public int getSleepEfficiencyScore() {
        return sleepEfficiencyScore;
    }

    public void setSleepEfficiencyScore(int sleepEfficiencyScore) {
        this.sleepEfficiencyScore = sleepEfficiencyScore;
    }

    public int getFallAsleepScore() {
        return fallAsleepScore;
    }

    public void setFallAsleepScore(int fallAsleepScore) {
        this.fallAsleepScore = fallAsleepScore;
    }

    public int getSleepTimeScore() {
        return sleepTimeScore;
    }

    public void setSleepTimeScore(int sleepTimeScore) {
        this.sleepTimeScore = sleepTimeScore;
    }

    public int getExitSleepMode() {
        return exitSleepMode;
    }

    public void setExitSleepMode(int exitSleepMode) {
        this.exitSleepMode = exitSleepMode;
    }

    public int getDeepAndLightMode() {
        return deepAndLightMode;
    }

    public void setDeepAndLightMode(int deepAndLightMode) {
        this.deepAndLightMode = deepAndLightMode;
    }

    public int getOtherDuration() {
        return otherDuration;
    }

    public void setOtherDuration(int otherDuration) {
        this.otherDuration = otherDuration;
    }

    public int getFirstDeepDuration() {
        return firstDeepDuration;
    }

    public void setFirstDeepDuration(int firstDeepDuration) {
        this.firstDeepDuration = firstDeepDuration;
    }

    public int getGetUpDuration() {
        return getUpDuration;
    }

    public void setGetUpDuration(int getUpDuration) {
        this.getUpDuration = getUpDuration;
    }

    public int getGetUpToDeepAve() {
        return getUpToDeepAve;
    }

    public void setGetUpToDeepAve(int getUpToDeepAve) {
        this.getUpToDeepAve = getUpToDeepAve;
    }

    public int getOnePointDuration() {
        return onePointDuration;
    }

    public void setOnePointDuration(int onePointDuration) {
        this.onePointDuration = onePointDuration;
    }

    public int getAccurateType() {
        return accurateType;
    }

    public void setAccurateType(int accurateType) {
        this.accurateType = accurateType;
    }

    public int getInsomniaTag() {
        return insomniaTag;
    }

    public void setInsomniaTag(int insomniaTag) {
        this.insomniaTag = insomniaTag;
    }

    public int getInsomniaScore() {
        return insomniaScore;
    }

    public void setInsomniaScore(int insomniaScore) {
        this.insomniaScore = insomniaScore;
    }

    public int getInsomniaTimes() {
        return insomniaTimes;
    }

    public void setInsomniaTimes(int insomniaTimes) {
        this.insomniaTimes = insomniaTimes;
    }

    public int getInsomniaLength() {
        return insomniaLength;
    }

    public void setInsomniaLength(int insomniaLength) {
        this.insomniaLength = insomniaLength;
    }

    public List<InsomniaTimeData> getInsomniaBeanList() {
        return insomniaBeanList;
    }

    public void setInsomniaBeanList(List<InsomniaTimeData> insomniaBeanList) {
        this.insomniaBeanList = insomniaBeanList;
    }

    public String getStartInsomniaTime() {
        return startInsomniaTime;
    }

    public void setStartInsomniaTime(String startInsomniaTime) {
        this.startInsomniaTime = startInsomniaTime;
    }

    public String getStopInsomniaTime() {
        return stopInsomniaTime;
    }

    public void setStopInsomniaTime(String stopInsomniaTime) {
        this.stopInsomniaTime = stopInsomniaTime;
    }

    public int getInsomniaDuration() {
        return insomniaDuration;
    }

    public void setInsomniaDuration(int insomniaDuration) {
        this.insomniaDuration = insomniaDuration;
    }
}

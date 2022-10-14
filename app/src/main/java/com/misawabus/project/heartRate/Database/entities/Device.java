package com.misawabus.project.heartRate.Database.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "device_table")
public class Device {

    @ColumnInfo(name = "device_id")
    @PrimaryKey(autoGenerate = true)
    public long deviceId;

    @ColumnInfo(name = "mac_address")
    public String macAddress;

    @ColumnInfo(name = "name")
    public String name;
    @ColumnInfo(name = "weight")
    public String weight;
    @ColumnInfo(name = "gender")
    public String gender;
    @ColumnInfo(name = "birth_date")
    public Date birthDate;
    @ColumnInfo(name = "height")
    public String height;
    @ColumnInfo(name = "steps_goal")
    public String stepsGoal;

    public Device(){

    }

    public long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(long deviceId) {
        this.deviceId = deviceId;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getWeight() {
        return weight;
    }
    public void setWeight(String weight) {
        this.weight = weight;
    }

    public Date getBirthDate() {
        return birthDate;
    }
    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getHeight() {
        return height;
    }
    public void setHeight(String height) {
        this.height = height;
    }

    public String getStepsGoal() {
        return stepsGoal;
    }
    public void setStepsGoal(String stepsGoal) {
        this.stepsGoal = stepsGoal;
    }

    @Override
    public String toString() {
        return "Device{" +
                "deviceId=" + deviceId +
                ", macAddress='" + macAddress + '\'' +
                ", name='" + name + '\'' +
                ", weight='" + weight + '\'' +
                ", gender='" + gender + '\'' +
                ", birthDate=" + birthDate +
                ", height='" + height + '\'' +
                ", stepsGoal=" + stepsGoal +
                '}';
    }
}

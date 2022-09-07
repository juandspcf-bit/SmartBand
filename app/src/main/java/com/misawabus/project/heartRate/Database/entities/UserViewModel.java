package com.misawabus.project.heartRate.Database.entities;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class UserViewModel extends AndroidViewModel {
    private final MutableLiveData<String> macAddress= new MutableLiveData<>();
    private final MutableLiveData<String> name= new MutableLiveData<>();
    private final MutableLiveData<String> gender= new MutableLiveData<>();
    private final MutableLiveData<String> birthDay= new MutableLiveData<>();
    private final MutableLiveData<String> weight= new MutableLiveData<>();
    private final MutableLiveData<String> height= new MutableLiveData<>();


    public UserViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<String> getMacAddress() {
        return macAddress;
    }
    public void setMacAddress(String macAddress){
        this.macAddress.setValue(macAddress);
    }

    public MutableLiveData<String> getName() {
        return name;
    }
    public void setName(String name){
        this.name.setValue(name);
    }

    public MutableLiveData<String> getGender() {
        return gender;
    }
    public void setGender(String gender){this.gender.setValue(gender); }

    public MutableLiveData<String> getBirthDay() {
        return birthDay;
    }
    public void setBirthDay(String birthDay){
        this.birthDay.setValue(birthDay);
    }

    public MutableLiveData<String> getWeight() {
        return weight;
    }
    public void setWeight(String weight){
        this.weight.setValue(weight);
    }

    public MutableLiveData<String> getHeight() {
        return height;
    }
    public void setHeight(String height){
        this.height.setValue(height);
    }

}

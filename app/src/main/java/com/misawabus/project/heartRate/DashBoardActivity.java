package com.misawabus.project.heartRate;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.lifecycle.ViewModelProvider;

import com.inuker.bluetooth.library.Constants;
import com.misawabus.project.heartRate.Database.entities.Device;
import com.misawabus.project.heartRate.Utils.DBops;
import com.misawabus.project.heartRate.Utils.DateUtils;
import com.misawabus.project.heartRate.databinding.ActivityDashBoardV2Binding;
import com.misawabus.project.heartRate.device.config.DeviceConfig;
import com.misawabus.project.heartRate.device.config.DeviceSettings;
import com.misawabus.project.heartRate.device.readData.HealthsData;
import com.misawabus.project.heartRate.device.readRealTimeData.RealTimeTesterClass;
import com.misawabus.project.heartRate.fragments.MainDashBoardFragment;
import com.misawabus.project.heartRate.viewModels.DashBoardViewModel;
import com.misawabus.project.heartRate.viewModels.DeviceViewModel;
import com.orhanobut.logger.Logger;
import com.veepoo.protocol.VPOperateManager;
import com.veepoo.protocol.listener.base.IABleConnectStatusListener;

import java.time.LocalDate;

public class DashBoardActivity extends AppCompatActivity {
    private final static String TAG = DashBoardActivity.class.getSimpleName();
    private ActivityDashBoardV2Binding binding;
    private DashBoardViewModel dashBoardViewModel;


    ConnectivityManager connectivityManager;
    ConnectivityManager.NetworkCallback myCallBack= new ConnectivityManager.NetworkCallback(){
        @Override
        public void onAvailable(@NonNull Network network) {
            super.onAvailable(network);
            dashBoardViewModel.setWiFiEnable(true);
        }

        @Override
        public void onLost(@NonNull Network network) {
            super.onLost(network);
            dashBoardViewModel.setWiFiEnable(false);
        }
    };
    private VPOperateManager mVpoperateManager;


    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashBoardV2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle extras = getIntent().getExtras();
        String macAddress = extras.getString("deviceAddress");

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        hideBottomNavigationBar();
        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {



            }
        });

        System.setProperty("org.apache.poi.javax.xml.stream.XMLInputFactory", "com.fasterxml.aalto.stax.InputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLOutputFactory", "com.fasterxml.aalto.stax.OutputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLEventFactory", "com.fasterxml.aalto.stax.EventFactoryImpl");

        mVpoperateManager = VPOperateManager.getMangerInstance(getApplicationContext());
        DBops dbHandler = new DBops();
        dbHandler.initViewModels(this);
        DeviceSettings deviceSettings = new DeviceSettings(getApplicationContext(), this);
        HealthsData healthsData = new HealthsData(getApplicationContext(), this);
        RealTimeTesterClass realTimeTesterClass = new RealTimeTesterClass(getApplicationContext(),this, getApplication());
        DeviceConfig.enableDevice(getApplicationContext(), this, healthsData);

        dashBoardViewModel = new ViewModelProvider(this).get(DashBoardViewModel.class);
        dashBoardViewModel.setHealthsReadDataManager(healthsData);
        dashBoardViewModel.setDeviceSettingManager(deviceSettings);
        dashBoardViewModel.setRealTimeTesterClass(realTimeTesterClass);
        dashBoardViewModel.setIsConnected(true);
        dashBoardViewModel.setIsEnableFeatures(false);
        DeviceViewModel deviceViewModel = new ViewModelProvider(this).get(DeviceViewModel.class);
        deviceViewModel.setMacAddress(macAddress);

        DeviceViewModel.getSingleDeviceRow(macAddress).observe(this, deviceDB -> {
            if (deviceDB == null){
                dashBoardViewModel.setDevice(new Device());
                return;
            }
            dashBoardViewModel.setDevice(deviceDB);
            LocalDate localDateBirthDate = DateUtils.getLocalDate(deviceDB.getBirthDate(), "-");
            int age = LocalDate.now().getYear() - localDateBirthDate.getYear();
            dashBoardViewModel.setAge(age);
        });


/*        DeviceViewModel.getSingleDeviceRow(macAddress).observe(this, device -> {
            if (device == null || device.getBirthDate() == null) return;
            LocalDate localDateBirthDate = DateUtils.getLocalDate(device.getBirthDate(), "-");
            int age = LocalDate.now().getYear() - localDateBirthDate.getYear();
            dashBoardViewModel.setAge(age);
        });*/

        VPOperateManager.getMangerInstance(this).registerConnectStatusListener(macAddress, new IABleConnectStatusListener() {

            @Override
            public void onConnectStatusChanged(String mac, int status) {
                if (status == Constants.STATUS_CONNECTED) {
                    Logger.t(TAG).i("STATUS_CONNECTED");
                } else if (status == Constants.STATUS_DISCONNECTED) {
                    dashBoardViewModel.setIsConnected(false);
                    Logger.t(TAG).i("STATUS_DISCONNECTED..........");
                }
            }
        });

        getSupportFragmentManager().beginTransaction().replace(R.id.mainDashBoardFragmentContainerInActivityDashBoard, new MainDashBoardFragment()).commit();



    }

    private void hideBottomNavigationBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsControllerCompat windowInsetsController =
                    WindowCompat.getInsetsController(getWindow(), binding.mainDashBoardFragmentContainerInActivityDashBoard);
            windowInsetsController.setSystemBarsBehavior(
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            );
            windowInsetsController.hide(WindowInsetsCompat.Type.navigationBars());
        }else {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    }


    private void registerCallBack(){
        connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        connectivityManager.registerDefaultNetworkCallback(myCallBack);
    }

    private void unRegisterCallBack(){
        if(connectivityManager==null) return;
        connectivityManager.unregisterNetworkCallback(myCallBack);
    }

    @Override
    public void onResume() {
        super.onResume();
        hideBottomNavigationBar();
        registerCallBack();
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        DeviceConfig.disconnectDevice(getBaseContext());
        unRegisterCallBack();
        finish();
        finishAndRemoveTask();
    }




}
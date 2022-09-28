package com.misawabus.project.heartRate;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.companion.CompanionDeviceManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;
import com.inuker.bluetooth.library.Code;
import com.inuker.bluetooth.library.model.BleGattProfile;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.utils.BluetoothUtils;
import com.misawabus.project.heartRate.Utils.BluetoothMustPermissions;
import com.misawabus.project.heartRate.adapters.BleScanViewAdapter;
import com.misawabus.project.heartRate.adapters.CustomLogAdapter;
import com.misawabus.project.heartRate.adapters.recyclerView.OnViewClickedCallback;
import com.misawabus.project.heartRate.viewModels.ScanConnectionViewModel;
import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;
import com.veepoo.protocol.VPOperateManager;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.base.IConnectResponse;
import com.veepoo.protocol.listener.base.INotifyResponse;
import com.veepoo.protocol.util.VPLogger;

import java.util.ArrayList;
import java.util.List;

public class ScanConnectionActivity extends AppCompatActivity implements OnViewClickedCallback {
    private final static String TAG = ScanConnectionActivity.class.getSimpleName();
    private final static String YOUR_APPLICATION = "timaimee";
    private static final int SELECT_DEVICE_REQUEST_CODE = 20000;
    private static final int LOCATION_PER_CODE = 20001;
    Context mContext = ScanConnectionActivity.this;
    private final int REQUEST_CODE = 1;
    List<SearchResult> mListData = new ArrayList<>();
    List<String> mListAddress = new ArrayList<>();
    BleScanViewAdapter bleConnectAdatpter;

    private BluetoothAdapter mBAdapter;
    RecyclerView mRecyclerView;
    TextView mTitleTextView;
    VPOperateManager mVpoperateManager;
    private boolean mIsOadModel;

    private final String grantAccessCollection = "grantAccessCollection";

    private ScanConnectionViewModel scanConnectionViewModel;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean isConnectionInProcess = false;


    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        swipeRefreshLayout = findViewById(R.id.devices_refresh_layout);
        swipeRefreshLayout.setEnabled(true);
        swipeRefreshLayout.setSize(SwipeRefreshLayout.LARGE);
        swipeRefreshLayout.setProgressViewEndTarget(true, 512);
        scanConnectionViewModel = new ViewModelProvider(this).get(ScanConnectionViewModel.class);
        initBluetoothLocalDevice();


        initLog();
        mVpoperateManager = VPOperateManager.getMangerInstance(mContext.getApplicationContext());
        VPLogger.setDebug(true);


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                bluetoothMustPermissions.check_BLUETOOTH_SCAN_Permission();
                if (bluetoothAdapter.isDiscovering()) {
                    bluetoothAdapter.cancelDiscovery();
                }

                if (checkBLE()) {
                    if (!mListAddress.isEmpty()) {
                        mListAddress.clear();
                    }
                    if (!mListData.isEmpty()) {
                        mListData.clear();
                    }

                    if (!BluetoothUtils.isBluetoothEnabled()) {
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(mContext, "蓝牙没有开启", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    startRemoteDevicesDiscoveryProcess();

                }

            }
        });


        scanConnectionViewModel.getRecyclerViewData().observe(this, new Observer<>() {
            @Override
            public void onChanged(List<SearchResult> searchResults) {
                initRecycleView(searchResults);
            }
        });


    }


    private void initRecycleView(List<SearchResult> searchResults) {
        mRecyclerView = findViewById(R.id.main_recylerlist);
        mTitleTextView = findViewById(R.id.main_title);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        bleConnectAdatpter = new BleScanViewAdapter(this, searchResults);

        mRecyclerView.setAdapter(bleConnectAdatpter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        bleConnectAdatpter.setBleItemOnclick(this);
        mTitleTextView.setText(R.string.devices_list);
    }

    @Override
    public void OnViewClicked(int position) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, "正在连接，请稍等...", Toast.LENGTH_SHORT).show();
            }
        });

        if (isConnectionInProcess) return;

        bluetoothMustPermissions.check_BLUETOOTH_SCAN_Permission();
        bluetoothAdapter.cancelDiscovery();
        bluetoothAdapter.cancelDiscovery();
        swipeRefreshLayout.setRefreshing(false);
        SearchResult searchResult = mListData.get(position);
        isConnectionInProcess = true;
        connectDevice(searchResult.getAddress(), searchResult.getName());
    }


    private void initLog() {
        Logger.init(YOUR_APPLICATION)
                .methodCount(0)
                .methodOffset(0)
                .hideThreadInfo()
                .logLevel(LogLevel.FULL)
                .logAdapter(new CustomLogAdapter());
    }


    private void initBLE() {
        Log.d("InsideINIT", ".........");
        BluetoothManager mBManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if (null != mBManager) {
            mBAdapter = mBManager.getAdapter();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

        }
        checkBLE();

    }

    private boolean checkBLE() {
        if (!BluetoothUtils.isBluetoothEnabled()) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_CODE);
            }
            return false;
        } else {

            return true;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(deviceFound);
    }

    private void initBluetoothLocalDevice() {
        bluetoothMustPermissions.check_BLUETOOTH_LOCATION_Permission();
        BluetoothManager systemService = (BluetoothManager) getBaseContext().getSystemService(Context.BLUETOOTH_SERVICE);
        systemService.getAdapter();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        String nameAdapter = bluetoothAdapter.getName();
        Log.d("nameAdapter", nameAdapter);
        if (bluetoothAdapter == null) {
            Log.d("NoBluetooth", "bluetooth is not supported");
            return;
        }
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_CODE);
        }

    }


    private void startRemoteDevicesDiscoveryProcess() {
        IntentFilter filter1 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        registerReceiver(discoveryProcess, filter1);

        IntentFilter filter2 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(discoveryProcess, filter2);

        IntentFilter filter3 = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(deviceFound, filter3);

        bluetoothMustPermissions.check_BLUETOOTH_SCAN_Permission();
        bluetoothAdapter.startDiscovery();
    }

    private final BroadcastReceiver discoveryProcess = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals("android.bluetooth.adapter.action.DISCOVERY_STARTED")) {
                Log.d("DiscoveryProcess", "The discovery has started:   " + action);
            } else if (action.equals("android.bluetooth.adapter.action.DISCOVERY_FINISHED")) {
                swipeRefreshLayout.setRefreshing(false);
                initRecycleView(mListData);
                Log.d("DiscoveryProcess", "The discovery has finished:   " + mListData.toString());
            }

        }
    };


    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver deviceFound = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.R)
        public void onReceive(Context context, Intent intent) {
            Log.d("onReceiveMethod:", "device Detected");
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        handleBluetoothDevice(device);
                    }
                });
            }
        }
    };

    private BluetoothAdapter bluetoothAdapter;


    private void handleBluetoothDevice(BluetoothDevice device) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) bluetoothMustPermissions.check_BLUETOOTH_CONNECT_Permission();
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) bluetoothMustPermissions.check_BLUETOOTH_Permission();

        if (device != null && device.getName() != null) {
            String deviceName = device.getName();
            Log.d("deviceName", deviceName);
        }

        if (device != null && device.getAddress() != null) {
            String remoteDeviceAddress = device.getAddress();
            Log.d("remoteDeviceAddress", remoteDeviceAddress);
            addToCollectionSearchResult(device);
        }
    }

    private void addToCollectionSearchResult(BluetoothDevice device) {
        SearchResult deviceSearchResult = new SearchResult(device);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) bluetoothMustPermissions.check_BLUETOOTH_CONNECT_Permission();
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) bluetoothMustPermissions.check_BLUETOOTH_Permission();
        if (!mListAddress.contains(deviceSearchResult.getAddress()) && device.getName() != null) {
            List<SearchResult> searchResults = new ArrayList<>(mListData.size());
            searchResults.addAll(mListData);
            searchResults.sort(new DeviceCompare());
            mListData.add(deviceSearchResult);
            mListAddress.add(deviceSearchResult.getAddress());
            mListData.sort(new DeviceCompare());
            scanConnectionViewModel.setRecyclerViewData(searchResults);
        }
    }






    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            swipeRefreshLayout.setRefreshing(false);
            return;
        }

        if (requestCode == REQUEST_CODE && BluetoothUtils.isBluetoothEnabled()) {

            swipeRefreshLayout.setRefreshing(false);
            return;
        }

        if (requestCode == SELECT_DEVICE_REQUEST_CODE && data != null) {
            BluetoothDevice device = data.getParcelableExtra(CompanionDeviceManager.EXTRA_DEVICE);
            SearchResult deviceSearchResult = new SearchResult(device);
            if (device != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, "正在连接，请稍等...", Toast.LENGTH_SHORT).show();
                    }
                });
                connectDevice(deviceSearchResult.getAddress(), deviceSearchResult.getName());
            }
        } else {
            swipeRefreshLayout.setRefreshing(false);
            super.onActivityResult(requestCode, resultCode, data);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("ON_RESUME","...........");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mVpoperateManager = VPOperateManager.getMangerInstance(mContext.getApplicationContext());
        mVpoperateManager.disconnectWatch(new IBleWriteResponse() {
            @Override
            public void onResponse(int i) {

            }
        });
        List<SearchResult> searchResults = new ArrayList<>(mListData.size());
        scanConnectionViewModel.setRecyclerViewData(searchResults);
        isConnectionInProcess = false;
        Log.d("ON_RESTART", ".................");
    }

    private void connectDevice(final String mac, final String deviceName) {

        mVpoperateManager.connectDevice(mac, deviceName, new IConnectResponse() {

            @Override
            public void connectState(int code, BleGattProfile profile, boolean isoadModel) {
                if (code == Code.REQUEST_SUCCESS) {
                    //蓝牙与设备的连接状态
                    Logger.t(TAG).i("连接成功");
                    Logger.t(TAG).i("是否是固件升级模式=" + isoadModel);
                    mIsOadModel = isoadModel;
                } else {
                    isConnectionInProcess = false;
                    Logger.t(TAG).i("连接失败");
                }
            }
        }, new INotifyResponse() {
            @Override
            public void notifyState(int state) {
                if (state == Code.REQUEST_SUCCESS) {
                    View view = findViewById(R.id.devices_refresh_layout);
                    Snackbar.make(view, "Connection successful", Snackbar.LENGTH_LONG).show();
                    Intent intent = new Intent(mContext, DashBoardActivity.class);
                    intent.putExtra("isoadmodel", mIsOadModel);
                    intent.putExtra("deviceAddress", mac);
                    startActivity(intent);
                    finish();


                } else {
                    isConnectionInProcess = false;
                    mVpoperateManager = VPOperateManager
                            .getMangerInstance(mContext.getApplicationContext());
                    View view = findViewById(R.id.devices_refresh_layout);
                    Snackbar.make(view, "This device is not supported", Snackbar.LENGTH_LONG)
                            .show();
                    Logger.t(TAG).i("监听失败，重新连接");
                }
            }
        });
    }




    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case SELECT_DEVICE_REQUEST_CODE:
                Log.d("PERMISSION_ALL","SELECT_DEVICE_REQUEST_CODE");
                break;
            case LOCATION_PER_CODE:
                Log.d("PERMISSION_ALL","LOCATION_PER_CODE");
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initBLE();
                    swipeRefreshLayout.setRefreshing(false);
                }else if(grantResults.length != 0){
                    swipeRefreshLayout.setRefreshing(false);
                }
        }
    }


    public final BluetoothMustPermissions bluetoothMustPermissions = new BluetoothMustPermissions() {
        @Override
        public synchronized void check_BLUETOOTH_CONNECT_Permission() {
            if (ContextCompat.checkSelfPermission(
                    getApplication(), Manifest.permission.BLUETOOTH_CONNECT) ==
                    PackageManager.PERMISSION_GRANTED) {
                Log.d("PERMISSION_ALL","CONNECT_GRANTED");
                return;
                // You can use the API that requires the permission.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.BLUETOOTH_CONNECT)) {

            } else {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    Log.d("PERMISSION_ALL","BLUETOOTH_CONNECT");
                        requestPermissions(new String[]{Manifest.permission.BLUETOOTH_CONNECT}, SELECT_DEVICE_REQUEST_CODE);
                }

            }
        }

        @Override
        public synchronized void check_BLUETOOTH_LOCATION_Permission() {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                // You can use the API that requires the permission.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.BLUETOOTH_SCAN)) {

            } else {
                Log.d("PERMISSION_ALL","LOC PER");
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PER_CODE);
            }
        }

        @Override
        public synchronized void check_BLUETOOTH_SCAN_Permission() {
            if (ContextCompat.checkSelfPermission(
                    getApplication(), Manifest.permission.BLUETOOTH_SCAN) ==
                    PackageManager.PERMISSION_GRANTED) {
                return;
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.BLUETOOTH_SCAN)) {

            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    Log.d("PERMISSION_ALL","SCAN PER");
                    requestPermissions(new String[]{Manifest.permission.BLUETOOTH_SCAN}, SELECT_DEVICE_REQUEST_CODE);
                }
            }
        }

        @Override
        public void check_BLUETOOTH_Permission() {
            if (ContextCompat.checkSelfPermission(
                    getApplication(), Manifest.permission.BLUETOOTH) ==
                    PackageManager.PERMISSION_GRANTED) {
                Log.d("PERMISSION_ALL","CONNECT_GRANTED");
                return;
                // You can use the API that requires the permission.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.BLUETOOTH_CONNECT)) {

            } else {

                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
                    Log.d("PERMISSION_ALL","BLUETOOTH_Permission");
                    requestPermissions(new String[]{Manifest.permission.BLUETOOTH}, SELECT_DEVICE_REQUEST_CODE);
                }

            }
        }
    };


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d("INTENT_FROM", "..............");
        finishAndRemoveTask();
    }
}
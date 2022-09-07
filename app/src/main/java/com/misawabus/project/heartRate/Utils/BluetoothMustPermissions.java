package com.misawabus.project.heartRate.Utils;

public interface BluetoothMustPermissions {
    void  check_BLUETOOTH_CONNECT_Permission();
    void check_BLUETOOTH_LOCATION_Permission();
    void check_BLUETOOTH_SCAN_Permission();
    void check_BLUETOOTH_Permission();
}

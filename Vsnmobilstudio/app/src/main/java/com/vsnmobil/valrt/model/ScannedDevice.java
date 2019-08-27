package com.vsnmobil.valrt.model;

import android.bluetooth.BluetoothDevice;

import com.vsnmobil.valrt.BuildConfig;

/**
 * ScannedDevice.java
 * Model class that provides details about device name, device MAC address and RSSI value.
 *
 */
public class ScannedDevice {
    /** The Constant UNKNOWN. */
    private static final String UNKNOWN = "Unknown";
    /**  BluetoothDevice. */
    private BluetoothDevice bluetoothDevice;
    /**  RSSI. */
    private int rssiValue;
    /**  Display Name. */
    private String deviceDisplayName;
    /**  Device MAC Address. */
    private String deviceDiplayAddress;
    /**
     * Instantiates a new scanned device.
     *
     * @param device the device
     * @param rssi the rssi
     */
    public ScannedDevice(BluetoothDevice device, int rssi) {
        if (device == null) {
            throw new IllegalArgumentException("BluetoothDevice is null");
        }
        bluetoothDevice = device;
        // If HelpNowAlert build variant then display HN-ALERT for device name
        if (BuildConfig.FLAVOR.equals("HelpNowAlert")) {
            deviceDisplayName = device.getName().replace("V.ALRT", "HN-ALERT");
        } else {
            deviceDisplayName = device.getName();
        }
        if ((deviceDisplayName == null) || (deviceDisplayName.length() == 0)) {
            deviceDisplayName = UNKNOWN;
        }
        rssiValue = rssi;
        deviceDiplayAddress =  device.getAddress();
    }
    /**
     * Gets the device.
     *
     * @return the device
     */
    public BluetoothDevice getDevice() {
        return bluetoothDevice;
    }
    /**
     * Gets the rssi.
     *
     * @return the rssi
     */
    public int getRssi() {
        return rssiValue;
    }
    /**
     * Sets the rssi.
     *
     * @param rssi the new rssi
     */
    public void setRssi(int rssi) {
        rssiValue = rssi;
    }
    /**
     * Gets the display name.
     *
     * @return the display name
     */
    public String getDisplayName() {
        return deviceDisplayName;
    }
    /**
     * Sets the display name.
     *
     * @param displayName the new display name
     */
    public void setDisplayName(String displayName) {
        deviceDisplayName = displayName;
    }
    /**
     * Gets the device mac.
     *
     * @return the device mac
     */
    public String getDeviceMac() {
        return deviceDiplayAddress;
    }
    /**
     * Sets the device mac.
     *
     * @param deviceAddress the new device mac
     */
    public void setDeviceMac(String deviceAddress) {
        deviceDiplayAddress = deviceAddress;
    }
}

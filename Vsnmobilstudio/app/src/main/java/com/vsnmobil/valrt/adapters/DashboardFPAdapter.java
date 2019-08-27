package com.vsnmobil.valrt.adapters;

import java.util.ArrayList;
//import
import java.util.HashMap;
//import
import android.support.v4.app.Fragment;
//import
import android.support.v4.app.FragmentManager;
//import
import android.support.v4.app.FragmentPagerAdapter;
//import
import com.vsnmobil.valrt.VALRTApplication;
//import
import com.vsnmobil.valrt.fragment.DashboardFragment;
/**
 * DashboardFPAdapter.java
 * <p/>
 * This adapter is a fragment pager adapter.It is used to load the
 * each fragment in this adapter class
 */
public class DashboardFPAdapter extends FragmentPagerAdapter {
    /** The paired device. */
    private ArrayList<HashMap<String, String>> pairedDevice;
    /** The device name. */
    private String deviceName;
    /** The device address. */
    private String deviceAddress;
    /** The device battery. */
    private String deviceBattery;
    /** The device fall detection. */
    private String deviceFallDetection;
    /** The device count. */
    private int deviceCount = 0;
    /**
     * Instantiates a new dashboard fp adapter.
     *
     * @param fm the fm
     * @param paireddevice the paireddevice
     */
    //constructor
    public DashboardFPAdapter(FragmentManager fm, ArrayList<HashMap<String, String>> paireddevice) {
        super(fm);
        pairedDevice = paireddevice;
        deviceCount = paireddevice.size();
    }
    /* (non-Javadoc)
     * @see android.support.v4.app.FragmentPagerAdapter#getItem(int)
     */
    @Override
    public Fragment getItem(int position) {
        deviceName = pairedDevice.get(position).get(VALRTApplication.DEVICE_NAME);
        deviceAddress = pairedDevice.get(position).get(VALRTApplication.DEVICE_ADDRESS);
        deviceBattery = pairedDevice.get(position).get(VALRTApplication.BATTERY_STATUS);
        deviceFallDetection = pairedDevice.get(position).get(VALRTApplication.FALLDETECTION_STATUS);
        return DashboardFragment.newInstance(deviceName, deviceAddress, deviceBattery, deviceFallDetection, position);
    }
    /* (non-Javadoc)
     * @see android.support.v4.view.PagerAdapter#getCount()
     */
    @Override
    public int getCount() {
        return deviceCount;
    }
}

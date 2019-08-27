package com.vsnmobil.valrt.adapters;

import java.util.List;
//import
import android.bluetooth.BluetoothDevice;
//import
import android.content.Context;
//import
import android.view.LayoutInflater;
//import
import android.view.View;
//import
import android.view.ViewGroup;
//import
import android.widget.ArrayAdapter;
//import
import android.widget.TextView;
//import
import com.vsnmobil.valrt.R;
//import
import com.vsnmobil.valrt.model.ScannedDevice;
/**
 * ScannedDeviceAdapter.java
 * <p/>
 * This adapter is used to load the scanned puck in list view.
 */
public class ScannedDeviceAdapter extends ArrayAdapter<ScannedDevice> {
    /** The list. */
    private List<ScannedDevice> list;
    /** The inflater. */
    private LayoutInflater inflater;
    /** The res id. */
    private int resId;
    /**
     * Instantiates a new scanned device adapter.
     *
     * @param context the context
     * @param resId the res id
     * @param objects the objects
     */
    //Constructor
    public ScannedDeviceAdapter(Context context, int resId, List<ScannedDevice> objects) {
        super(context, resId, objects);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.resId = resId;
        list = objects;
    }
    /* (non-Javadoc)
     * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ScannedDevice item = (ScannedDevice) getItem(position);
        if (convertView == null) {
            convertView = inflater.inflate(resId, null);
        }
        TextView deviceNameTextview = (TextView) convertView.findViewById(R.id.device_name_textview);
        TextView deviceAddress = (TextView) convertView.findViewById(R.id.device_address_textview);
        deviceNameTextview.setText(item.getDisplayName());
        deviceAddress.setText(item.getDevice().getAddress());
        return convertView;
    }
    /**
     * add or update BluetoothDevice.
     *
     * @param newDevice the new device
     * @param rssi the rssi
     * @param scanRecord the scan record
     */
    public void update(BluetoothDevice newDevice, int rssi, byte[] scanRecord) {
        if ((newDevice == null) || (newDevice.getAddress() == null)) {
            return;
        }

        boolean contains = false;
        for (ScannedDevice device : list) {
            //LogUtils.LOGD("****** Hao: ", "ScannedDeviceAdapter->udpate "+newDevice.getAddress()+ " "+device.getDevice().getAddress());
            if (newDevice.getAddress().equals(device.getDevice().getAddress())) {
                contains = true;
                device.setRssi(rssi); // update
                break;
            }
        }
        if (!contains) {
            //LogUtils.LOGD("****** Hao: ", "ScannedDeviceAdapter->udpate2 "+newDevice.getAddress());

            // add new BluetoothDevice into the adapter.
            list.add(new ScannedDevice(newDevice, rssi));
        }
        // Refresh the list view.
        notifyDataSetChanged();
    }
}

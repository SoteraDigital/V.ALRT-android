package com.vsnmobil.valrt.adapters;

import java.util.ArrayList;
//import
import java.util.HashMap;
//import
import android.content.Context;
//import
import android.view.LayoutInflater;
//import
import android.view.View;
//import
import android.view.ViewGroup;
//import
import android.widget.BaseAdapter;
//import
import android.widget.TextView;
//import
import com.vsnmobil.valrt.R;
//import
import com.vsnmobil.valrt.VALRTApplication;
/**
 * PairedDeviceAdapter.java
 * <p/>
 * This adapter is used to load the paired device in list view.
 */
public class PairedDeviceAdapter extends BaseAdapter {
    /** The list. */
    private ArrayList<HashMap<String, String>> list;
    /** The inflater. */
    private LayoutInflater inflater;
    /** The activity. */
    private Context activity;
    /** The connection status. */
    private String connectionStatus;
    /**
     * Instantiates a new paired device adapter.
     *
     * @param context the context
     * @param listcollection the listcollection
     */
    //Constructor
    public PairedDeviceAdapter(Context context, ArrayList<HashMap<String, String>> listcollection) {
        activity = context;
        list = listcollection;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    /* (non-Javadoc)
     * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
     */
    @Override
    public View getView(final int pos, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View vi = convertView;
        if (vi == null) {
            vi = inflater.inflate(R.layout.listitem_paired_device, null);
            holder = new ViewHolder();
            holder.name = (TextView) vi.findViewById(R.id.device_name_textview);
            holder.address = (TextView) vi.findViewById(R.id.device_address_textview);
            holder.status = (TextView) vi.findViewById(R.id.device_status_textview);
            vi.setTag(holder);
        } else {
            holder = (ViewHolder) vi.getTag();
        }
        //Update the connection status in UI
        if (((String) list.get(pos).get(VALRTApplication.DEVICE_STATUS)).equalsIgnoreCase(VALRTApplication.CONNECTING)) {
            connectionStatus = activity.getString(R.string.connecting);
        } else if (((String) list.get(pos).get(VALRTApplication.DEVICE_STATUS)).equalsIgnoreCase(VALRTApplication.CONNECTED)) {
            connectionStatus = activity.getString(R.string.connected);
        } else if (((String) list.get(pos).get(VALRTApplication.DEVICE_STATUS)).equalsIgnoreCase(VALRTApplication.DISCONNECTED)) {
            connectionStatus = activity.getString(R.string.notconnected);
        }

        holder.name.setText((String) list.get(pos).get(VALRTApplication.DEVICE_NAME));
        holder.address.setText((String) list.get(pos).get(VALRTApplication.DEVICE_ADDRESS));
        holder.status.setText(connectionStatus);
        return vi;
    }
    /* (non-Javadoc)
     * @see android.widget.Adapter#getCount()
     */
    @Override
    public int getCount() {
        return list.size();
    }
    /* (non-Javadoc)
     * @see android.widget.Adapter#getItem(int)
     */
    @Override
    public Object getItem(int position) {
        return list.get(position);
    }
    /* (non-Javadoc)
     * @see android.widget.Adapter#getItemId(int)
     */
    @Override
    public long getItemId(int position) {
        return position;
    }
    /**
     * The Class ViewHolder.
     */
    // It's a view holder it contains a group of views.
    public static class ViewHolder {
        /** The name. */
        public TextView name;
        /** The address. */
        public TextView address;
        /** The status. */
        public TextView status;

    }

}

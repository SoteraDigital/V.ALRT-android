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
 * ProductInformationAdapter.java
 * <p/>
 * This adapter is used to load the product information of puck in list view.
 */
public class ProductInformationAdapter extends BaseAdapter {
    /** The list. */
    private ArrayList<HashMap<String, String>> list;
    /** The inflater. */
    private LayoutInflater inflater;
    /** The activity. */
    private Context activity;
    /**
     * Instantiates a new product information adapter.
     *
     * @param context the context
     * @param listcollection the listcollection
     */
    //Constructor
    public ProductInformationAdapter(Context context, ArrayList<HashMap<String, String>> listcollection) {
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
            vi = inflater.inflate(R.layout.listitem_product_information, null);
            holder = new ViewHolder();
            holder.deviceNametextView = (TextView) vi.findViewById(R.id.productinfo_devicename_textview);
            holder.serialNumbertextView = (TextView) vi.findViewById(R.id.productinfo_serialnumber_textview);
            holder.softwareVersionTextview = (TextView) vi.findViewById(R.id.productinfo_softwareversion_textview);
            vi.setTag(holder);
        } else
            holder = (ViewHolder) vi.getTag();
        holder.deviceNametextView.setText((String) list.get(pos).get(VALRTApplication.DEVICE_NAME));
        holder.serialNumbertextView.setText((String) list.get(pos).get(VALRTApplication.DEVICE_SERIAL_NUMBER));
        holder.softwareVersionTextview.setText((String) list.get(pos).get(VALRTApplication.DEVICE_SOFTWARE_VERSION));
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
    public static class ViewHolder {
        /** The device nametext view. */
        public TextView deviceNametextView;
        /** The serial numbertext view. */
        public TextView serialNumbertextView;
        /** The software version textview. */
        public TextView softwareVersionTextview;
    }

}

package com.vsnmobil.valrt.storage;

import java.text.SimpleDateFormat;
//import
import java.util.ArrayList;
//import
import java.util.Date;
//import
import java.util.HashMap;
//import
import java.util.List;
//import
import java.util.Locale;
//import
import android.content.ContentValues;
//import
import android.content.Context;
//import
import android.database.Cursor;
//import
import android.database.sqlite.SQLiteDatabase;
//import
import android.database.sqlite.SQLiteOpenHelper;
//import
import android.provider.BaseColumns;
//import
import com.vsnmobil.valrt.VALRTApplication;
/**
 * The Class DatabaseHelper.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    /** The helper. */
    private static DatabaseHelper helper;
    /** The lock. */
    private static Object lock = new Object();
    /**
     * Instantiates a new database helper.
     *
     * @param context the context
     */
    //Constructor
    public DatabaseHelper(Context context) {
        super(context, VALRTApplication.DATABASE_NAME, null,VALRTApplication.DATABASE_VERSION);
    }
    /* (non-Javadoc)
     * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Device Table: To maintain the device related records.
        String query = "CREATE TABLE " + VALRTApplication.DEVICE_TABLE + "("+ BaseColumns._ID + "," + VALRTApplication.DEVICE_ADDRESS+ " TEXT," + VALRTApplication.DEVICE_NAME + " TEXT,"
                + VALRTApplication.DEVICE_STATUS + " TEXT,"+ VALRTApplication.BATTERY_STATUS + " TEXT,"+ VALRTApplication.FALLDETECTION_STATUS + " TEXT,"+ VALRTApplication.DEVICE_SOFTWARE_VERSION + " TEXT,"
                + VALRTApplication.DEVICE_SERIAL_NUMBER + " TEXT)";
        db.execSQL(query);

        // History Log Table: To maintain the history log records.
        String queryHistoryLog = "CREATE TABLE "+ VALRTApplication.HISTORY_LOG_TABLE + "("+ VALRTApplication.HISTORY_LOG_STATUS + " TEXT)";
        db.execSQL(queryHistoryLog);
    }
    /* (non-Javadoc)
     * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //  Do nothing.
    }
    //---------------------------------- User defined methods ----------------------------------------------------//
    /**
     * To access the database instance, to do the CRUD operation in concurrent way.
     *
     * @param context of the component.
     * @return DatabaseHelper instance of DatabaseHelper class
     */
    public static synchronized DatabaseHelper getInstance(Context context){
        if(helper == null){
            helper = new DatabaseHelper(context);
        }
        return helper;
    }
    /**
     * To insert the device information into table.
     *
     * @param queryValues the query values
     */
    public void insertBleDevice(HashMap<String, String> queryValues) {
        synchronized (lock) {
            SQLiteDatabase database = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(VALRTApplication.DEVICE_ADDRESS,queryValues.get(VALRTApplication.DEVICE_ADDRESS));
            values.put(VALRTApplication.DEVICE_NAME,queryValues.get(VALRTApplication.DEVICE_NAME));
            values.put(VALRTApplication.DEVICE_STATUS,queryValues.get(VALRTApplication.DEVICE_STATUS));
            values.put(VALRTApplication.BATTERY_STATUS, "-1");
            values.put(VALRTApplication.FALLDETECTION_STATUS, "0");
            database.insert(VALRTApplication.DEVICE_TABLE, null, values);
            database.close();
        }
    }
    /**
     * To fetch the paired connected device information.
     * @return ArrayList with the paired connected device
     */
    public ArrayList<HashMap<String, String>> getPairedConnectedDeviceList() {
        synchronized (lock) {
            ArrayList<HashMap<String, String>> deviceDataList;
            deviceDataList = new ArrayList<HashMap<String, String>>();
            SQLiteDatabase database = this.getWritableDatabase();
            Cursor cursor = database.query(VALRTApplication.DEVICE_TABLE,
                    new String[] { VALRTApplication.DEVICE_ADDRESS, VALRTApplication.DEVICE_NAME, VALRTApplication.DEVICE_STATUS, VALRTApplication.BATTERY_STATUS,VALRTApplication.FALLDETECTION_STATUS},
                    VALRTApplication.DEVICE_STATUS +"=?",new String[] {VALRTApplication.CONNECTED}, null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put(VALRTApplication.DEVICE_ADDRESS, cursor.getString(cursor.getColumnIndex(VALRTApplication.DEVICE_ADDRESS)));
                    map.put(VALRTApplication.DEVICE_NAME, cursor.getString(cursor.getColumnIndex(VALRTApplication.DEVICE_NAME)));
                    map.put(VALRTApplication.DEVICE_STATUS, cursor.getString(cursor.getColumnIndex(VALRTApplication.DEVICE_STATUS)));
                    map.put(VALRTApplication.BATTERY_STATUS, cursor.getString(cursor.getColumnIndex(VALRTApplication.BATTERY_STATUS)));
                    map.put(VALRTApplication.FALLDETECTION_STATUS,cursor.getString(cursor.getColumnIndex(VALRTApplication.FALLDETECTION_STATUS)));
                    deviceDataList.add(map);
                } while (cursor.moveToNext());
            }
            // Close cursor and database.
            cursor.close();
            database.close();
            // return all inserted v.alrt device list
            return deviceDataList;
        }
    }
    /**
     * To fetch the paired device information.
     * @return ArrayList with paired device information.
     */
    public ArrayList<HashMap<String, String>> getPairedDeviceList() {

        synchronized (lock) {
            ArrayList<HashMap<String, String>> deviceDataList;
            deviceDataList = new ArrayList<HashMap<String, String>>();
            SQLiteDatabase database = this.getWritableDatabase();
            Cursor cursor = database.query(VALRTApplication.DEVICE_TABLE,
                    new String[] { VALRTApplication.DEVICE_ADDRESS, VALRTApplication.DEVICE_NAME, VALRTApplication.DEVICE_STATUS, VALRTApplication.BATTERY_STATUS,VALRTApplication.FALLDETECTION_STATUS},
                    null,null, null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put(VALRTApplication.DEVICE_ADDRESS, cursor.getString(cursor.getColumnIndex(VALRTApplication.DEVICE_ADDRESS)));
                    map.put(VALRTApplication.DEVICE_NAME, cursor.getString(cursor.getColumnIndex(VALRTApplication.DEVICE_NAME)));
                    map.put(VALRTApplication.DEVICE_STATUS, cursor.getString(cursor.getColumnIndex(VALRTApplication.DEVICE_STATUS)));
                    map.put(VALRTApplication.BATTERY_STATUS, cursor.getString(cursor.getColumnIndex(VALRTApplication.BATTERY_STATUS)));
                    map.put(VALRTApplication.FALLDETECTION_STATUS,cursor.getString(cursor.getColumnIndex(VALRTApplication.FALLDETECTION_STATUS)));
                    deviceDataList.add(map);
                } while (cursor.moveToNext());

            }
            // Close cursor and database.
            cursor.close();
            database.close();
            // return all inserted v.alrt device list
            return deviceDataList;
        }
    }
    /**
     * To fetch all the connected device.
     * @return List of all paired device list.
     */
    public List<String> getAllDeviceAddress() {
        synchronized (lock) {
            List<String> deviceAddressList = new ArrayList<String>();
            SQLiteDatabase database = this.getWritableDatabase();
            Cursor allDeviceCursor = null ;
            try {
                allDeviceCursor = database.query(VALRTApplication.DEVICE_TABLE, new String[] { VALRTApplication.DEVICE_ADDRESS}, null, null, null, null, null);
                if (allDeviceCursor.moveToFirst()) {
                    do {
                        deviceAddressList.add(allDeviceCursor.getString(allDeviceCursor.getColumnIndex(VALRTApplication.DEVICE_ADDRESS)));
                    } while (allDeviceCursor.moveToNext());
                }
                // Close cursor and database
                allDeviceCursor.close();
            } catch (Exception e) {}
            database.close();
            return deviceAddressList;
        }
    }
    /**
     * To retrieve the disconnected device address.
     * @return List of disconnected device.
     */
    public List<String> getDisconnectedDeviceAddress() {
        synchronized (lock) {
            List<String> deviceAddressList = new ArrayList<String>();
            SQLiteDatabase database = this.getWritableDatabase();
            Cursor cursor = null;
            try {
                cursor = database.query(VALRTApplication.DEVICE_TABLE,  new String[] { VALRTApplication.DEVICE_ADDRESS}, VALRTApplication.DEVICE_STATUS +"=?",new String[] {VALRTApplication.DISCONNECTED}, null, null, null);
                if (cursor.moveToFirst()) {
                    do {
                        deviceAddressList.add(cursor.getString(cursor.getColumnIndex(VALRTApplication.DEVICE_ADDRESS)));
                    } while (cursor.moveToNext());
                }
                // Close cursor and database.
                cursor.close();
            } catch (Exception e) {}
            database.close();
            return deviceAddressList;
        }
    }
    /**
     * To delete the paired device from the table.
     *
     * @param deviceaddress the deviceaddress
     */
    public void deleteDevice(String deviceaddress) {

        SQLiteDatabase database = this.getWritableDatabase();
        String deleteQuery = "DELETE FROM " + VALRTApplication.DEVICE_TABLE+ " WHERE " + VALRTApplication.DEVICE_ADDRESS + "='"+ deviceaddress + "'";
        database.execSQL(deleteQuery);
        database.close();
    }
    //
    /**
     * To Update the connection status of the previously connected device.
     *
     * @param deviceaddress the deviceaddress
     * @param status connection status.
     */
    public void updateDeviceConnectionStatus(String deviceaddress, String status) {
        synchronized (lock) {
            SQLiteDatabase database = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(VALRTApplication.DEVICE_STATUS, status);
            try {
                database.update(VALRTApplication.DEVICE_TABLE, values,VALRTApplication.DEVICE_ADDRESS + " = ?",new String[] { deviceaddress });
                database.close();

            } catch (Exception e) {}
        }
    }
    /**
     *  To update the fall detection enable / disable status of the previously connected device.
     *
     * @param deviceaddress the deviceaddress
     * @param status true / false
     */
    public void updateDeviceFalldetectionStatus(String deviceaddress,String status) {
        synchronized (lock) {
            SQLiteDatabase database = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(VALRTApplication.FALLDETECTION_STATUS, status);
            database.update(VALRTApplication.DEVICE_TABLE, values,VALRTApplication.DEVICE_ADDRESS + " = ?",new String[] { deviceaddress });
            database.close();
        }
    }
    /**
     * To  update the battery status of the previously connected device.
     *
     * @param deviceaddress the deviceaddress
     * @param status percentage of the remaining value in battery.
     */
    public void updateDeviceBatteryStatus(String deviceaddress, String status) {

        synchronized (lock) {
            SQLiteDatabase database = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(VALRTApplication.BATTERY_STATUS, status);
            database.update(VALRTApplication.DEVICE_TABLE, values,VALRTApplication.DEVICE_ADDRESS + " = ?",new String[] { deviceaddress });
            database.close();
        }
    }
    /**
     * To get the fall detection status of the specific device.
     *
     * @param deviceAddress the device address
     * @return true / false
     */
    public String getFalldetectionStatus(String deviceAddress) {

        synchronized (lock) {
            String fallStatus = null;
            SQLiteDatabase database = this.getWritableDatabase();
            Cursor cursor = database.query(VALRTApplication.DEVICE_TABLE,new String[] { VALRTApplication.FALLDETECTION_STATUS },VALRTApplication.DEVICE_ADDRESS + "= ?",new String[] { deviceAddress }, null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    fallStatus = cursor.getString(cursor.getColumnIndex(VALRTApplication.FALLDETECTION_STATUS));
                } while (cursor.moveToNext());
            }
            // Close cursor and database.
            cursor.close();
            database.close();
            return fallStatus;
        }
    }
    /**
     * To get the battery status of the specific device.
     *
     * @param deviceAddress the device address
     * @return battery percentage value in table.
     */
    public String getBatteryStatus(String deviceAddress) {

        synchronized (lock) {
            String batteryStatus = null;
            SQLiteDatabase database = this.getWritableDatabase();
            Cursor cursor = database.query(VALRTApplication.DEVICE_TABLE,new String[] { VALRTApplication.BATTERY_STATUS },VALRTApplication.DEVICE_ADDRESS + "= ?",new String[] { deviceAddress }, null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    batteryStatus = cursor.getString(cursor
                            .getColumnIndex(VALRTApplication.BATTERY_STATUS));
                } while (cursor.moveToNext());
            }
            // Close cursor and database.
            cursor.close();
            database.close();
            return batteryStatus;
        }
    }
    /**
     * To rename the device name of the previously connected device.
     *
     * @param deviceaddress the deviceaddress
     * @param devicename the devicename
     * @return integer query value.
     */
    public int renameDevice(String deviceaddress, String devicename) {

        synchronized (lock) {
            int value = 0;
            SQLiteDatabase database = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(VALRTApplication.DEVICE_NAME, devicename);
            value = database.update(VALRTApplication.DEVICE_TABLE, values,VALRTApplication.DEVICE_ADDRESS + " = ?",new String[] { deviceaddress });
            database.close();
            return value;
        }
    }
    /**
     * To update the connection status of all connected device as disconnected.
     */
    public void updateConnectionStatus() {

        synchronized (lock) {
            SQLiteDatabase database = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(VALRTApplication.DEVICE_STATUS,VALRTApplication.DISCONNECTED);
            database.update(VALRTApplication.DEVICE_TABLE, values, null, null);
            database.close();
        }
    }
    /**
     * To get the total number of paired devices.
     * @return integer total count.
     */
    public int getPairedDeviceCount() {

        synchronized (lock) {
            int deviceCount = -1;
            Cursor countcursor = null;
            SQLiteDatabase db = this.getReadableDatabase();
            countcursor = db.query(VALRTApplication.DEVICE_TABLE,  new String[] { BaseColumns._ID}, null,null, null, null, null);
            deviceCount = countcursor.getCount();
            countcursor.close();
            db.close();
            return deviceCount;
        }
    }
    /**
     * To get the total number of paired disconnected device.
     * @return integer total count.
     */
    public int getPairedDisConnectedDeviceCount() {

        synchronized (lock) {
            int deviceCount = -1;
            Cursor countcursor = null;
            SQLiteDatabase db = this.getReadableDatabase();
            countcursor = db.query(VALRTApplication.DEVICE_TABLE,  new String[] {BaseColumns._ID}, VALRTApplication.DEVICE_STATUS +"=?",new String[] {VALRTApplication.DISCONNECTED}, null, null, null);
            deviceCount = countcursor.getCount();
            countcursor.close();
            db.close();
            return deviceCount;
        }
    }
    /**
     * To check for the availability of device name already in device table to rename the device.
     *
     * @param deviceName the device name
     * @return integer total count of the name.
     */
    public int checkDeviceNameAvailability(String deviceName) {

        synchronized (lock) {
            int deviceCount = -1;
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor namecursor = null;
            namecursor = db.query(VALRTApplication.DEVICE_TABLE,  new String[] {BaseColumns._ID}, VALRTApplication.DEVICE_NAME +"=?",new String[] {deviceName}, null, null, null);
            deviceCount = namecursor.getCount();
            // Close cursor and database.
            namecursor.close();
            db.close();
            return deviceCount;
        }
    }
    /**
     * To update the device software version into the table.
     *
     * @param deviceaddress the deviceaddress
     * @param softwareversion the softwareversion
     */
    public void updateDeviceSoftwareVersion(String deviceaddress,String softwareversion) {
        synchronized (lock) {
            SQLiteDatabase database = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(VALRTApplication.DEVICE_SOFTWARE_VERSION, softwareversion);
            database.update(VALRTApplication.DEVICE_TABLE, values,VALRTApplication.DEVICE_ADDRESS + " = ?",new String[] { deviceaddress });
            database.close();
        }
    }
    /**
     * To fetch the name of the device.
     *
     * @param deviceAddress the device address
     * @return string name of the device.
     */
    public String getDeviceName(String deviceAddress) {
        synchronized (lock) {
            String devicename = null;
            SQLiteDatabase database = this.getWritableDatabase();
            Cursor deviceNameCursor = null;
            deviceNameCursor = database.query(VALRTApplication.DEVICE_TABLE, new String[]{VALRTApplication.DEVICE_NAME}, VALRTApplication.DEVICE_ADDRESS + "= ?", new String[]{deviceAddress}, null, null, null);
            if (deviceNameCursor.moveToFirst()) {
                do {
                    devicename = deviceNameCursor.getString(deviceNameCursor.getColumnIndex(VALRTApplication.DEVICE_NAME));
                } while (deviceNameCursor.moveToNext());
            }
            // Close cursor and database.
            deviceNameCursor.close();
            database.close();
            return devicename;
        }
    }
    /**
     * To update the device serial number into the table.
     *
     * @param deviceaddress the deviceaddress
     * @param serialnumber the serialnumber
     */
    public void updateDeviceSerialNumber(String deviceaddress,String serialnumber) {
        synchronized (lock) {
            SQLiteDatabase database = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(VALRTApplication.DEVICE_SERIAL_NUMBER, serialnumber);
            database.update(VALRTApplication.DEVICE_TABLE, values,
                    VALRTApplication.DEVICE_ADDRESS + " = ?",
                    new String[] { deviceaddress });
            database.close();
        }
    }
    /**
     * To retrieve Device name, serial number and software version for Product information.
     *
     * @return ArrayList with the above values.
     */
    public ArrayList<HashMap<String, String>> getProductInformation() {
        synchronized (lock) {
            ArrayList<HashMap<String, String>> deviceProductInformation;
            deviceProductInformation = new ArrayList<HashMap<String, String>>();
            SQLiteDatabase database = this.getWritableDatabase();
            Cursor cursor = database.query(VALRTApplication.DEVICE_TABLE,  new String[] {VALRTApplication.DEVICE_NAME,VALRTApplication.DEVICE_SOFTWARE_VERSION,VALRTApplication.DEVICE_SERIAL_NUMBER}, null,null, null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put(VALRTApplication.DEVICE_NAME, cursor.getString(cursor.getColumnIndex(VALRTApplication.DEVICE_NAME)));
                    map.put(VALRTApplication.DEVICE_SOFTWARE_VERSION,cursor.getString(cursor.getColumnIndex(VALRTApplication.DEVICE_SOFTWARE_VERSION)));
                    map.put(VALRTApplication.DEVICE_SERIAL_NUMBER,cursor.getString(cursor.getColumnIndex(VALRTApplication.DEVICE_SERIAL_NUMBER)));
                    deviceProductInformation.add(map);
                } while (cursor.moveToNext());
            }
            cursor.close();
            database.close();
            // return all inserted v.alrt device list
            return deviceProductInformation;
        }
    }
    /**
     *  To get the serial number of the device.
     *
     * @param deviceAddress the device address
     * @return string serial number
     */
    public synchronized String  getDeviceSerial(String deviceAddress) {
        synchronized (lock) {
            String deviceSerial = null;
            SQLiteDatabase database = this.getWritableDatabase();
            Cursor cursor = database.query(VALRTApplication.DEVICE_TABLE,new String[] { VALRTApplication.DEVICE_SERIAL_NUMBER },VALRTApplication.DEVICE_ADDRESS + "= ?",new String[] { deviceAddress }, null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    deviceSerial = cursor.getString(cursor.getColumnIndex(VALRTApplication.DEVICE_SERIAL_NUMBER));
                } while (cursor.moveToNext());
            }
            // Close cursor and database.
            cursor.close();
            database.close();
            return deviceSerial;
        }
    }
    // -------------------------------- History Log Table Methods----------------------------- //
    /**
     * To insert the history log into the table.
     *
     * @param historyContent the history content
     */
    public void insertDeviceHistory(String historyContent) {
        synchronized (lock) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault());
            String date = dateFormat.format(new Date());
            SQLiteDatabase database = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(VALRTApplication.HISTORY_LOG_STATUS, date + ","+ historyContent);
            database.insert(VALRTApplication.HISTORY_LOG_TABLE, null, values);
            database.close();
        }
        deleteOlderCallHistoryLogEntries();
    }
    /**
     * To retrieve device history in History Log Table.
     *
     * @return list of log records.
     */
    public List<String> getDeviceHistory() {
        synchronized (lock) {
            List<String> deviceHistoryLog = new ArrayList<String>();
            String selectQuery = "SELECT *  FROM "+ VALRTApplication.HISTORY_LOG_TABLE + " ORDER BY ROWID DESC";
            SQLiteDatabase database = this.getWritableDatabase();
            Cursor cursor = database.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    deviceHistoryLog.add(cursor.getString(cursor.getColumnIndex(VALRTApplication.HISTORY_LOG_STATUS)));
                } while (cursor.moveToNext());
            }
            // Close cursor and database.
            cursor.close();
            database.close();
            return deviceHistoryLog;
        }
    }
    /**
     * To delete last 100 rows in the History Log table.
     */
    public void deleteOlderCallHistoryLogEntries() {
        synchronized (lock) {
            SQLiteDatabase database = this.getWritableDatabase();
            String deleteQuery = null;
            String selectQuery = "SELECT *  FROM "+ VALRTApplication.HISTORY_LOG_TABLE;
            Cursor cursor = database.rawQuery(selectQuery, null);
            int rowCount = cursor.getCount();
            if (rowCount > 100) {
                deleteQuery = "DELETE FROM " + VALRTApplication.HISTORY_LOG_TABLE+ " WHERE ROWID NOT IN(SELECT ROWID FROM "+ VALRTApplication.HISTORY_LOG_TABLE+ " ORDER BY ROWID DESC LIMIT 100)";
                database.execSQL(deleteQuery);
            }
            // Close cursor and database.
            cursor.close();
            database.close();
        }
    }
}

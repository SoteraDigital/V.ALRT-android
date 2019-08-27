package com.vsnmobil.valrt.model;

import java.util.ArrayList;
//import
import java.util.List;
//import
import java.util.Timer;
//import
import java.util.TimerTask;
//import
import android.bluetooth.BluetoothGatt;
//import
import android.bluetooth.BluetoothGattCharacteristic;
//import
import android.bluetooth.BluetoothGattDescriptor;

import com.vsnmobil.valrt.utils.LogUtils;

/**
 * ProcessQueueExecutor.java 
 *
 * This class is used to execute the read,write and write descriptor request one by one
 * in 1.1 seconds delay. 
 *
 */
public class ProcessQueueExecutor extends Thread {
    /** The tag. */
    private String TAG = LogUtils.makeLogTag(ProcessQueueExecutor.class);
    /** The Constant REQUEST_TYPE_READ_CHAR. */
    public final static int REQUEST_TYPE_READ_CHAR = 1;
    /** The Constant REQUEST_TYPE_WRITE_CHAR. */
    public final static int REQUEST_TYPE_WRITE_CHAR = 2;
    /** The Constant REQUEST_TYPE_WRITE_DESCRIPTOR. */
    public final static int REQUEST_TYPE_WRITE_DESCRIPTOR = 3;
    /** The Constant EXECUTE_DELAY. */
    public final static long EXECUTE_DELAY = 1100;// delay in execution
    /** The process queue timer. */
    Timer processQueueTimer = null;
    /** The process list. */
    private static List<ReadWriteCharacteristic> processList = new ArrayList<ReadWriteCharacteristic>();
    /**
     * Adds the request to ProcessQueueExecutor.
     *
     * @param readWriteCharacteristic the read write characteristic
     */
    public static void addProcess(ReadWriteCharacteristic readWriteCharacteristic) {
        processList.add(readWriteCharacteristic);
    }
    /**
     * Removes the request from ProcessQueueExecutor.
     *
     * @param readWriteCharacteristic the read write characteristic
     */
    public static void removeProcess(ReadWriteCharacteristic readWriteCharacteristic) {
        processList.remove(readWriteCharacteristic);
    }
    /**
     * Execute proecess.
     */
    public void executeProecess() {

        if (!processList.isEmpty()) {
            ReadWriteCharacteristic readWriteCharacteristic = processList.get(0);
            int type = readWriteCharacteristic.getRequestType();
            BluetoothGatt bluetoothGatt = readWriteCharacteristic.getBluetoothGatt();
            Object parseObject = readWriteCharacteristic.getObject();

            if (type == REQUEST_TYPE_READ_CHAR) {
                BluetoothGattCharacteristic characteristic = (BluetoothGattCharacteristic) parseObject;
                try {
                    bluetoothGatt.readCharacteristic(characteristic);
                } catch (Exception e) {
                }
            } else if (type == REQUEST_TYPE_WRITE_CHAR) {
                BluetoothGattCharacteristic characteristic = (BluetoothGattCharacteristic) parseObject;
                try {
                    bluetoothGatt.writeCharacteristic(characteristic);
                } catch (Exception e) {
                }
            } else if (type == REQUEST_TYPE_WRITE_DESCRIPTOR) {
                BluetoothGattDescriptor clientConfig = (BluetoothGattDescriptor) parseObject;
                try {
                    bluetoothGatt.writeDescriptor(clientConfig);
                } catch (Exception e) {
                }
            }

            removeProcess(readWriteCharacteristic);
        }
    }
    /**
     * Returns the number of elements in ProcessQueueExecutor.
     *
     * @return the number of elements in ProcessQueueExecutor
     */
    public int getSize() {
        if (processList != null) {
            return processList.size();
        }
        return 0;
    }
    /* (non-Javadoc)
     * @see java.lang.Thread#interrupt()
     */
    @Override
    public void interrupt() {
        super.interrupt();
        if (processQueueTimer != null) {
            processQueueTimer.cancel();
            processQueueTimer.purge();
            processQueueTimer = null;
        }
    }
    /* (non-Javadoc)
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
        super.run();
        try {
            if (processQueueTimer == null) {
                processQueueTimer = new Timer();

                processQueueTimer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        executeProecess();
                    }
                }, 0, EXECUTE_DELAY);
            }
        } catch (Exception e){
            LogUtils.LOGI(TAG,e.getMessage()) ;
        }
    }
}

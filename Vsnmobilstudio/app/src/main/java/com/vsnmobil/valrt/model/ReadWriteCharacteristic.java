package com.vsnmobil.valrt.model;

import android.bluetooth.BluetoothGatt;
/**
 * ReadWriteCharacteristic.java
 * Model class that provides details about RequestType, BluetoothGatt object and
 * Object
 */
public class ReadWriteCharacteristic {
	/** The request type. */
	private int requestType;
	/** The bluetooth gatt. */
	private BluetoothGatt bluetoothGatt;
	/** The object. */
	private Object object;
	/**
	 * Instantiates a new read write characteristic.
	 */
	public ReadWriteCharacteristic() {
	}
	/**
	 * Instantiates a new read write characteristic.
	 *
	 * @param requestType the request type
	 * @param bluetoothGatt the bluetooth gatt
	 * @param object the object
	 */
	public ReadWriteCharacteristic(int requestType,BluetoothGatt bluetoothGatt,Object object) {
		this.requestType=requestType;
		this.bluetoothGatt=bluetoothGatt;
		this.object=object;
	}
	/**
	 * Gets the request type.
	 *
	 * @return the request type
	 */
	public int getRequestType() {
		return requestType;
	}
	/**
	 * Sets the request type.
	 *
	 * @param requestType the new request type
	 */
	public void setRequestType(int requestType) {
		this.requestType = requestType;
	}
	/**
	 * Gets the bluetooth gatt.
	 *
	 * @return the bluetooth gatt
	 */
	public BluetoothGatt getBluetoothGatt() {
		return bluetoothGatt;
	}
	/**
	 * Sets the bluetooth gatt.
	 *
	 * @param bluetoothGatt the new bluetooth gatt
	 */
	public void setBluetoothGatt(BluetoothGatt bluetoothGatt) {
		this.bluetoothGatt = bluetoothGatt;
	}
	/**
	 * Gets the object.
	 *
	 * @return the object
	 */
	public Object getObject() {
		return object;
	}
	/**
	 * Sets the object.
	 *
	 * @param object the new object
	 */
	public void setObject(Object object) {
		this.object = object;
	}

}

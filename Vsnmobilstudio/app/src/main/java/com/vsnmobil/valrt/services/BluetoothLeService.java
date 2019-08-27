package com.vsnmobil.valrt.services;

import java.util.ArrayList;
//import
import java.util.HashMap;
//import
import java.util.List;
//import
import java.util.Timer;
//import
import java.util.TimerTask;
//import
import java.util.UUID;
//import
import android.app.Service;
//import
import android.bluetooth.BluetoothAdapter;
//import
import android.bluetooth.BluetoothDevice;
//import
import android.bluetooth.BluetoothGatt;
//import
import android.bluetooth.BluetoothGattCallback;
//import
import android.bluetooth.BluetoothGattCharacteristic;
//import
import android.bluetooth.BluetoothGattDescriptor;
//import
import android.bluetooth.BluetoothGattService;
//import
import android.bluetooth.BluetoothManager;
//import
import android.bluetooth.BluetoothProfile;
//import
import android.content.Context;
//import
import android.content.Intent;
//import
import android.os.Binder;
//import
import android.os.Build;
//import
import android.os.IBinder;
//import
import android.util.Log;
//import
import android.os.Trace;

import com.crashlytics.android.Crashlytics;
import com.vsnmobil.valrt.GattConstant;
//import
import com.vsnmobil.valrt.R;
//import
import com.vsnmobil.valrt.VALRTApplication;
//import
import com.vsnmobil.valrt.activities.AlertProgressActivity;
//import
import com.vsnmobil.valrt.activities.DisconnectActivity;
//import
import com.vsnmobil.valrt.activities.FallDetectActivity;
//import
import com.vsnmobil.valrt.activities.HomeActivity;
//import
import com.vsnmobil.valrt.model.ProcessQueueExecutor;
//import
import com.vsnmobil.valrt.model.ReadWriteCharacteristic;
//import
import com.vsnmobil.valrt.storage.DatabaseHelper;
//import
import com.vsnmobil.valrt.utils.LogUtils;
//import
import com.vsnmobil.valrt.utils.NotificationUtils;
//import
import com.vsnmobil.valrt.utils.Utils;
/**
 * BluetoothLeService.java
 *
 * The communication between the Bluetooth Low Energy device will be
 * communicated through this service class only The initial connect request and
 * disconnect request will be executed in this class.Also, all the status from
 * the Bluetooth device will be notified in the corresponding callback methods.
 *
 */
public class BluetoothLeService extends Service {
	/** The tag. */
	private String TAG = LogUtils.makeLogTag(BluetoothLeService.class);
	/** The Constant ACTION_GATT_SERVICES_DISCOVERED. */
	// Constants going to use in the broadcast receiver as intent action.
	public final static String ACTION_GATT_SERVICES_DISCOVERED = "com.vsnmobil.valrt.ACTION_GATT_SERVICES_DISCOVERED";
	/** The Constant ACTION_GATT_PRE_CONNECTED. */
	public final static String ACTION_GATT_PRE_CONNECTED = "com.vsnmobil.valrt.ACTION_GATT_PRE_CONNECTED";
	/** The Constant ACTION_GATT_CONNECTED. */
	public final static String ACTION_GATT_CONNECTED = "com.vsnmobil.valrt.ACTION_GATT_CONNECTED";
	/** The Constant ACTION_OAD_DETECTED. */
	public final static String ACTION_OAD_DETECTED = "com.vsnmobil.valrt.ACTION_OAD_DETECTED";
	/** The Constant ACTION_GATT_DISCONNECTED. */
	public final static String ACTION_GATT_DISCONNECTED = "com.vsnmobil.valrt.ACTION_GATT_DISCONNECTED";
	/** The Constant ACTION_BATTERY_STATUS. */
	public final static String ACTION_BATTERY_STATUS = "com.vsnmobil.valrt.ACTION_BATTERY_STATUS";
	/** The Constant ACTION_RSSI_STATUS. */
	public final static String ACTION_RSSI_STATUS = "com.vsnmobil.valrt.ACTION_RSSI_STATUS";
	/** The Constant ACTION_DATA_WRITE. */
	public final static String ACTION_DATA_WRITE = "com.vsnmobil.valrt.ACTION_DATA_WRITE";
	/** The Constant EXTRA_DATA. */
	public final static String EXTRA_DATA = "com.vsnmobil.valrt.EXTRA_DATA";
	/** The Constant EXTRA_STATUS. */
	public final static String EXTRA_STATUS = "com.vsnmobil.valrt.EXTRA_STATUS";
	/** The Constant EXTRA_ADDRESS. */
	public final static String EXTRA_ADDRESS = "com.vsnmobil.valrt.EXTRA_ADDRESS";
	/** The bluetooth manager. */
	private BluetoothManager bluetoothManager = null;
	/** The bluetooth adapter. */
	private static BluetoothAdapter bluetoothAdapter = null;
	/** The gatt service. */
	private static BluetoothGattService gattService = null;
	// Hao 0630: battery service
	public static BluetoothGattService batteryService = null;
	/** The device. */
	private BluetoothDevice device = null;
	/** The m char identify. */
	public BluetoothGattCharacteristic mCharIdentify = null;
	/** The m char block. */
	public BluetoothGattCharacteristic mCharBlock = null;
	/** The m char verification. */
	public BluetoothGattCharacteristic mCharVerification = null;
	/** The process queue executor. */
	public ProcessQueueExecutor processQueueExecutor = new ProcessQueueExecutor();
	/** The bluetooth gatt map. */
	public static HashMap<String, BluetoothGatt> bluetoothGattMap;
	/** The read all device connection timer. */
	private Timer readAllDeviceConnectionTimer = null;
	/** The reconnect list. */
	private List<String> reconnectList;
	/** The reconnect service intent. */
	private Intent reconnectServiceIntent;
	/** The db helper. */
	private DatabaseHelper dbHelper;
	/** The rssi value. */
	private String rssiValue = "-107";
	/** The battery value. */
	private String batteryValue = "0";
	/** The history log. */
	private String historyLog;
	/** The connection status period. */
	// To start
	private long CONNECTION_STATUS_PERIOD = 20000;
	/** The initial timeer period. */
	private long INITIAL_TIMEER_PERIOD = 0;
	// 45,000 millisecond is the pre-defined value given by the VSN Team
	/** The failure status timer. */
	// to wait and listen set notification value is gets succeed or not.
	private long FAILURE_STATUS_TIMER = 45000;
	/** The failure counter. */
	private int failureCounter = 0;
	/** The failure timer. */
	private Timer failureTimer = null;
	/**
	 * On create.
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		// if blue tooth adapter is not initialized stop the service.
		if (Utils.isBluetoothEnabled(this) == false
				|| VALRTApplication.getPrefBoolean(BluetoothLeService.this,
				VALRTApplication.VALRT_SWITCH_OFF) == true) {
			stopForeground(false);
			BluetoothLeService.this.stopSelf();
		}
		// Database helper to fetch data from local database
		dbHelper = DatabaseHelper.getInstance(BluetoothLeService.this);
		// To add and maintain the BluetoothGatt object of each BLE device.
		bluetoothGattMap = new HashMap<String, BluetoothGatt>();
		// To add in the disconnected device address in this list to reconnect
		// it.
		reconnectList = new ArrayList<String>();
		// To execute the read and write operation in a queue.
		if (!processQueueExecutor.isAlive()) {
			processQueueExecutor.start();
		}

	}
	/**
	 * On start command.
	 *
	 * @param intent the intent
	 * @param flags the flags
	 * @param startId the start id
	 * @return the int
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// if blue tooth adapter is not initialized stop the service.
		if (Utils.isBluetoothEnabled(this) == false
				|| VALRTApplication.getPrefBoolean(BluetoothLeService.this,
				VALRTApplication.VALRT_SWITCH_OFF) == true) {
			stopForeground(false);
			BluetoothLeService.this.stopSelf();
		}
		// If database helper object is null create it freshly whenever this
		// onStartCommand method is called.
		if (dbHelper == null) {
			dbHelper = DatabaseHelper.getInstance(BluetoothLeService.this);
		}
		// To start the reconnect service.
		reconnectServiceIntent = new Intent(getApplicationContext(),
				ReconnectService.class);
		// Check the connection status every 30 seconds.
		readAllDeviceConnectionStatus();

		// To keep running the service always in background.
		startForeground(VALRTApplication.BLUETOOTHLESERVICE_NOTIFY_ID,
				NotificationUtils.getNotification(this,
						getString(R.string.service_running)));
		return START_STICKY;
	}
	/**
	 * On destroy.
	 */
	@Override
	public void onDestroy() {
		// To stop the foreground service.
		stopForeground(false);
		// Stop the read / write operation queue.
		processQueueExecutor.interrupt();
		// Cancel the schedule timer which is checking the connection status of
		// GATT object.
		if (readAllDeviceConnectionTimer != null) {
			readAllDeviceConnectionTimer.cancel();
		}
		// cancel the write failure timer.
		if (failureTimer != null) {
			failureTimer.cancel();
		}
	}
	/**
	 * On task removed.
	 *
	 * @param rootIntent the root intent
	 */
	@Override
	public void onTaskRemoved(Intent rootIntent) {
		// If the recent task has been cleared start the ping service to start
		// this BlueetoothLeService again.
		VALRTApplication.startService(this);
		super.onTaskRemoved(rootIntent);
	}
	/** Manage the BLE service. */
	private final IBinder binder = new LocalBinder();

	// Local binder to bind the service and communicate with this
	/**
	 * The Class LocalBinder.
	 */
	// BluetoothLeService class.
	public class LocalBinder extends Binder {

		/**
		 * Gets the service.
		 *
		 * @return the service
		 */
		public BluetoothLeService getService() {
			return BluetoothLeService.this;
		}
	}
	/**
	 * On unbind.
	 *
	 * @param intent the intent
	 * @return true, if successful
	 */
	@Override
	public boolean onUnbind(Intent intent) {
		// In this particular example,close() is invoked when the UI is
		// disconnected from the Service.
		return super.onUnbind(intent);
	}
	/**
	 * On bind.
	 *
	 * @param arg0 the arg0
	 * @return the i binder
	 */
	@Override
	public IBinder onBind(Intent arg0) {
		return binder;
	}
	/**
	 * Initializes a reference to the local Blue tooth adapter.
	 *
	 * @return Return true if the initialization is successful.
	 */
	public boolean initialize() {
		// For API level 18 and above, get a reference to BluetoothAdapter
		// through BluetoothManager.
		if (bluetoothManager == null) {
			bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
			if (bluetoothManager == null) {
				return false;
			}
		}

		bluetoothAdapter = bluetoothManager.getAdapter();
		if (bluetoothAdapter == null) {
			return false;
		}
		return true;
	}
	/**
	 * To read the value from the BLE Device.
	 *
	 * @param mGatt the m gatt
	 * @param characteristic the characteristic
	 */
	public void readCharacteristic(final BluetoothGatt mGatt,
								   final BluetoothGattCharacteristic characteristic) {
		if (!checkConnectionState(mGatt)) {
			return;
		}
		ReadWriteCharacteristic readWriteCharacteristic = new ReadWriteCharacteristic(
				ProcessQueueExecutor.REQUEST_TYPE_READ_CHAR, mGatt,
				characteristic);
		ProcessQueueExecutor.addProcess(readWriteCharacteristic);
	}
	/**
	 * To write the value to BLE Device.
	 *
	 * @param mGatt the m gatt
	 * @param characteristic the characteristic
	 * @param b the b
	 */
	public void writeCharacteristic(final BluetoothGatt mGatt,
									final BluetoothGattCharacteristic characteristic, byte[] b) {
		if (!checkConnectionState(mGatt)) {
			return;
		}
		characteristic.setValue(b);
		ReadWriteCharacteristic readWriteCharacteristic = new ReadWriteCharacteristic(
				ProcessQueueExecutor.REQUEST_TYPE_WRITE_CHAR, mGatt,
				characteristic);
		ProcessQueueExecutor.addProcess(readWriteCharacteristic);
	}
	/**
	 * Enables or disables notification on a give characteristic.
	 *
	 * @param mGatt the m gatt
	 * @param characteristic            Characteristic to act on.
	 * @param enabled            If true, enable notification. False otherwise.
	 */
	public void setCharacteristicNotification(final BluetoothGatt mGatt,
											  BluetoothGattCharacteristic characteristic, boolean enabled) {
		if (!checkConnectionState(mGatt)) {
			return;
		}
		if (!mGatt.setCharacteristicNotification(characteristic, enabled)) {
			return;
		}
		final BluetoothGattDescriptor clientConfig = characteristic
				.getDescriptor(GattConstant.CLIENT_CHARACTERISTIC_CONFIG);
		if (clientConfig == null) {
			return;
		}
		clientConfig.setValue(enabled ? GattConstant.ENABLE_NOTIFICATION_VALUE
				: GattConstant.DISABLE_NOTIFICATION_VALUE);
		ReadWriteCharacteristic readWriteCharacteristic = new ReadWriteCharacteristic(
				ProcessQueueExecutor.REQUEST_TYPE_WRITE_DESCRIPTOR, mGatt,
				clientConfig);
		ProcessQueueExecutor.addProcess(readWriteCharacteristic);
	}
	/**
	 * Connects to the GATT server hosted on the Blue tooth LE device.
	 *
	 * @param address
	 *            The device address of the destination device.
	 * @return Return true if the connection is initiated successfully. The
	 *         connection result is reported asynchronously through the
	 *         {@code BluetoothGattCallback# onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
	 *         callback.
	 */
	public boolean connect(final String address) {

		if (address == null
				|| VALRTApplication.getPrefBoolean(BluetoothLeService.this,
				VALRTApplication.VALRT_SWITCH_OFF) == true) {
			return false;
		}
		// HN 050615: re-init bluetoothAdapter if null
		if (bluetoothAdapter == null) {
			if (!initialize()) {
				return false;
			}
		}

		BluetoothGatt bluetoothGatt = bluetoothGattMap.get(address);
		if (bluetoothGatt != null) {
			bluetoothGatt.disconnect();
			bluetoothGatt.close();
		}
		device = bluetoothAdapter.getRemoteDevice(address);

		// HN 050615: Don't need to check for connection state.  When we are at this point
		// device was found during scan, so it must be disconnected.  So just issue connectGatt().
		// Also, fitbit apps causes BLE stack to always report V.ALRT as connected even though it's disconnected

		if (device == null) {
			return false;
		}
		// We want to directly connect to the device, so we are setting the
		// autoConnect parameter to false.
		BluetoothGatt mBluetoothGatt = device.connectGatt(this, false,
				mGattCallbacks);
		// Add the each BluetoothGatt in to an array list.
		if (!bluetoothGattMap.containsKey(address)) {
			bluetoothGattMap.put(address, mBluetoothGatt);
		} else {
			bluetoothGattMap.remove(address);
			bluetoothGattMap.put(address, mBluetoothGatt);
		}

		return true;
	}
	/**
	 * To disconnect the connected Blue tooth Low energy Device from the APP.
	 *
	 * @param gatt the gatt
	 * @param status            log and notification needed or not.
	 */
	public void disconnect(BluetoothGatt gatt, boolean status) {
		if (gatt != null) {
			BluetoothDevice device = gatt.getDevice();
			String deviceAddress = device.getAddress();
			String deviceName = dbHelper.getDeviceName(device.getAddress());

			try {
				bluetoothGattMap.remove(deviceAddress);
				gatt.disconnect();
				gatt.close();
			} catch (Exception e) {
				LogUtils.LOGI(TAG, e.getMessage());
			}
			if (status == true) {
				historyLog = deviceName + "," + deviceAddress + ","
						+ getResources().getString(R.string.forget_me);
				updateHistoryLogTable(historyLog);
				NotificationUtils
						.postNotification(
								this,
								deviceName + " "
										+ getString(R.string.disconnected),
								VALRTApplication.BLUETOOTH_CONNECT_DISCONNECT_NOTIFY_ID);
			}
		}
	}
	/**
	 * To check the connection status of the GATT object.
	 *
	 * @param gatt the gatt
	 * @return If connected it will return true else false.
	 */
	public boolean checkConnectionState(BluetoothGatt gatt) {
		if (bluetoothAdapter == null) {
			// HN 050615: re-init bluetoothAdapter if null
			if (!initialize()) {
				return false;
			}
		}
		BluetoothDevice device = gatt.getDevice();
		String deviceAddress = device.getAddress();
		final BluetoothDevice bluetoothDevice = bluetoothAdapter
				.getRemoteDevice(deviceAddress);
		int connectionState = bluetoothManager.getConnectionState(
				bluetoothDevice, BluetoothProfile.GATT);
		if (connectionState == BluetoothProfile.STATE_CONNECTED) {
			return true;
		}
		return false;
	}
	/**
	 * To check the connection status of the GATT object.
	 *
	 * @param deviceAddress the device address
	 * @return If connected it will return true else false.
	 */
	public boolean checkConnectionState(String deviceAddress) {
		if (bluetoothAdapter == null) {
			return false;
		}
		final BluetoothDevice btDevice = bluetoothAdapter
				.getRemoteDevice(deviceAddress);
		int connectionState = bluetoothManager.getConnectionState(btDevice,
				BluetoothProfile.GATT);
		if (connectionState == BluetoothProfile.STATE_CONNECTED) {
			return true;
		}
		return false;
	}
	// The connection status of the Blue tooth Low energy Device will be
	/** The m gatt callbacks. */
	// notified in the below callback.
	private BluetoothGattCallback mGattCallbacks = new BluetoothGattCallback() {
		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status,
											int newState) {
			BluetoothDevice device = gatt.getDevice();
			String deviceAddress = device.getAddress();
			String deviceName = dbHelper.getDeviceName(device.getAddress());
			try {
				//LogUtils.LOGD("****** Hao:", "ConnectionStateChange "+newState);

				switch (newState) {
					case BluetoothProfile.STATE_CONNECTED:
						broadcastUpdate(ACTION_GATT_PRE_CONNECTED, deviceAddress,
								status);
						// if device tracking is already in progress cancel it.
						if (VALRTApplication.isDeviceTrackInProgress) {
							DisconnectActivity.cancelthisActivity();
						}
						//LogUtils.LOGD("****** Hao:", "Start service discovery");

						// start service discovery
						gatt.discoverServices();
						break;
					case BluetoothProfile.STATE_DISCONNECTED:
					    Trace.beginSection("Disconnected");

                        LogUtils.LOGD(TAG, "Disconnected from device.");
						try
						{
							bluetoothGattMap.remove(deviceAddress);
							gatt.disconnect();
							gatt.close();
						} catch (Exception e) {
							LogUtils.LOGI(TAG, e.getMessage());
						}
						// Update the disconnected status in database.
						dbHelper.updateDeviceConnectionStatus(deviceAddress,
								VALRTApplication.DISCONNECTED);
						if (!reconnectList.contains(deviceAddress)) {
							reconnectList.add(deviceAddress);
							if (!VALRTApplication.getPrefBoolean(
									BluetoothLeService.this,
									VALRTApplication.VALRT_SWITCH_OFF)) {
								NotificationUtils
										.postNotification(
												BluetoothLeService.this,
												deviceName
														+ " "
														+ getString(R.string.reconncet_notification),
												VALRTApplication.BLUETOOTH_CONNECT_DISCONNECT_NOTIFY_ID);
								historyLog = deviceName
										+ ","
										+ deviceAddress
										+ ","
										+ getResources().getString(
										R.string.reconncet_notification);
								updateHistoryLogTable(historyLog);
							}
						}
						if (VALRTApplication.getPrefBoolean(
								BluetoothLeService.this,
								VALRTApplication.VALRT_SWITCH_OFF)
								|| (VALRTApplication.isForgetMeClicked == true
								|| !Utils
								.isBluetoothEnabled(BluetoothLeService.this)
								|| VALRTApplication.isAlertInProgress
								|| VALRTApplication.isFallDetectInProgress || !Utils
								.isAirplaneModeEnabled(BluetoothLeService.this))) {
							VALRTApplication.isForgetMeClicked = false;
						} else {
							if (Utils.isTrackerEnabled(BluetoothLeService.this)) {
								VALRTApplication.isDeviceTrackInProgress = true;
								openHomeActivity();
							}
						}
						broadcastUpdate(ACTION_GATT_DISCONNECTED, deviceAddress,
								status);
						break;
					default:
						break;
				}
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		}
		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			BluetoothDevice device = gatt.getDevice();
			String deviceAddress = device.getAddress();
			String deviceName = dbHelper.getDeviceName(device.getAddress());


			if (status == BluetoothGatt.GATT_SUCCESS)
			{
			    Trace.endSection();
				failureCounter = 0;
				NotificationUtils
						.postNotification(
								BluetoothLeService.this,
								deviceName + " "
										+ getString(R.string.connected),
								VALRTApplication.BLUETOOTH_CONNECT_DISCONNECT_NOTIFY_ID);
				if (reconnectList.contains(deviceAddress)) {
					reconnectList.remove(deviceAddress);
				}
				historyLog = deviceName
						+ ","
						+ deviceAddress
						+ ","
						+ getResources().getString(
						R.string.history_device_connected);
				dbHelper.updateDeviceConnectionStatus(deviceAddress,
						VALRTApplication.CONNECTED);
				updateHistoryLogTable(historyLog);
				broadcastUpdate(ACTION_GATT_CONNECTED, deviceAddress, status);
				// Do APP verification as soon as service discovered.
				try {
					appVerification(
							gatt,
							getGattChar(gatt,
									GattConstant.SERVICE_VSN_SIMPLE_SERVICE,
									GattConstant.CHAR_APP_VERIFICATION),
							GattConstant.NEW_APP_VERIFICATION_VALUE);
				} catch (Exception e) {
				}

				failureCheckTimer(gatt);
				for (BluetoothGattService service : gatt.getServices()) {

					if ((service == null) || (service.getUuid() == null)) {
						continue;
					}
					if (GattConstant.SERVICE_DEVICE_INFO.equals(service
							.getUuid())) {
						// Read the device serial number
						readCharacteristic(
								gatt,
								service.getCharacteristic(GattConstant.CHAR_SERIAL_NUMBER));
						// Read the device software version
						readCharacteristic(
								gatt,
								service.getCharacteristic(GattConstant.CHAR_SOFTWARE_REV));
					}

					if (GattConstant.SERVICE_BATTERY_LEVEL.equals(service
							.getUuid())) {
						// Hao 063016: Save the service to use later
						batteryService = service;
						// Hao 063016: Moved read to OnCharacteristicWrite callback
						//getDeviceBattery(
						//		gatt,
						//		service.getCharacteristic(GattConstant.CHAR_BATTERY_LEVEL));

						// Set notification if change in battery percentage.
						setCharacteristicNotification(
								gatt,
								service.getCharacteristic(GattConstant.CHAR_BATTERY_LEVEL),
								true);
					}

					if (GattConstant.SERVICE_VSN_SIMPLE_SERVICE.equals(service
							.getUuid())) {
						mCharVerification = service
								.getCharacteristic(GattConstant.CHAR_APP_VERIFICATION);
						// Writ Emergency key press and Fall detection
						if (dbHelper.getFalldetectionStatus(deviceAddress)
								.equalsIgnoreCase("1")) {
							enableForDetect(
									gatt,
									service.getCharacteristic(GattConstant.CHAR_DETECTION_CONFIG),
									GattConstant.ENABLE_FALL_KEY_DETECTION_VALUE);
						} else {
							enableForDetect(
									gatt,
									service.getCharacteristic(GattConstant.CHAR_DETECTION_CONFIG),
									GattConstant.ENABLE_KEY_DETECTION_VALUE);
						}
						// Set notification for emergency key press and fall
						// detection
						setCharacteristicNotification(
								gatt,
								service.getCharacteristic(GattConstant.CHAR_DETECTION_NOTIFY),
								true);
						setCharacteristicNotification(
								gatt,
								service.getCharacteristic(GattConstant.CHAR_DETECTION_NOTIFY),
								true);
					}

					if (GattConstant.SERVICE_SILENTMODE.equals(service
							.getUuid())) {
						if (VALRTApplication.getPrefBoolean(
								BluetoothLeService.this,
								VALRTApplication.DEVICESILENTCBX)) {
							// write for put the device in silent mode
							writeCharacteristic(
									gatt,
									service.getCharacteristic(GattConstant.CHAR_SILENTMODE),
									GattConstant.ENABLE_SILENT_MODE_VALUE);
						} else {
							// write for put the device in normal mode.
							writeCharacteristic(
									gatt,
									service.getCharacteristic(GattConstant.CHAR_SILENTMODE),
									GattConstant.NORMAL_MODE_VALUE);
						}
					}

					if (GattConstant.SERVICE_ADJIST_CONNECTION_INTERVAL
							.equals(service.getUuid())
							&& Utils.isAdjustControlAcceptable()) {
						// write for adjust connection control value to make the
						// device response time as 1.1 second on MTK 6735 platform
						if (Build.HARDWARE.equalsIgnoreCase("mt6735") || Build.HARDWARE.equalsIgnoreCase("mt6753"))
							writeCharacteristic(
								gatt,
								service.getCharacteristic(GattConstant.CHAR_ADJIST_CONNECTION_INTERVAL),
								GattConstant.ADJIST_CONNECTION_INTERVAL_VALUE_MT6735);
						// for other platforms use 980ms with slave latency of 1
						else
							writeCharacteristic(
									gatt,
									service.getCharacteristic(GattConstant.CHAR_ADJIST_CONNECTION_INTERVAL),
									GattConstant.ADJIST_CONNECTION_INTERVAL_VALUE);
					}
				}
			} else {
				// Service discovery failed close and disconnect the GATT object
				// of the device.
				// HN 050615: remove device from GattMap since disconnect() does not guarantee onConnectionStateChange will be called.
				disconnect(gatt,false);
			}
		}
		// CallBack when the response available for registered the notification(
		// Battery Status, Fall Detect, Key Press)
		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt,
											BluetoothGattCharacteristic characteristic) {
			System.gc();

			BluetoothDevice device = gatt.getDevice();
			String deviceAddress = device.getAddress();
			String deviceName = dbHelper.getDeviceName(deviceAddress);
			BluetoothGatt receivedGatt = gatt;

			// Get Battery Status
			if (GattConstant.CHAR_BATTERY_LEVEL
					.equals(characteristic.getUuid())) {
				batteryValue = characteristic.getIntValue(
						BluetoothGattCharacteristic.FORMAT_UINT8, 0).toString();
				updateBatteryStatus(deviceName, deviceAddress, batteryValue);
			}

			if (GattConstant.CHAR_KEY_PRESS.equals(characteristic.getUuid())) {
				// If App is in active state.
				if (VALRTApplication.getPrefBoolean(BluetoothLeService.this,
						VALRTApplication.VALRT_SWITCH_OFF) == false) {
					final String keyValue = characteristic.getIntValue(
							BluetoothGattCharacteristic.FORMAT_UINT8, 0)
							.toString();
					// Acknowledge the device that data has been received.
					ackDevice(receivedGatt, GattConstant.RECEIVED_ACK);

					if (!VALRTApplication.isAlertInProgress) {
						// Emergency key press event is triggered start alert in
						// progress.
						if (keyValue.equalsIgnoreCase("3")) {
							AlertProgressActivity.alertProgressGatt = receivedGatt;
							VALRTApplication.isAlertInProgress = true;
							openHomeActivity();
							historyLog = deviceName
									+ ","
									+ deviceAddress
									+ ","
									+ getResources().getString(
									R.string.history_device_keypressed);
							updateHistoryLogTable(historyLog);
							// Free fall is event is triggered start fall
							// detected dialog.
						} else if (keyValue.equalsIgnoreCase("4")
								&& VALRTApplication.isFallDetectInProgress == false) {
							FallDetectActivity.alertProgressGatt = receivedGatt;
							AlertProgressActivity.alertProgressGatt = receivedGatt;
							VALRTApplication.isFallDetectInProgress = true;
							openHomeActivity();
							historyLog = deviceName
									+ ","
									+ deviceAddress
									+ ","
									+ getResources().getString(
									R.string.history_device_falldetect);
							updateHistoryLogTable(historyLog);
						}
					} else {
						if (receivedGatt != AlertProgressActivity.alertProgressGatt)
							ackDevice(receivedGatt, GattConstant.CANCEL_ACK);
					}
				}
			}
		}
		// Callback when the response available for Read Characteristic Request
		@Override
		public void onCharacteristicRead(BluetoothGatt gatt,
										 BluetoothGattCharacteristic characteristic, int status) {

			BluetoothDevice device = gatt.getDevice();
			String deviceAddress = device.getAddress();
			String deviceName = dbHelper.getDeviceName(deviceAddress);

			if (status == BluetoothGatt.GATT_SUCCESS) {
				// Update the database with received battery value.
				if (GattConstant.CHAR_BATTERY_LEVEL.equals(characteristic
						.getUuid())) {
					batteryValue = characteristic.getIntValue(
							BluetoothGattCharacteristic.FORMAT_UINT8, 0)
							.toString();
					updateBatteryStatus(deviceName, deviceAddress, batteryValue);
				}
				// Update the database with received serial number of device.
				if (GattConstant.CHAR_SERIAL_NUMBER.equals(characteristic
						.getUuid())) {
					String serialNo = new String(characteristic.getValue());
					dbHelper.updateDeviceSerialNumber(deviceAddress, serialNo);
				}
				// Update the database with received software version.
				if (GattConstant.CHAR_SOFTWARE_REV.equals(characteristic
						.getUuid())) {
					String softwareVersion = new String(
							characteristic.getValue());
					VALRTApplication.setPrefString(BluetoothLeService.this,
							VALRTApplication.DEVICE_SOFTWARE_VERSION,
							softwareVersion);
					Crashlytics.setString("fw_version",softwareVersion);
					dbHelper.updateDeviceSoftwareVersion(deviceAddress,
							softwareVersion);
				}
			}
		}
		// Callback when the response available for Write Characteristic Request
		@Override
		public void onCharacteristicWrite(BluetoothGatt gatt,
										  BluetoothGattCharacteristic characteristic, int status) {
			if (characteristic.getValue().equals(
					GattConstant.OAD_SWTICH_KEY_VALUE))
				broadcastUpdate(ACTION_DATA_WRITE, gatt.getDevice()
						.getAddress(), status);
			// Hao 063016: callback for connection interval change
			if (GattConstant.CHAR_ADJIST_CONNECTION_INTERVAL.equals(characteristic.getUuid())) {
				// Read device battery percentage
				if (batteryService != null) {
					getDeviceBattery(gatt,batteryService.getCharacteristic(GattConstant.CHAR_BATTERY_LEVEL));
				}
			}
		}
		// Callback when the response available for Read Descriptor Request
		@Override
		public void onDescriptorRead(BluetoothGatt gatt,
									 BluetoothGattDescriptor descriptor, int status) {
		}
		// Callback when the response available for Write Descriptor Request
		@Override
		public void onDescriptorWrite(BluetoothGatt gatt,
									  BluetoothGattDescriptor descriptor, int status) {
			failureCounter++;
			if (status != BluetoothGatt.GATT_SUCCESS) {
				failureCounter = 3;
				// HN 050615: replaced gatt.disconnect() with disconnect() call
				disconnect(gatt, false);
			}
		}
		@Override
		public void onReadRemoteRssi(BluetoothGatt gatt, final int rssi,
									 int status) {
			String deviceAddress = gatt.getDevice().getAddress();
			if (status == BluetoothGatt.GATT_SUCCESS) {
				rssiValue = Integer.toString(rssi);
				broadcastUpdate(ACTION_RSSI_STATUS, rssiValue, deviceAddress);
			}
		}
	};
	/**
	 * To write the value to BLE Device for APP verification.
	 *
	 * @param mGatt the m gatt
	 * @param ch the ch
	 * @param value the value
	 */
	public void appVerification(final BluetoothGatt mGatt,
								final BluetoothGattCharacteristic ch, final byte[] value) {
		writeCharacteristic(mGatt, ch, value);
	}
	/**
	 * To write the value to BLE Device for Emergency / Fall alert.
	 *
	 * @param mGatt the m gatt
	 * @param ch the ch
	 * @param value the value
	 */
	public void enableForDetect(final BluetoothGatt mGatt,
								final BluetoothGattCharacteristic ch, final byte[] value) {
		writeCharacteristic(mGatt, ch, value);
	}
	/**
	 * To write the value to BLE Device to read the battery.
	 *
	 * @param mGatt the m gatt
	 * @param ch the ch
	 * @return the device battery
	 */
	public void getDeviceBattery(final BluetoothGatt mGatt,
								 final BluetoothGattCharacteristic ch) {
		readCharacteristic(mGatt, ch);
	}
	/**
	 * Acknowledge the device that the value received.
	 *
	 * @param mGatt the m gatt
	 * @param value the value
	 */
	public void ackDevice(BluetoothGatt mGatt, byte[] value) {
		BluetoothGattCharacteristic ch;
		ch = getGattChar(mGatt, GattConstant.SERVICE_VSN_SIMPLE_SERVICE,
				GattConstant.ACK_DETECT);
		writeCharacteristic(mGatt, ch, value);
	}
	/**
	 * Silent the connected BLE Devices.
	 *
	 * @param gatt the gatt
	 * @param status the status
	 */
	public void silentDevice(BluetoothGatt gatt, boolean status) {
		BluetoothGattCharacteristic ch;
		ch = getGattChar(gatt, GattConstant.SERVICE_SILENTMODE,
				GattConstant.CHAR_SILENTMODE);
		if (status == true) {
			writeCharacteristic(gatt, ch, GattConstant.ENABLE_SILENT_MODE_VALUE);
		} else {
			writeCharacteristic(gatt, ch, GattConstant.NORMAL_MODE_VALUE);
		}
	}
	/**
	 * To make the buzzer in BLE device ring.
	 *
	 * @param mGatt the m gatt
	 * @param value the value
	 */
	public void findMEDevice(BluetoothGatt mGatt, byte[] value) {
		String deviceName = dbHelper.getDeviceName(mGatt.getDevice()
				.getAddress());
		String deviceAddress = mGatt.getDevice().getAddress();
		BluetoothGattCharacteristic ch;
		ch = getGattChar(mGatt, GattConstant.SERVICE_IMMEDIATE_ALERT,
				GattConstant.CHAR_IMMEDIATE_ALERT);
		writeCharacteristic(mGatt, ch, value);
		historyLog = deviceName + "," + deviceAddress + ","
				+ getResources().getString(R.string.find_me);
		updateHistoryLogTable(historyLog);
	}
	/**
	 * To get the characteristic of the corresponding BluetoothGatt object and
	 * service UUID and Characteristic UUID.
	 *
	 * @param mGatt the m gatt
	 * @param serviceuuid the serviceuuid
	 * @param charectersticuuid the charectersticuuid
	 * @return BluetoothGattCharacteristic of the given service and
	 *         Characteristic UUID.
	 */
	public BluetoothGattCharacteristic getGattChar(BluetoothGatt mGatt,
												   UUID serviceuuid, UUID charectersticuuid) {
		gattService = mGatt.getService(serviceuuid);
		return gattService.getCharacteristic(charectersticuuid);
	}
	/**
	 * To get the List of BluetoothGattCharacteristic from the given GATT object
	 * for Service UUID.
	 *
	 * @param mGatt the m gatt
	 * @param serviceuuid the serviceuuid
	 * @return List of BluetoothGattCharacteristic.
	 */
	public List<BluetoothGattCharacteristic> getGattCharList(
			BluetoothGatt mGatt, UUID serviceuuid) {
		gattService = mGatt.getService(serviceuuid);
		return gattService.getCharacteristics();
	}
	/**
	 * To get the BluetoothGatt of the corresponding device.
	 *
	 * @param bGattkey the b gattkey
	 * @return BluetoothGatt of the device from the array
	 */
	public BluetoothGatt getGatt(String bGattkey) {
		return bluetoothGattMap.get(bGattkey);
	}
	/**
	 * Open home activity.
	 */
	// To open the home activity before the alert screen.
	public void openHomeActivity() {
		Intent i = new Intent();
		i.setClass(this, HomeActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(i);
	}
	/**
	 * Update the battery status in the database.
	 *
	 * @param name the name
	 * @param address the address
	 * @param batterystatus the batterystatus
	 */
	public void updateBatteryStatus(String name, String address,
									String batterystatus) {
		// Local notification to post the low battery
		if (Integer.parseInt(batterystatus) < 11) {
			String deviceName = dbHelper.getDeviceName(address);
			NotificationUtils.postNotification(BluetoothLeService.this,
					deviceName + " " + getString(R.string.connected) + ", "
							+ getString(R.string.low_battery),
					VALRTApplication.BLUETOOTH_CONNECT_DISCONNECT_NOTIFY_ID);
			historyLog = name + "," + address + ","
					+ getResources().getString(R.string.low_battery) + " "
					+ batteryValue + " %";
			updateHistoryLogTable(historyLog);
		}
		dbHelper.updateDeviceBatteryStatus(address, batterystatus);
		broadcastUpdate(ACTION_BATTERY_STATUS, address, batterystatus);
	}
	/**
	 * Broadcast the values to the UI if the application is in foreground.
	 *
	 * @param action the action
	 * @param value the value
	 * @param address the address
	 */
	private void broadcastUpdate(final String action, final String value,
								 final String address) {
		final Intent intent = new Intent(action);
		intent.putExtra(EXTRA_DATA, value);
		intent.putExtra(EXTRA_ADDRESS, address);
		sendBroadcast(intent);
	}
	/**
	 * Broadcast the values to the UI if the application is in foreground.
	 *
	 * @param action the action
	 * @param address the address
	 * @param status the status
	 */
	public void broadcastUpdate(final String action, final String address,
								final int status) {
		final Intent intent = new Intent(action);
		intent.putExtra(EXTRA_ADDRESS, address);
		intent.putExtra(EXTRA_STATUS, status);
		sendBroadcast(intent);
	}
	/**
	 * To put silent / normal the all connected BLE Devices.
	 *
	 * @param status the status
	 */
	public void silentAllDevice(boolean status) {
		try {
			for (BluetoothGatt gattObj : bluetoothGattMap.values()) {
				silentDevice(gattObj, status);
			}
		} catch (Exception e) {
		}
	}
	/**
	 * To disconnect the all the connected device.
	 */
	public void disConnectAllDevice() {
		try {
			for (BluetoothGatt gattObj : bluetoothGattMap.values()) {
				// disconnect all the device without notification and log.
				disconnect(gattObj, false);
			}
		} catch (Exception e) {
			LogUtils.i(VALRTApplication.TAG, e);
		}
	}
	/**
	 * To read the all connected BLE Device connection status periodically.
	 */
	public void readAllDeviceConnectionStatus() {
		try {
			if (readAllDeviceConnectionTimer == null) {
				readAllDeviceConnectionTimer = new Timer();
				readAllDeviceConnectionTimer.scheduleAtFixedRate(
						new TimerTask() {
							public void run() {
								//LogUtils.LOGD("****** Hao:", "ReadAllDeviceConnectionStatus ");

								for (String deviceAddress : dbHelper
										.getAllDeviceAddress()) {
									//LogUtils.LOGD("****** Hao:", "ReadAllDeviceConnectionStatus: "+deviceAddress);

									// Check the V.ALRT Switch is off.
									boolean isSwitchOff = VALRTApplication
											.getPrefBoolean(
													BluetoothLeService.this,
													VALRTApplication.VALRT_SWITCH_OFF);
									if (bluetoothAdapter != null) {
										if (bluetoothGattMap.size() == 0) {
											if (VALRTApplication.isScanActivityRunning == false
													&& isSwitchOff == false) {
												//LogUtils.LOGD("****** Hao:", "StartReconnect1 ");

												startService(reconnectServiceIntent);
											}
										} else if (!checkConnectionState(deviceAddress)) {
											dbHelper.updateDeviceConnectionStatus(
													deviceAddress,
													VALRTApplication.DISCONNECTED);
											broadcastUpdate(
													ACTION_GATT_DISCONNECTED,
													deviceAddress, 1);
											// To ignore the unwanted
											// notification.
											if (!reconnectList
													.contains(deviceAddress)) {
												reconnectList
														.add(deviceAddress);
												String deviceName = dbHelper
														.getDeviceName(deviceAddress);
												if (!VALRTApplication
														.getPrefBoolean(
																BluetoothLeService.this,
																VALRTApplication.VALRT_SWITCH_OFF)) {
													NotificationUtils
															.postNotification(
																	BluetoothLeService.this,
																	deviceName
																			+ " "
																			+ getString(R.string.reconncet_notification),
																	VALRTApplication.BLUETOOTH_CONNECT_DISCONNECT_NOTIFY_ID);
													historyLog = deviceName
															+ ","
															+ deviceAddress
															+ ","
															+ getResources()
															.getString(
																	R.string.reconncet_notification);
													updateHistoryLogTable(historyLog);
												}
											}
											if (VALRTApplication.isScanActivityRunning == false
													&& isSwitchOff == false) {
												//LogUtils.LOGD("****** Hao:", "StartReconnect2 ");

												startService(reconnectServiceIntent);
											}
										} else {
											stopService(reconnectServiceIntent);
										}
									} else {
										// reinitialize Bluetooth adapter if it
										// is null
										initialize();
									}
								}
							}
						}, INITIAL_TIMEER_PERIOD, CONNECTION_STATUS_PERIOD);
			}
		} catch (Exception e) {
			LogUtils.i(VALRTApplication.TAG, e);
		}
		System.gc();
	}
	/**
	 * To store the log values.
	 *
	 * @param updateLogString the update log string
	 */
	public void updateHistoryLogTable(String updateLogString) {
		dbHelper.insertDeviceHistory(updateLogString);
	}
	/**
	 * This method is to check whether the set notification value get success.If
	 * it fails we need to disconnect the GATT object.
	 *
	 * @param btGatt the bt gatt
	 */
	public void failureCheckTimer(final BluetoothGatt btGatt) {
		try {
			if (failureTimer == null) {
				failureTimer = new Timer();
			}
			failureTimer.schedule(new java.util.TimerTask() {
				@Override
				public void run() {
					// When trying to write the set notification value, need to
					// retry three times
					// if it's do not get succeed in all the three retry
					// disconnect the puck.
					if (failureCounter != 3) {
						if (btGatt != null) {
							// HN 050615: replaced gatt.disconnect() with disconnect function call
							disconnect(btGatt, false);
						}
					}
				}
			}, FAILURE_STATUS_TIMER);
		} catch (Exception e) {
			LogUtils.i(VALRTApplication.TAG, e);
		}
	}
}
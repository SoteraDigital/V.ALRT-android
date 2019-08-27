package com.vsnmobil.valrt;

import java.util.UUID;
/**
 * GattConstant.java
 *
 * This class contains all the constants which are used to do the read and write operation
 * in the Blue-tooth low energy Device.
 *
 */
public class GattConstant {
    /** The service device info. */
    // To read the device information for the device information service.
    public static UUID SERVICE_DEVICE_INFO = UUID.fromString("0000180a-0000-1000-8000-00805f9b34fb");
    /** The char serial number. */
    // Characteristic UUID to read the each puck's serial number.
    public static UUID CHAR_SERIAL_NUMBER = UUID.fromString("00002a25-0000-1000-8000-00805f9b34fb");
    /** The char software rev. */
    // Characteristic UUID to read the each puck's software version.
    public static UUID CHAR_SOFTWARE_REV = UUID.fromString("00002a28-0000-1000-8000-00805f9b34fb");
    /** The client characteristic config. */
    // Client Characteristic UUID Values to set for notification.
    public static UUID CLIENT_CHARACTERISTIC_CONFIG = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    /** The Constant ENABLE_NOTIFICATION_VALUE. */
    // To enable the notification value
    public static final byte[] ENABLE_NOTIFICATION_VALUE = { (byte) 0x01,0x00};
    /** The Constant DISABLE_NOTIFICATION_VALUE. */
    // To disable the notification value
    public static final byte[] DISABLE_NOTIFICATION_VALUE = { (byte) 0x00,0x00 };
    /** The Constant SERVICE_IMMEDIATE_ALERT. */
    // To make the buzzer in puck to make sound need to use this service.
    public static final UUID SERVICE_IMMEDIATE_ALERT = UUID.fromString("00001802-0000-1000-8000-00805f9b34fb");
    /** The Constant CHAR_IMMEDIATE_ALERT. */
    // Characteristic UUID for buzzer alert (immediate alert)
    public static final UUID CHAR_IMMEDIATE_ALERT = UUID.fromString("00002a06-0000-1000-8000-00805f9b34fb");
    /** The Constant ENABLE_IMMEDIATE_ALERT_VALUE. */
    // Value need to write to make the buzzer sound.
    public static final byte[] ENABLE_IMMEDIATE_ALERT_VALUE = new byte[] { (byte) 0x02 };
    /** The Constant SERVICE_BATTERY_LEVEL. */
    // To read the battery information form the Battery information service.
    public static final UUID SERVICE_BATTERY_LEVEL = UUID.fromString("0000180F-0000-1000-8000-00805f9b34fb");
    /** The Constant CHAR_BATTERY_LEVEL. */
    // Characteristic to read the battery status value.
    public static final UUID CHAR_BATTERY_LEVEL = UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb");
    /** The Constant SERVICE_VSN_SIMPLE_SERVICE. */
    // VSN Simple Service to listen the key press,fall detect and acknowledge and cancel the event.
    public static final UUID SERVICE_VSN_SIMPLE_SERVICE = UUID.fromString("fffffff0-00f7-4000-b000-000000000000");// 0xFFF0
    /** The Constant CHAR_KEY_PRESS. */
    // Characteristic UUID for key press and fall detect event.
    public static final UUID CHAR_KEY_PRESS = UUID.fromString("fffffff4-00f7-4000-b000-000000000000");// 0xFFF4
    /** The Constant ACK_DETECT. */
    // Characteristic UUID for acknowledge the data received and cancel the key press / fall detect event.
    public static final UUID ACK_DETECT= UUID.fromString("fffffff3-00f7-4000-b000-000000000000");// 0xFFF3
    /** The Constant RECEIVED_ACK. */
    // Value need to write the acknowledge data received.
    public static  final byte[] RECEIVED_ACK = new byte[]{(byte) 0x01};
    /** The Constant CANCEL_ACK. */
    // Value need to write to cancel the key press / fall detect.
    public static  final byte[] CANCEL_ACK = new byte[]{(byte) 0x00};
    /** The Constant CHAR_DETECTION_CONFIG. */
    //Fall detection
    public static final UUID CHAR_DETECTION_CONFIG = UUID.fromString("fffffff2-00f7-4000-b000-000000000000");// 0xFFF2
    /** The Constant CHAR_DETECTION_NOTIFY. */
    public static final UUID CHAR_DETECTION_NOTIFY = UUID.fromString("fffffff4-00f7-4000-b000-000000000000");// 0xFFF4
    /** The Constant ENABLE_FALL_KEY_DETECTION_VALUE. */
    //write 06 to this to enable button and fall detection
    public static final byte[] ENABLE_FALL_KEY_DETECTION_VALUE = new byte[] { (byte) 0x06 };
    /** The Constant ENABLE_KEY_DETECTION_VALUE. */
    public static final byte[] ENABLE_KEY_DETECTION_VALUE = new byte[] { (byte) 0x02 };
    /** The Constant SERVICE_SILENTMODE. */
    //To put the puck to silent mode and bring back to normal mode need to use this service.
    public static final UUID SERVICE_SILENTMODE = UUID.fromString("fffffff0-00f7-4000-b000-000000000000");//0xFFF0
    /** The Constant CHAR_SILENTMODE. */
    // Characteristic UUID to put device in silent mode and normal mode.
    public static final UUID CHAR_SILENTMODE = UUID.fromString("fffffff1-00f7-4000-b000-000000000000");//0xFFF1
    /** The Constant ENABLE_SILENT_MODE_VALUE. */
    // To put the device in silent mode need to write the this value to puck.
    public static final byte[] ENABLE_SILENT_MODE_VALUE = new byte[] { (byte) 0x03 };
    /** The Constant NORMAL_MODE_VALUE. */
    // To put the device to normal mode need to write the this value to puck.
    public static final byte[] NORMAL_MODE_VALUE = new byte[] { (byte) 0x00 };
    /** The Constant CHAR_APP_VERIFICATION. */
    //Characteristic UUID to secure the puck and restrict to respond to other APP.
    public static final UUID  CHAR_APP_VERIFICATION = UUID.fromString("fffffff5-00f7-4000-b000-000000000000");//0xFFF5
    /** The Constant APP_VERIFICATION_VALUE. */
    // Value need to write with in 30 seconds of connection event occurred.
    public static final byte[] APP_VERIFICATION_VALUE = { (byte) 0xBC,(byte)0xF5, (byte)0xAC,(byte)0x48,(byte)0x40};
    /** The Constant NEW_APP_VERIFICATION_VALUE. */
    // New Value need to write with in 30 seconds of connection event occurred.
    public static final byte[] NEW_APP_VERIFICATION_VALUE = { (byte) 0x80,(byte)0xBE, (byte)0xF5,(byte)0xAC,(byte)0xFF};
    /** The Constant SERVICE_ADJIST_CONNECTION_INTERVAL. */
    // Adjust connection interval using this service, we can adjust the connection interval to 1.5 seconds to save puck battery life.
    public static final UUID SERVICE_ADJIST_CONNECTION_INTERVAL = UUID.fromString("ffffccc0-00f7-4000-b000-000000000000");
    /** The Constant CHAR_ADJIST_CONNECTION_INTERVAL. */
    // Characteristic UUID for the  Adjust connection interval
    public static final UUID CHAR_ADJIST_CONNECTION_INTERVAL = UUID.fromString("ffffccc2-00f7-4000-b000-000000000000");
    /** The Constant ADJIST_CONNECTION_INTERVAL_VALUE. */
    // Value to be write to the puck. This write value need to be done after all the read / wrte operation.
    public static final byte[] ADJIST_CONNECTION_INTERVAL_VALUE = new byte[] { (byte)0x10, (byte)0x03, (byte)0x20, (byte)0x03, (byte)0x01, (byte)0x00, (byte)0x58, (byte)0x02};
    /** The Constant ADJIST_CONNECTION_INTERVAL_VALUE_MT6735. */
    // Value to be write to the puck. MTK platform doesn't support 01 slave latency.
    public static final byte[] ADJIST_CONNECTION_INTERVAL_VALUE_MT6735 = new byte[] { (byte)0x00, (byte)0x03, (byte)0x10, (byte)0x03, (byte)0x00, (byte)0x00, (byte)0x58, (byte)0x02};
    /** The Constant OAD_SWTICH_KEY_VALUE. */
    // Over the Air download (OAD): To update the pucks' software over the air need to write the value to CHAR_APP_VERIFICATION
    public static final  byte[]  OAD_SWTICH_KEY_VALUE =  { (byte) 0xD4,(byte)0xC1, (byte)0xE2,(byte)0xAC,(byte)0xF5};
    /** The Constant OAD_FORCE_RESET_KEY_VALUE. */
    // To reset the puck need to write the below value in CHAR_APP_VERIFICATION.
    public static final  byte[]  OAD_FORCE_RESET_KEY_VALUE =  { (byte) 0x03,(byte)0x02, (byte)0x73,(byte)0x8F,(byte)0x08};

}

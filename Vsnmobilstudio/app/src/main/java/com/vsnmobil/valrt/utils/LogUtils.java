package com.vsnmobil.valrt.utils;

import android.util.Log;
//import
import com.vsnmobil.valrt.BuildConfig;
/**
 * LogUtils.java 
 * To get the different types of log in the application we can use this LogUtils class.
 * By default before creating the signed APK the application will be in debug-able mode
 * Once, you created the signed APK, SDK itself change to debug-able false.Since,we have 
 * checked the condition BuildConfig.DEBUG and showing log.
 */
public class LogUtils {
    /**
     * The Constant LOG_PREFIX.
     */
    private static final String LOG_PREFIX = "V.ALRT_";
    /**
     * The Constant LOG_PREFIX_LENGTH.
     */
    private static final int LOG_PREFIX_LENGTH = LOG_PREFIX.length();
    /** The Constant MAX_LOG_TAG_LENGTH. */
    private static final int MAX_LOG_TAG_LENGTH = 23;
    /**
     * Make log tag.
     *
     * @param str the str
     * @return the string
     */
    public static String makeLogTag(String str) {
        if (str.length() > MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH) {
            return LOG_PREFIX + str.substring(0, MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH - 1);
        }
        return LOG_PREFIX + str;
    }
    /**
     * Don't use this when obfuscating class names!.
     *
     * @param cls the cls
     * @return the string
     */
    public static String makeLogTag(Class<?> cls) {
        return makeLogTag(cls.getSimpleName());
    }
    /**
     * Logd.
     *
     * @param tag the tag
     * @param message the message
     */
    public static void LOGD(final String tag, String message) {
            Log.d(tag, message);

    }
    /**
     * Logd.
     *
     * @param tag the tag
     * @param message the message
     * @param cause the cause
     */
    public static void LOGD(final String tag, String message, Throwable cause) {
            Log.d(tag, message, cause);

    }
    /**
     * Logv.
     *
     * @param tag the tag
     * @param message the message
     */
    public static void LOGV(final String tag, String message) {
            Log.v(tag, message);

    }
    /**
     * Logv.
     *
     * @param tag the tag
     * @param message the message
     * @param cause the cause
     */
    public static void LOGV(final String tag, String message, Throwable cause) {
            Log.v(tag, message, cause);

    }
    /**
     * Logi.
     *
     * @param tag the tag
     * @param message the message
     */
    public static void LOGI(final String tag, String message) {
            Log.i(tag, message);

    }
    /**
     * I.
     *
     * @param tag the tag
     * @param cause the cause
     */
    public static void i(final String tag, Throwable cause) {

            Log.i(tag, tag, cause);

    }
    /**
     * Logi.
     *
     * @param tag the tag
     * @param message the message
     * @param cause the cause
     */
    public static void LOGI(final String tag, String message, Throwable cause) {
            Log.i(tag, message, cause);

    }
    /**
     * Logw.
     *
     * @param tag the tag
     * @param message the message
     */
    public static void LOGW(final String tag, String message) {
            Log.w(tag, message);

    }
    /**
     * Logw.
     *
     * @param tag the tag
     * @param message the message
     * @param cause the cause
     */
    public static void LOGW(final String tag, String message, Throwable cause) {
            Log.w(tag, message, cause);

    }
    /**
     * Loge.
     *
     * @param tag the tag
     * @param message the message
     */
    public static void LOGE(final String tag, String message) {
            Log.e(tag, message);
    }
    /**
     * Loge.
     *
     * @param tag the tag
     * @param message the message
     * @param cause the cause
     */
    public static void LOGE(final String tag, String message, Throwable cause) {
            Log.e(tag, message, cause);

    }
    /**
     * Instantiates a new log utils.
     */
    private LogUtils() {
    }
}

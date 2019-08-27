package com.vsnmobil.valrt;

import android.content.BroadcastReceiver;
//import
import android.content.Context;
//import
import android.content.Intent;
//import
import android.net.Uri;
//import
import com.vsnmobil.valrt.services.ReconnectService;
//import
import com.vsnmobil.valrt.storage.DatabaseHelper;
//import
import com.vsnmobil.valrt.utils.NotificationUtils;
/**
 * UpdatePackageReceiver.java when the user upgrade to new version of APP this
 * receiver will be called. if user already connected any puck with the APP it
 * will be disconnected. To reconnect in again it will start the
 * ReconnectService.java.
 */
public class UpdatePackageReceiver extends BroadcastReceiver {
    /** The db helper. */
    private DatabaseHelper dbHelper;
    /** The package replaced. */
    private final String PACKAGE_REPLACED = "android.intent.action.PACKAGE_REPLACED";
    /* (non-Javadoc)
     * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        dbHelper = new DatabaseHelper(context);
        final String action = intent.getAction();
        Uri data = intent.getData();
        if (action.equals(PACKAGE_REPLACED)) {
            String packageName = data.getEncodedSchemeSpecificPart();
            // Check the package name and update the connection status.
            if (packageName.equals(context.getPackageName())) {
                // set the panic tone enable.
                VALRTApplication.setPrefBoolean(context,VALRTApplication.PANICTONECBX,true);
                if (dbHelper.getPairedDeviceCount() != 0) {
                    // dbHelper.updateConnectionStatus();
                    if(!VALRTApplication.getPrefBoolean(context, VALRTApplication.VALRT_SWITCH_OFF)){
                        if(VALRTApplication.isUpgraded = false)
                            NotificationUtils.postNotification(context,context.getString(R.string.upgrade_relaunch_app),VALRTApplication.BLUETOOTH_CONNECT_DISCONNECT_NOTIFY_ID);
                        VALRTApplication.isUpgraded = false;
                        if (VALRTApplication.isScanActivityRunning == false)
                            context.startService(new Intent(context, ReconnectService.class));
                    }
                }
            }
        }
    }
}

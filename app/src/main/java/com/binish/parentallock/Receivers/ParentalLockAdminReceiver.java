package com.binish.parentallock.Receivers;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;

public class ParentalLockAdminReceiver extends DeviceAdminReceiver {
    @Override
    public void onDisabled(Context context, Intent intent) {
        super.onDisabled(context, intent);
    }

    @Override
    public void onEnabled(Context context, Intent intent) {
        super.onEnabled(context, intent);
    }

    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {
        DevicePolicyManager deviceManger = (DevicePolicyManager)context.getSystemService(
                Context.DEVICE_POLICY_SERVICE);
        deviceManger.lockNow();
        return "Disabling rights might cause problems with the app";
    }
}

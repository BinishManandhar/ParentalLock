package com.binish.parentallock.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.binish.parentallock.Utils.UsefulFunctions;
import com.binish.parentallock.services.Service;

public class ServiceInitiateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (!UsefulFunctions.isMyServiceRunning(context, Service.class)) {
            context.startService(new Intent(context,Service.class));
        }
    }
}

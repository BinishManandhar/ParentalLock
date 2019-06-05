package com.binish.parentallock.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.binish.parentallock.Services.Service;

public class ServiceDestroyReceiver extends BroadcastReceiver {
    String LOGTAG = "PackageNames";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(LOGTAG,"Restart Command Received");
        context.startService(new Intent(context,Service.class));
    }
}

package com.binish.parentallock.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.binish.parentallock.Services.Service;
import com.binish.parentallock.Utils.UsefulFunctions;

public class ServiceDestroyReceiver extends BroadcastReceiver {
    String LOGTAG = "LockScreenLog";
    @Override
    public void onReceive(final Context context, Intent intent) {
        if(!UsefulFunctions.isMyServiceRunning(context,Service.class)) {
            Log.i(LOGTAG,""+UsefulFunctions.isMyServiceRunning(context,Service.class));
            Thread thread = new Thread(){
                @Override
                public void run() {
                    super.run();
                    context.startService(new Intent(context, Service.class));
                }
            };
            thread.run();
        }
    }
}

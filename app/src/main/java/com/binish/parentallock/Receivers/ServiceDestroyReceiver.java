package com.binish.parentallock.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.util.Log;

import com.binish.parentallock.services.JobService;
import com.binish.parentallock.services.Service;
import com.binish.parentallock.Utils.UsefulFunctions;

public class ServiceDestroyReceiver extends BroadcastReceiver {
    String LOGTAG = "LockScreenLog";

    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.i(LOGTAG, "" + UsefulFunctions.isMyServiceRunning(context, JobService.class));
        if (!UsefulFunctions.isMyServiceRunning(context, JobService.class)) {

//            context.startService(new Intent(context, Service.class));
            if (!UsefulFunctions.isJobServiceOn(context))
                UsefulFunctions.startJobService(context);
            else {
                UsefulFunctions.cancelJobService(context,UsefulFunctions.JOB_ID);
            }
        }
    }
}

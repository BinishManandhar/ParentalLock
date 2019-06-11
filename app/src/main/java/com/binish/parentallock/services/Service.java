package com.binish.parentallock.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import com.binish.parentallock.Receivers.ServiceDestroyReceiver;
import com.binish.parentallock.Utils.UsefulFunctions;

public class Service extends android.app.Service {

    CountDownTimer checking;
    String dummy = "";
    String LOGS = "PackageNames";
    Thread thread;
    ForegroundBinder mBinder;

    @Override
    public IBinder onBind(Intent intent) {
        if (mBinder == null){
            mBinder = new ForegroundBinder();
        }
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Toast.makeText(this, "ParentalLock Service Started", Toast.LENGTH_SHORT).show();
        Log.i("LockScreenLog", "onStartCommand");
        thread = new Thread() {
            @Override
            public void run() {
                if (Looper.myLooper() == null)
                    Looper.prepare();
                try {
                    Thread.sleep(1000);
                    Log.i(LOGS, "Running: "+UsefulFunctions.getForegroundApp(Service.this));
                    if (UsefulFunctions.checkLockUnlock(Service.this, UsefulFunctions.getForegroundApp(Service.this))) {
                        PackageManager packageManager = Service.this.getPackageManager();

                        try {
                            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(UsefulFunctions.getForegroundApp(Service.this), 0);
                            String appName = (String) packageManager.getApplicationLabel(applicationInfo);
                            Drawable appIcon = packageManager.getApplicationIcon(applicationInfo);
                            int color = UsefulFunctions.getAppColour(Service.this, applicationInfo, UsefulFunctions.getForegroundApp(Service.this));
                            dummy = applicationInfo.packageName;
                            if (UsefulFunctions.getPassValue(Service.this, UsefulFunctions.getForegroundApp(Service.this)))
                                UsefulFunctions.showLockScreen(Service.this, appName, appIcon, color, applicationInfo.packageName);

                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                    } else {
                        UsefulFunctions.changePassCheck(Service.this, true, dummy);
                    }
                    dummy = UsefulFunctions.getForegroundApp(Service.this);
                    thread.run();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();



        return START_REDELIVER_INTENT;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent in = new Intent(this, ServiceDestroyReceiver.class);
        sendBroadcast(in);
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onDestroy() {
        UsefulFunctions.initiateAlarm(this);
        super.onDestroy();
    }

    public class ForegroundBinder extends Binder
    {
        public Service getService()
        {
            // Return this instance of ForegroundService
            // so clients can call public methods
            return Service.this;
        }
    }
}

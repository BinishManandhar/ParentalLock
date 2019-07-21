package com.binish.parentallock.services;

import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import com.binish.parentallock.Receivers.ServiceInitiateReceiver;
import com.binish.parentallock.Utils.ThreadClass;
import com.binish.parentallock.Utils.UsefulFunctions;

public class Service extends android.app.Service {

    CountDownTimer checking;
    String dummy = "";
    String LOGS = "PackageNames";
    ThreadClass thread;
    TimerTaskService tImerTaskService;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        tImerTaskService.initialize();
        Log.i("LockScreenLog", "onStartCommand");
        if(!thread.isAlive())
            thread.run();
        UsefulFunctions.initiateAlarm(this);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        thread = new ThreadClass(this);
        thread.setDaemon(true);
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();
        super.onCreate();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        UsefulFunctions.initiateAlarm(this);
        sendBroadcast(new Intent(this,ServiceInitiateReceiver.class));
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onDestroy() {
        UsefulFunctions.initiateAlarm(this);
        sendBroadcast(new Intent(this,ServiceInitiateReceiver.class));
        super.onDestroy();
    }

}

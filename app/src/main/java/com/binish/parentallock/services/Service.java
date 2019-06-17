package com.binish.parentallock.services;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.Looper;
import android.os.Process;
import android.util.Log;

import com.binish.parentallock.Receivers.ServiceInitiateReceiver;
import com.binish.parentallock.Utils.ThreadClass;
import com.binish.parentallock.Utils.TimerTaskService;
import com.binish.parentallock.Utils.UsefulFunctions;

public class Service extends android.app.Service {

    CountDownTimer checking;
    String dummy = "";
    String LOGS = "PackageNames";
    ThreadClass thread;
    TimerTaskService tImerTaskService;
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
//        tImerTaskService.initialize();
        Log.i("LockScreenLog", "onStartCommand");
        if(!thread.isAlive())
            thread.run();
        tImerTaskService.initialize();
        UsefulFunctions.initiateAlarm(this);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        tImerTaskService = new TimerTaskService(this);
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

    private void interruptingThread(){
        UsefulFunctions.initiateAlarm(this);
        new Thread(){
            @Override
            public void run() {
                try {
                    Thread.sleep(60*1000);
                    thread.interrupt();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
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

package com.binish.parentallock.services;

import android.content.Intent;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;

import com.binish.parentallock.Receivers.ServiceInitiateReceiver;
import com.binish.parentallock.Utils.TimerTaskService;
import com.binish.parentallock.Utils.UsefulFunctions;

public class Service extends android.app.Service {

    CountDownTimer checking;
    String dummy = "";
    String LOGS = "PackageNames";
    Thread thread;
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
        tImerTaskService.initialize();
        /*Log.i("LockScreenLog", "onStartCommand");
        if(thread!=null){thread.interrupt();}
        thread = new Thread() {
            @Override
            public void run() {
                if (Looper.myLooper() == null)
                    Looper.prepare();
                try {
                    Thread.sleep(800);
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
        thread.start();*/

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        tImerTaskService = new TimerTaskService(this);
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

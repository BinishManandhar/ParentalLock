package com.binish.parentallock.services;

import android.app.job.JobParameters;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.util.Log;

import com.binish.parentallock.Utils.UsefulFunctions;

public class JobService extends android.app.job.JobService {
    String dummy="";
    Thread thread;
    String LOGS="PackageNames";
    @Override
    public boolean onStartJob(JobParameters params) {
        thread = new Thread(){
            @Override
            public void run() {
                if(Looper.myLooper()==null)
                    Looper.prepare();
                try {
                    Thread.sleep(500);
                    Log.i(LOGS, "Running Job: "+UsefulFunctions.getForegroundApp(JobService.this));
                    if (UsefulFunctions.checkLockUnlock(JobService.this, UsefulFunctions.getForegroundApp(JobService.this))
                            && UsefulFunctions.checkLockUnlockTime(JobService.this,UsefulFunctions.getForegroundApp(JobService.this))) {
                        PackageManager packageManager = JobService.this.getPackageManager();

                        try {
                            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(UsefulFunctions.getForegroundApp(JobService.this), 0);
                            String appName = (String) packageManager.getApplicationLabel(applicationInfo);
                            Drawable appIcon = packageManager.getApplicationIcon(applicationInfo);
                            int color = UsefulFunctions.getAppColour(JobService.this, applicationInfo, UsefulFunctions.getForegroundApp(JobService.this));
                            dummy = applicationInfo.packageName;
                            if (UsefulFunctions.getPassValue(JobService.this, UsefulFunctions.getForegroundApp(JobService.this)))
                                UsefulFunctions.showLockScreen(JobService.this, appName, appIcon, color, applicationInfo.packageName);

                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                    } else {
                        UsefulFunctions.changePassCheck(JobService.this, true, dummy);
                    }
                    dummy = UsefulFunctions.getForegroundApp(JobService.this);
                    thread.run();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.i("LockScreenLog","onStopJob");
        thread.interrupt();
        jobFinished(params,false);
        stopSelf();
        stopService(new Intent(this,JobService.class));
        UsefulFunctions.initiateAlarm(this);
        return true;
    }
}

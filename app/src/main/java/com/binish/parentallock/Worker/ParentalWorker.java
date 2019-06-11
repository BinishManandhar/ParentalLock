package com.binish.parentallock.Worker;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;

import com.binish.parentallock.Utils.UsefulFunctions;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class ParentalWorker extends Worker {
    String dummy="";
    String LOGS = "WorkerManagerLogs";
    Context context;
    Thread thread;
    public ParentalWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        return myWork();
    }
    
    private Result myWork(){
        thread = new Thread(){
            @Override
            public void run() {
                if(Looper.myLooper()==null)
                    Looper.prepare();
                try {
                    Thread.sleep(600);
                    Log.i(LOGS,"MyWork");
                    if (UsefulFunctions.checkLockUnlock(context, UsefulFunctions.getForegroundApp(context))) {
                        PackageManager packageManager = context.getPackageManager();

                        try {
                            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(UsefulFunctions.getForegroundApp(context), 0);
                            String appName = (String) packageManager.getApplicationLabel(applicationInfo);
                            Drawable appIcon = packageManager.getApplicationIcon(applicationInfo);
                            int color = UsefulFunctions.getAppColour(context, applicationInfo, UsefulFunctions.getForegroundApp(context));
                            dummy = applicationInfo.packageName;
                            if (UsefulFunctions.getPassValue(context, UsefulFunctions.getForegroundApp(context)))
                                UsefulFunctions.showLockScreen(context, appName, appIcon, color, applicationInfo.packageName);

                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                    } else {
                        UsefulFunctions.changePassCheck(context, true, dummy);
                    }
                    dummy = UsefulFunctions.getForegroundApp(context);
                    thread.run();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();

        return Worker.Result.success();
    }
}

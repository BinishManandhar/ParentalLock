package com.binish.parentallock.Utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class TimerTaskService {
    Context context;
    Timer mTimer;
    int CHECK_INTERVAL = 500;
    
    public TimerTaskService(Context context){
        this.context = context;
    }
    public void initialize(){
        // cancel if already existed
        if (mTimer != null) {
            mTimer.cancel();
            initialize();
        } else {
            // recreate new
            mTimer = new Timer();
        }
        // schedule task
        mTimer.schedule(new RunningTimerTask(context), 1000, CHECK_INTERVAL);
    }
}
class RunningTimerTask extends TimerTask{
    Context context;
    String dummy = "";

    public RunningTimerTask(Context context){
        this.context = context;
    }
    String LOGS = "PackageNames";
    @Override
    public void run() {
        if (Looper.myLooper() == null)
            Looper.prepare();
        Log.i(LOGS, "Running TimerTask: "+UsefulFunctions.getForegroundApp(context));
        if (UsefulFunctions.checkLockUnlock(context, UsefulFunctions.getForegroundApp(context))
                && UsefulFunctions.checkLockUnlockTime(context,UsefulFunctions.getForegroundApp(context))) {
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
    }
}

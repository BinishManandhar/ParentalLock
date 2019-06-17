package com.binish.parentallock.Utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.util.Log;

public class ThreadClass extends Thread {
    Context context;
    public ThreadClass(Context context){
        this.context = context;
    }
    String dummy = "";
    String LOGS = "PackageNames";
    @Override
    public void run() {
        if (Looper.myLooper() == null)
            Looper.prepare();
        try {
            Thread.sleep(800);
            Log.i(LOGS, "Running: "+UsefulFunctions.getForegroundApp(context));
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
            this.run();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        super.run();
    }
}

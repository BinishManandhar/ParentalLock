package com.binish.parentallock.Services;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.binish.parentallock.Receivers.ServiceDestroyReceiver;
import com.binish.parentallock.Utils.UsefulFunctions;

public class Service extends android.app.Service {

    CountDownTimer checking;
    String dummy="";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Toast.makeText(this, "ParentalLock Service Started", Toast.LENGTH_SHORT).show();
        checking = new CountDownTimer(60 * 1000, 600) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.i("PackageNames","Running: "+UsefulFunctions.getForegroundApp(Service.this));

                if(UsefulFunctions.getForegroundApp(Service.this).equals("com.app.alexa")){
                    PackageManager packageManager = getPackageManager();

                    try {
                        ApplicationInfo applicationInfo = packageManager.getApplicationInfo(UsefulFunctions.getForegroundApp(Service.this),0);
                        String appName = (String) packageManager.getApplicationLabel(applicationInfo);
                        Drawable appIcon = packageManager.getApplicationIcon(applicationInfo);
                        int color = UsefulFunctions.getAppColour(Service.this,applicationInfo,UsefulFunctions.getForegroundApp(Service.this));
                        dummy = applicationInfo.packageName;
                        Log.i("PassValue","Value: "+UsefulFunctions.getPassValue(Service.this,"com.musixmatch.android.lyrify"));
                        if(UsefulFunctions.getPassValue(Service.this,"com.app.alexa"))
                            UsefulFunctions.showLockScreen(Service.this,appName,appIcon,color,applicationInfo.packageName);

                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    UsefulFunctions.changePassCheck(Service.this,true,dummy);
                }
                dummy = UsefulFunctions.getForegroundApp(Service.this);
            }

            @Override
            public void onFinish() {
                checking.start();
            }
        }.start();

        return START_REDELIVER_INTENT;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Intent in = new Intent(this, ServiceDestroyReceiver.class);
        sendBroadcast(in);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        Toast.makeText(this, "ParentalLock Service Destroyed", Toast.LENGTH_SHORT).show();
//        Intent in = new Intent(this, ServiceDestroyReceiver.class);
//        sendBroadcast(in);
    }

}

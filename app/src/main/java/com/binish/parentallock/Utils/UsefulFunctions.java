package com.binish.parentallock.Utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AppOpsManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.PowerManager;
import android.os.Process;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.binish.parentallock.Database.DatabaseHelper;
import com.binish.parentallock.LockScreen.LockScreen;
import com.binish.parentallock.Models.LockUnlockModel;
import com.binish.parentallock.Models.ProfileModel;
import com.binish.parentallock.R;
import com.binish.parentallock.Receivers.ServiceDestroyReceiver;
import com.binish.parentallock.services.JobService;
import com.binish.parentallock.services.Service;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.SortedMap;
import java.util.TreeMap;

public class UsefulFunctions {

    public static int JOB_ID = 11;


    public static String getForegroundApp(Context context) {
        String currentApp = "NULL";
        UsageStatsManager usm = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        long time = System.currentTimeMillis();
        List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 1000, time);

        if (appList != null && appList.size() > 0) {
            SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
            for (UsageStats usageStats : appList) {
                mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
            }
            if (mySortedMap != null && !mySortedMap.isEmpty()) {
                currentApp = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
            }
        }

        return currentApp;
    }

    public static void usageAccessSettingsPage(Context context) {
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static boolean usageAccessCheck(Context context) {
        boolean granted;
        AppOpsManager appOps = (AppOpsManager) context
                .getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(), context.getPackageName());

        if (mode == AppOpsManager.MODE_DEFAULT) {
            granted = (context.checkCallingOrSelfPermission(Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED);

        } else {
            granted = (mode == AppOpsManager.MODE_ALLOWED);
        }

        return granted;
    }

    public static void showLockScreen(Context context, String appName, Drawable appIcon, int appColor, String packageName) {
        Intent startMain = new Intent(context, LockScreen.class);
//        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startMain.putExtra("appName", appName);
        startMain.putExtra("appColor", appColor);
        startMain.putExtra("packageName", packageName);
        startMain.putExtra("appIcon", drawableToByte(appIcon));
        context.startActivity(startMain);
    }

    public static List<ApplicationInfo> getAppList(Context context) {

        PackageManager packageManager = context.getPackageManager();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> appList = packageManager.queryIntentActivities(mainIntent, 0);
        Collections.sort(appList, new ResolveInfo.DisplayNameComparator(packageManager));
        ArrayList<String> packs = new ArrayList<>();
        ArrayList<ApplicationInfo> applicationInfoList = new ArrayList<>();
        for (ResolveInfo resolveInfo : appList) {
            ActivityInfo activityInfo = resolveInfo.activityInfo;
            if (!UsefulFunctions.isSystemPackage(resolveInfo)) {
                packs.add(activityInfo.applicationInfo.packageName);

            }
        }
        for (int i = 0; i < packs.size(); i++) {
            ApplicationInfo a = null;
            try {
                a = packageManager.getApplicationInfo(packs.get(i), 0);
                applicationInfoList.add(a);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Log.i("PackageNames", "PackageName: " + packageManager.getApplicationLabel(a));
        }
        return applicationInfoList;
    }

    private static boolean isSystemPackage(ResolveInfo resolveInfo) {

        return ((resolveInfo.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }

    public static int getAppColour(Context context, ApplicationInfo applicationInfo, String packageName) {
        PackageManager pm = context.getPackageManager();
        try {
            Resources res = pm.getResourcesForApplication(applicationInfo);
            final int[] attrs = new int[]{
                    /** AppCompat attr */
                    res.getIdentifier("colorAccent", "attr", packageName),
                    /** Framework attr */
                    android.R.attr.colorPrimary
            };
            Resources.Theme theme = res.newTheme();
            theme.applyStyle(applicationInfo.theme, false);
            TypedArray a = theme.obtainStyledAttributes(attrs);
            int color = a.getColor(0, a.getColor(1, Color.WHITE));
            String hexColor = String.format("#%06X", (0xFFFFFF & color));
            Log.i("AppColor", "Color: " + hexColor);
            return color;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static byte[] drawableToByte(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        return b;
    }

    public static boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo serviceInfo : manager.getRunningServices(Integer.MAX_VALUE)) {
            Log.i("LockScreenLog", "Service: " + serviceInfo.service.getClassName());

            if (serviceClass.getName().equals(serviceInfo.service.getClassName())) {
                Log.i("LockScreenLog", "My Service: " + serviceClass.getName());
                return true;
            }
        }
        return false;
    }

    private static void insertAppListIntoDatabase(String packageName, Context context) {
        Log.i("PassValue", "Name: " + packageName);
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        databaseHelper.insertPassCheck(packageName, true);
    }

    public static void changePassCheck(Context context, boolean check, String packageName) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        databaseHelper.changePassCheck(packageName, check);
    }

    public static boolean getPassValue(Context context, String packageName) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        return databaseHelper.getPassCheck(packageName);
    }

    public static void appListDataFunction(Context context) {
        dropTableAtFirstOpen(context);
        createTableLockUnlock(context);
        List<ApplicationInfo> list = UsefulFunctions.getAppList(context);

        for (ApplicationInfo applicationInfo : list
                ) {
            UsefulFunctions.insertAppListIntoDatabase(applicationInfo.packageName, context);
        }
    }

    public static List<LockUnlockModel> getListForRecycler(Context context, List<ApplicationInfo> appList) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        List<LockUnlockModel> list = new ArrayList<>();
        for (ApplicationInfo applicationInfo : appList) {
            LockUnlockModel lockUnlockModel = new LockUnlockModel();
            lockUnlockModel.setApplicationInfo(applicationInfo);
            lockUnlockModel.setLockUnlockProfile(databaseHelper.getLockUnlockProfileName(applicationInfo.packageName));
            if (checkLockUnlock(context, applicationInfo.packageName))
                lockUnlockModel.setDrawableInt(R.drawable.ic_lock_red_24dp);
            else
                lockUnlockModel.setDrawableInt(R.drawable.ic_lock_open_green_24dp);
            list.add(lockUnlockModel);
        }
        return list;
    }

    private static void dropTableAtFirstOpen(Context context) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        databaseHelper.dropTablePassCheck();

    }

    private static void createTableLockUnlock(Context context) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        databaseHelper.createLockUnlockTable();
    }

    public static boolean checkLockUnlock(Context context, String packageName) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        return databaseHelper.checkLockUnlock(packageName);
    }

    public static boolean checkLockUnlockTime(Context context, String packageName) {
        try {
            DatabaseHelper databaseHelper = new DatabaseHelper(context);
            String profileName = databaseHelper.getLockUnlockProfileName(packageName);

            if(!profileName.equals("")) {
                ProfileModel profileModel = databaseHelper.getIndividualProfile(profileName);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a", Locale.US);
                SimpleDateFormat simpleDateFormatFull = new SimpleDateFormat("HH:mm", Locale.US);

                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(System.currentTimeMillis());
                String time = c.get(Calendar.HOUR_OF_DAY)+":"+c.get(Calendar.MINUTE);

                Date unlockFrom = simpleDateFormat.parse(profileModel.getUnlockFrom());
                Date currentTime = simpleDateFormatFull.parse(time);
                Date unlockTo = simpleDateFormat.parse(profileModel.getUnlockTo());
                if(currentTime.getTime() >= unlockFrom.getTime()
                        && currentTime.getTime() <= unlockTo.getTime()
                        && profileModel.isActive())
                    return false;
                if(!profileModel.isActive())
                    return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static void startJobService(Context context) {
        Log.i("LockScreenLog", "StartJobService");
        JobScheduler jobScheduler;
        JobInfo jobInfo;
        jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

        jobInfo = new JobInfo.Builder(JOB_ID, new ComponentName(context, JobService.class))
                .setOverrideDeadline(0)
                .build();
        jobScheduler.schedule(jobInfo);
    }

    public static void cancelJobService(Context context, int jobID) {
        JobScheduler jobScheduler;
        jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.cancelAll();
        if (!isJobServiceOn(context))
            startJobService(context);
    }


    public static boolean isJobServiceOn(Context context) {
        JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

        boolean hasBeenScheduled = false;

        for (JobInfo jobInfo : scheduler.getAllPendingJobs()) {
            if (jobInfo.getId() == JOB_ID) {
                hasBeenScheduled = true;
                break;
            }
        }

        return hasBeenScheduled;
    }

    public static void initiateAlarm(Context context) {
        final int TIME_TO_INVOKE = 60 * 1000;
        final int SERVICE_INVOKE = 60 * 60 * 1000;

        Intent intent = new Intent(context, ServiceDestroyReceiver.class);
        Intent intentService = new Intent(context, Service.class);

        AlarmManager alarms = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);



        PendingIntent pendingIntent = PendingIntent
                .getBroadcast(context, GlobalStaticVariables.ALARM_BROADCAST_SERVICE_DESTROY, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        PendingIntent pendingServiceIntent = PendingIntent
                .getService(context, GlobalStaticVariables.ALARM_START_SERVICE, intentService, PendingIntent.FLAG_CANCEL_CURRENT);

        boolean alarmUp = (PendingIntent.getBroadcast(context, GlobalStaticVariables.ALARM_BROADCAST_SERVICE_DESTROY,
                intent,
                PendingIntent.FLAG_NO_CREATE) != null);
        boolean serviceAlarmUp = (PendingIntent
                .getService(context, GlobalStaticVariables.ALARM_START_SERVICE, intentService, PendingIntent.FLAG_NO_CREATE) != null);

        if (alarmUp) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarms.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() +
                        TIME_TO_INVOKE, pendingIntent);
                alarms.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() +
                        SERVICE_INVOKE, pendingServiceIntent);
            }
        }
        if (serviceAlarmUp) {
            alarms.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() +
                    SERVICE_INVOKE, SERVICE_INVOKE, pendingServiceIntent);
        }
    }

    public static void showTimeDialog(Context context, final View parentView, final int viewID) {
        final Dialog dialog = new Dialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.time_picker_box, null);
        dialog.addContentView(view, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        dialog.show();
        final TimePicker timePicker = view.findViewById(R.id.timepicker);
        view.findViewById(R.id.settime).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hourOfDay, minute;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    hourOfDay = timePicker.getHour();
                    minute = timePicker.getMinute();
                } else {
                    hourOfDay = timePicker.getCurrentHour();
                    minute = timePicker.getCurrentMinute();
                }

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a", Locale.US);
                Date finalTime = calendar.getTime();
                Button unlockFrom = parentView.findViewById(viewID);
                unlockFrom.setText(simpleDateFormat.format(finalTime));
                dialog.dismiss();

            }
        });
        view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    @TargetApi(23)
    public static void checkBatteryOptimization(final Context context){
        String packageName = context.getPackageName();
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (!pm.isIgnoringBatteryOptimizations(packageName)) {
            new AlertDialog.Builder(context)
                    .setTitle("Disable battery optimization for better functionality")
                    .setMessage("Go to Settings->Battery Optimization->All Apps->Parental Lock->Don't optimize")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ignoreBatteryOptimizations(context);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(context,"Disable battery optimization for full functionality",Toast.LENGTH_LONG).show();
                        }
                    }).show();
        }
    }

    @TargetApi(23)
    public static void ignoreBatteryOptimizations(Context context) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }
        String packageName = context.getPackageName();
        try {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);

        } catch (Exception ex) {
            Log.wtf("IGNORE:BATTERY", ex);
        }


    }
}

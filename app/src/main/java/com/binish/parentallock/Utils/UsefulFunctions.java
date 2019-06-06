package com.binish.parentallock.Utils;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
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
import android.os.Process;
import android.provider.Settings;
import android.util.Log;

import com.binish.parentallock.Database.DatabaseHelper;
import com.binish.parentallock.LockScreen.LockScreen;
import com.binish.parentallock.Models.LockUnlockModel;
import com.binish.parentallock.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class UsefulFunctions {
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

    public static void showLockScreen(Context context, String appName, Drawable appIcon,int appColor,String packageName) {
        Intent startMain = new Intent(context,LockScreen.class);
//        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startMain.putExtra("appName",appName);
        startMain.putExtra("appColor",appColor);
        startMain.putExtra("packageName",packageName);
        startMain.putExtra("appIcon",drawableToByte(appIcon));
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

    public static int getAppColour(Context context,ApplicationInfo applicationInfo,String packageName){
        PackageManager pm = context.getPackageManager();
        try {
            Resources res = pm.getResourcesForApplication(applicationInfo);
            final int[] attrs = new int[] {
                    /** AppCompat attr */
                    res.getIdentifier("colorAccent", "attr", packageName),
                    /** Framework attr */
                    android.R.attr.colorPrimary
            };
            Resources.Theme theme = res.newTheme();
            theme.applyStyle(applicationInfo.theme,false);
            TypedArray a = theme.obtainStyledAttributes(attrs);
            int color = a.getColor(0,a.getColor(1, Color.WHITE));
            String hexColor = String.format("#%06X", (0xFFFFFF & color));
            Log.i("AppColor","Color: "+hexColor);
            return color;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static byte[] drawableToByte(Drawable drawable){
        Bitmap bitmap = null;

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
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

    public static boolean isMyServiceRunning(Context context,Class<?> serviceClass){
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo serviceInfo: manager.getRunningServices(Integer.MAX_VALUE)){
            Log.i("LockScreenLog","Service: "+serviceInfo.service.getClassName());

            if(serviceClass.getName().equals(serviceInfo.service.getClassName())) {
                Log.i("LockScreenLog","My Service: "+serviceClass.getName());
                return true;
            }
        }
        return false;
    }

    private static void insertAppListIntoDatabase(String packageName, Context context){
        Log.i("PassValue","Name: "+packageName);
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        databaseHelper.insertPassCheck(packageName,true);
    }

    public static void changePassCheck(Context context,boolean check,String packageName){
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        databaseHelper.changePassCheck(packageName,check);
    }

    public static boolean getPassValue(Context context,String packageName){
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        return databaseHelper.getPassCheck(packageName);
    }

    public static void appListDataFunction(Context context){
        dropTableAtFirstOpen(context);
        createTableLockUnlock(context);
        List<ApplicationInfo> list = UsefulFunctions.getAppList(context);

        for (ApplicationInfo applicationInfo: list
             ) {
            UsefulFunctions.insertAppListIntoDatabase(applicationInfo.packageName,context);
        }
    }

    public static List<LockUnlockModel> getListForRecycler(Context context, List<ApplicationInfo> appList){
        List<LockUnlockModel> list = new ArrayList<>();
        for (ApplicationInfo applicationInfo:appList) {
            LockUnlockModel lockUnlockModel = new LockUnlockModel();
            lockUnlockModel.setApplicationInfo(applicationInfo);
            if(checkLockUnlock(context,applicationInfo.packageName))
                lockUnlockModel.setDrawableInt(R.drawable.ic_lock_red_24dp);
            else
                lockUnlockModel.setDrawableInt(R.drawable.ic_lock_open_red_24dp);
            list.add(lockUnlockModel);
        }
        return list;
    }

    private static void dropTableAtFirstOpen(Context context){
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        databaseHelper.dropTablePassCheck();

    }

    private static void createTableLockUnlock(Context context){
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        databaseHelper.createLockUnlockTable();
    }

    public static boolean checkLockUnlock(Context context, String packageName){
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        return databaseHelper.checkLockUnlock(packageName);
    }
}

package com.binish.parentallock.Models;

import android.content.pm.ApplicationInfo;

public class LockUnlockModel {
    private ApplicationInfo applicationInfo;
    private int drawableInt;

    public ApplicationInfo getApplicationInfo() {
        return applicationInfo;
    }

    public void setApplicationInfo(ApplicationInfo applicationInfo) {
        this.applicationInfo = applicationInfo;
    }

    public int getDrawableInt() {
        return drawableInt;
    }

    public void setDrawableInt(int drawableInt) {
        this.drawableInt = drawableInt;
    }
}

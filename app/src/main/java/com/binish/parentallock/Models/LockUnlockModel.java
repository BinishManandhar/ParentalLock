package com.binish.parentallock.Models;

import android.content.pm.ApplicationInfo;

public class LockUnlockModel {
    private ApplicationInfo applicationInfo;
    private int drawableInt;
    private String lockUnlockProfile;
    private int lockUnlockPasswordDrawable;
    private int lockUnlockFingerprintDrawable;

    public int getLockUnlockFingerprintDrawable() {
        return lockUnlockFingerprintDrawable;
    }

    public void setLockUnlockFingerprintDrawable(int lockUnlockFingerprintDrawable) {
        this.lockUnlockFingerprintDrawable = lockUnlockFingerprintDrawable;
    }

    public int getLockUnlockPasswordDrawable() {
        return lockUnlockPasswordDrawable;
    }

    public void setLockUnlockPasswordDrawable(int lockUnlockPasswordDrawable) {
        this.lockUnlockPasswordDrawable = lockUnlockPasswordDrawable;
    }

    public String getLockUnlockProfile() {
        return lockUnlockProfile;
    }

    public void setLockUnlockProfile(String lockUnlockProfile) {
        this.lockUnlockProfile = lockUnlockProfile;
    }

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

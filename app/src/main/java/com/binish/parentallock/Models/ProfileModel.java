package com.binish.parentallock.Models;

public class ProfileModel {
    String profileName;
    String unlockFrom;
    String unlockTo;
    boolean isActive;

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public String getUnlockFrom() {
        return unlockFrom;
    }

    public void setUnlockFrom(String unlockFrom) {
        this.unlockFrom = unlockFrom;
    }

    public String getUnlockTo() {
        return unlockTo;
    }

    public void setUnlockTo(String unlockTo) {
        this.unlockTo = unlockTo;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}

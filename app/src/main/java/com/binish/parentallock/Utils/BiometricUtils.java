package com.binish.parentallock.Utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.biometrics.BiometricPrompt;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;

public class BiometricUtils {

    public static boolean isBiometricPromptEnabled(){
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P);
    }

    public static boolean isSDKVersionSupported(){
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);
    }

    public static boolean isHardwareSupported(Context context){
        FingerprintManagerCompat fingerprintManagerCompat = FingerprintManagerCompat.from(context);
        return fingerprintManagerCompat.isHardwareDetected();
    }

    public static boolean isFingerprintAvailable(Context context){
        FingerprintManagerCompat fingerprintManagerCompat = FingerprintManagerCompat.from(context);
        return  fingerprintManagerCompat.hasEnrolledFingerprints();
    }

    public static boolean isPermissionGranted(Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            return ActivityCompat.checkSelfPermission(context,Manifest.permission.USE_BIOMETRIC) == PackageManager.PERMISSION_GRANTED;
        }
        else {
            return ActivityCompat.checkSelfPermission(context,Manifest.permission.USE_FINGERPRINT) == PackageManager.PERMISSION_GRANTED;
        }
    }

    public static void askPermissionForFingerprint(Activity activity){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.USE_BIOMETRIC}, GlobalStaticVariables.FINGERPRINT_REQUEST_CODE);
        }
        else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.USE_FINGERPRINT}, GlobalStaticVariables.FINGERPRINT_REQUEST_CODE);
        }
    }

    @TargetApi(Build.VERSION_CODES.P)
    public static void displayBiometricPrompt(Context context, final BiometricCallBack biometricCallback) {
        new BiometricPrompt.Builder(context)
                .setTitle("Fingerprint Setup")
                .setSubtitle("Login to Parental Lock App")
                .setDescription("Place your finger on the sensor")
                .setNegativeButton("Cancel", context.getMainExecutor(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        biometricCallback.onAuthenticationError(BiometricPrompt.BIOMETRIC_ERROR_CANCELED,"cancelled");
                    }
                })
                .build();
    }
}

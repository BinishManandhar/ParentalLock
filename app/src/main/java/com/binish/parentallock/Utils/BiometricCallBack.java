package com.binish.parentallock.Utils;

import android.content.Context;
import android.hardware.biometrics.BiometricPrompt;
import android.hardware.biometrics.BiometricPrompt.AuthenticationCallback;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

@RequiresApi(api = Build.VERSION_CODES.P)
public class BiometricCallBack extends AuthenticationCallback {
    Context context;
    public BiometricCallBack(Context context){
        this.context = context;
    }
    @Override
    public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {

    }


    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
        Toast.makeText(context,helpString,Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {
        Toast.makeText(context,errString,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAuthenticationFailed() {
        Toast.makeText(context,"Failed",Toast.LENGTH_SHORT).show();
    }
}

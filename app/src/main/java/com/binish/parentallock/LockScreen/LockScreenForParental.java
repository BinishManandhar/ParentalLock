package com.binish.parentallock.LockScreen;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Build;
import android.os.CancellationSignal;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.binish.parentallock.R;
import com.binish.parentallock.Utils.BiometricUtils;
import com.binish.parentallock.Utils.PasswordGeneration;
import com.binish.parentallock.Utils.UsefulFunctions;

import java.util.Objects;

public class LockScreenForParental extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_lock_screen_for_parental);

        ImageView lockAppIcon = findViewById(R.id.lockAppIcon);
        final EditText lockInput = findViewById(R.id.lockInput);

        lockAppIcon.setImageResource(R.drawable.ic_launcher_foreground);

        lockInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String enteredPassword = lockInput.getText().toString();
                    if (PasswordGeneration.getSecurePassword(enteredPassword)
                            .equals(UsefulFunctions.getUniversalPassword(LockScreenForParental.this))
                            || enteredPassword.equals(UsefulFunctions.getIndividualPassword(LockScreenForParental.this, getPackageName()))) {
                        entrySuccessful();
                    }
                    return true;
                }
                return false;
            }
        });

        FloatingActionButton fingerprint = findViewById(R.id.fab_fingerprint);
        if (!BiometricUtils.isHardwareSupported(this)
                || !UsefulFunctions.checkUniversalFingerprintStatus(this))
            fingerprint.hide();
        else
            fingerprint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    displayFingerprintAccordingly(LockScreenForParental.this, LockScreenForParental.this);
                }
            });

    }

    //*************************BIO-METRICS******************************************//
    private void displayFingerprintAccordingly(Context context, Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if (BiometricUtils.isPermissionGranted(context)) {
                if (BiometricUtils.isFingerprintAvailable(context))
                    displayBiometricPrompt(context, getAuthenticationCall());
            } else {
                BiometricUtils.askPermissionForFingerprint(activity);
            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private void displayBiometricPrompt(Context context, final BiometricPrompt.AuthenticationCallback biometricCallback) {
        BiometricPrompt bm = new BiometricPrompt.Builder(context)
                .setTitle("Parental Lock")
                .setSubtitle("To login to your App")
                .setDescription("Place your finger on the sensor")
                .setNegativeButton("Cancel", context.getMainExecutor(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).build();
        bm.authenticate(new CancellationSignal(), context.getMainExecutor(), getAuthenticationCall());
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private BiometricPrompt.AuthenticationCallback getAuthenticationCall() {
        return new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                Toast.makeText(LockScreenForParental.this, errString, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                Toast.makeText(LockScreenForParental.this, helpString, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                entrySuccessful();
            }

            @Override
            public void onAuthenticationFailed() {
                Toast.makeText(LockScreenForParental.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
            }
        };
    }

    //*************************BIO-METRICS******************************************//

    //*************************Successful*******************************************//
    private void entrySuccessful(){
        finish();
    }
}

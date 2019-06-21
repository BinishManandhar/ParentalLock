package com.binish.parentallock.LockScreen;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;
import com.binish.parentallock.R;
import com.binish.parentallock.Utils.BiometricUtils;
import com.binish.parentallock.Utils.GlobalStaticVariables;
import com.binish.parentallock.Utils.PasswordGeneration;
import com.binish.parentallock.Utils.UsefulFunctions;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.Lock;

public class LockScreen extends AppCompatActivity {
    String packageName;
    FloatingActionButton fingerprint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_lock_screen);
        Objects.requireNonNull(getSupportActionBar()).hide();

        String appName = getIntent().getStringExtra("appName");
        packageName = getIntent().getStringExtra("packageName");
        int appColor = getIntent().getIntExtra("appColor", Color.WHITE);
        byte[] b = getIntent().getByteArrayExtra("appIcon");
        Bitmap appIcon = BitmapFactory.decodeByteArray(b, 0, b.length);

        TextView lockAppName = findViewById(R.id.lockAppName);
        ImageView lockAppIcon = findViewById(R.id.lockAppIcon);
        final EditText lockInput = findViewById(R.id.lockInput);


        findViewById(R.id.lockScreen).setBackgroundColor(appColor);
        lockAppName.setText(appName);
        lockAppIcon.setImageBitmap(appIcon);

        lockInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String enteredPassword = lockInput.getText().toString();
                    if (PasswordGeneration.getSecurePassword(enteredPassword)
                            .equals(UsefulFunctions.getUniversalPassword(LockScreen.this))
                            || enteredPassword.equals(UsefulFunctions.getIndividualPassword(LockScreen.this, packageName))) {
                        entrySuccessful();
                    }
                    return true;
                }
                return false;
            }
        });

        fingerprint = findViewById(R.id.fab_fingerprint);
        if (!BiometricUtils.isHardwareSupported(this)
                || !UsefulFunctions.checkFingerprintLockExist(this,packageName))
            fingerprint.hide();
        else
            fingerprint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    displayFingerprintAccordingly(LockScreen.this, LockScreen.this);
                }
            });


        //*******************************Pattern Lock*************************************************************//
        CardView patternCardView = findViewById(R.id.pattern_cardView);
        if(!UsefulFunctions.getIndividualPassword(this,packageName).equals("")) {
            if (UsefulFunctions.checkIndividualPasswordType(this, packageName).equals(GlobalStaticVariables.PASSWORDTYPE_TEXT))
                patternCardView.setVisibility(View.INVISIBLE);
            else
                lockInput.setVisibility(View.INVISIBLE);
        }
        else {
            if (UsefulFunctions.checkUniversalPasswordType(this).equals(GlobalStaticVariables.PASSWORDTYPE_TEXT))
                patternCardView.setVisibility(View.INVISIBLE);
            else
                lockInput.setVisibility(View.INVISIBLE);
        }

        final PatternLockView patternLockView = findViewById(R.id.pattern_lockView);
        patternLockView.setTactileFeedbackEnabled(true);
        patternLockView.addPatternLockListener(new PatternLockViewListener() {
            @Override
            public void onStarted() {

            }

            @Override
            public void onProgress(List<PatternLockView.Dot> progressPattern) {

            }

            @Override
            public void onComplete(List<PatternLockView.Dot> pattern) {
                String enteredPattern = PasswordGeneration.getSecurePassword(PatternLockUtils.patternToMD5(patternLockView,pattern));
                if(UsefulFunctions.getUniversalPassword(LockScreen.this).equals(enteredPattern)
                        || UsefulFunctions.getIndividualPassword(LockScreen.this,packageName).equals(PatternLockUtils.patternToMD5(patternLockView,pattern))) {
                    patternLockView.setViewMode(PatternLockView.PatternViewMode.CORRECT);
                    entrySuccessful();
                }
                else {
                    patternLockView.setViewMode(PatternLockView.PatternViewMode.WRONG);
                    patternLockView.clearPattern();
                }
            }

            @Override
            public void onCleared() {

            }
        });
        //*******************************Pattern Lock*************************************************************//
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
                Toast.makeText(LockScreen.this, errString, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                Toast.makeText(LockScreen.this, helpString, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                entrySuccessful();
            }

            @Override
            public void onAuthenticationFailed() {
                Toast.makeText(LockScreen.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
            }
        };
    }

    //*************************BIO-METRICS******************************************//

    //*************************Successful*******************************************//
    private void entrySuccessful(){
        UsefulFunctions.changePassCheck(LockScreen.this, false, packageName);
        finishAndRemoveTask();
        Intent intent = new Intent("finish_activity");
        intent.putExtra("where", "lockScreen");
        sendBroadcast(intent);
    }
    //******************************************************************************//

    @Override
    protected void onPause() {
        super.onPause();
        this.finishAndRemoveTask();
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.finishAndRemoveTask();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

package com.binish.parentallock.Activities;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;
import com.binish.parentallock.Database.DatabaseHelper;
import com.binish.parentallock.R;
import com.binish.parentallock.Utils.BiometricUtils;
import com.binish.parentallock.Utils.GlobalStaticVariables;
import com.binish.parentallock.Utils.PasswordGeneration;
import com.binish.parentallock.Utils.UsefulFunctions;

import java.util.List;

public class PasswordPage extends AppCompatActivity implements OnClickListener {
    EditText newPassword;
    EditText confirmPassword;
    ImageView passwordSet;
    NumberPicker passwordType;
    CheckBox checkBox;
    CardView passwordPattern;
    CardView passwordText;
    DatabaseHelper databaseHelper;
    PatternLockView patternLockView;
    TextView patternDescription;
    String nPattern="";
    String cPattern;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_page2);
        databaseHelper = new DatabaseHelper(this);
        FloatingActionButton backButton = findViewById(R.id.password_back);
        FloatingActionButton changeButton = findViewById(R.id.password_change);
        FloatingActionButton patternBackButton = findViewById(R.id.password_pattern_back);
        patternBackButton.setOnClickListener(this);
        passwordType = findViewById(R.id.password_type_picker);
        newPassword = findViewById(R.id.new_password);
        confirmPassword = findViewById(R.id.confirm_password);
        passwordSet = findViewById(R.id.password_set);
        checkBox = findViewById(R.id.default_fingerprint);
        passwordPattern = findViewById(R.id.password_pattern_cardView);
        passwordText = findViewById(R.id.password_text_cardView);
        patternLockView = findViewById(R.id.password_page_pattern);
        patternDescription = findViewById(R.id.pattern_description);

        if (UsefulFunctions.checkUniversalPasswordExist(this))
            passwordSet.setImageResource(R.drawable.ic_done_green_24dp);
        if(!BiometricUtils.isHardwareSupported(this)) {
            CardView fingerprintEnabled = findViewById(R.id.fingerprint_enabled);
            fingerprintEnabled.setVisibility(View.GONE);
        }
        checkBox.setChecked(databaseHelper.getFingerprintStatus());
        backButton.setOnClickListener(this);
        changeButton.setOnClickListener(this);
        checkBox.setOnClickListener(this);

        passwordType.setMinValue(0);
        passwordType.setMaxValue(1);
        passwordType.setDisplayedValues(new String[]{"Text","Pattern"});
        if(databaseHelper.getUniversalPasswordType().equals(GlobalStaticVariables.PASSWORDTYPE_PATTERN)) {
            passwordType.setValue(1);
            passwordText.setVisibility(View.GONE);
        }
        else
            passwordPattern.setVisibility(View.GONE);

        passwordType.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                if(newVal==1) {
                    passwordText.setVisibility(View.GONE);
                    passwordPattern.setVisibility(View.VISIBLE);
                }else {
                    passwordText.setVisibility(View.VISIBLE);
                    passwordPattern.setVisibility(View.GONE);
                }
            }
        });


        patternLockView.setTactileFeedbackEnabled(true);
        patternLockView.addPatternLockListener(new PatternLockViewListener() {
            @Override
            public void onStarted() {
                if(nPattern.equals(""))
                    patternDescription.setText("New Pattern");
            }

            @Override
            public void onProgress(List<PatternLockView.Dot> progressPattern) {

            }

            @Override
            public void onComplete(List<PatternLockView.Dot> pattern) {
                if(nPattern.equals("")) {
                    nPattern = PatternLockUtils.patternToMD5(patternLockView, pattern);
                    patternLockView.setViewMode(PatternLockView.PatternViewMode.CORRECT);
                    patternLockView.clearPattern();
                    patternDescription.setText("Confirm Pattern");
                }
                else {
                    cPattern = PatternLockUtils.patternToMD5(patternLockView, pattern);
                    if(nPattern.equals(cPattern)){
                        databaseHelper.insertPassword(nPattern,checkBox.isChecked(),passwordType.getValue());
                        patternLockView.setViewMode(PatternLockView.PatternViewMode.CORRECT);
                        delayMovingBack();
                        patternDescription.setText("Pattern Confirmed");
                    }
                    else {
                        patternLockView.setViewMode(PatternLockView.PatternViewMode.WRONG);
                        patternDescription.setText("Incorrect! Try again");
                        nPattern="";
                    }
                }
            }

            @Override
            public void onCleared() {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.password_back:
                if (checkForMovingBack(v))
                    onBackPressed();
                break;
            case R.id.password_change:
                String nPassword = newPassword.getText().toString();
                String cPassword = confirmPassword.getText().toString();
                if (nPassword.equals("") || cPassword.equals("")) {
                    Snackbar.make(v, "Password cannot be empty", Snackbar.LENGTH_SHORT).show();
                } else if (PasswordGeneration.getSecurePassword(nPassword).equals(PasswordGeneration.getSecurePassword(cPassword))) {
                    passwordSet.setImageResource(R.drawable.ic_done_green_24dp);
                    Snackbar.make(v, "Password Stored:", Snackbar.LENGTH_SHORT).show();

                    databaseHelper.insertPassword(nPassword,checkBox.isChecked(),passwordType.getValue());
                    delayMovingBack();
                } else {
                    Snackbar.make(v, "Passwords don't match", Snackbar.LENGTH_SHORT).show();
                }
                UsefulFunctions.hideKeyboard(this);
                break;
            case R.id.default_fingerprint:
                if (checkBox.isChecked())
                    databaseHelper.changeFingerprintStatus(checkBox.isChecked());
                else
                    databaseHelper.changeFingerprintStatus(checkBox.isChecked());
                break;
            case R.id.password_pattern_back:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (checkForMovingBack(findViewById(android.R.id.content)))
            super.onBackPressed();
    }

    public boolean checkForMovingBack(View v) {
        if (!UsefulFunctions.checkUniversalPasswordExist(this)) {
            Snackbar.make(v, "Setting password is mandatory", Snackbar.LENGTH_LONG).show();
            return false;
        } else {
            return true;
        }
    }

    private void delayMovingBack() {
        new CountDownTimer(1500, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                onBackPressed();
            }
        }.start();
    }
}

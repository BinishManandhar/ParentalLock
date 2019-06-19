package com.binish.parentallock.Activities;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import com.binish.parentallock.Database.DatabaseHelper;
import com.binish.parentallock.R;
import com.binish.parentallock.Utils.PasswordGeneration;
import com.binish.parentallock.Utils.UsefulFunctions;

public class PasswordPage extends AppCompatActivity implements OnClickListener {
    EditText newPassword;
    EditText confirmPassword;
    ImageView passwordSet;
    CheckBox checkBox;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_page2);
        databaseHelper = new DatabaseHelper(this);
        FloatingActionButton backButton = findViewById(R.id.password_back);
        FloatingActionButton changeButton = findViewById(R.id.password_change);
        newPassword = findViewById(R.id.new_password);
        confirmPassword = findViewById(R.id.confirm_password);
        passwordSet = findViewById(R.id.password_set);
        checkBox = findViewById(R.id.default_fingerprint);
        if (UsefulFunctions.checkUniversalPasswordExist(this))
            passwordSet.setImageResource(R.drawable.ic_done_green_24dp);
        checkBox.setChecked(databaseHelper.getFingerprintStatus());
        backButton.setOnClickListener(this);
        changeButton.setOnClickListener(this);
        checkBox.setOnClickListener(this);
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

                    databaseHelper.insertPassword(nPassword,checkBox.isChecked());
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

package com.binish.parentallock.LockScreen;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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

import com.binish.parentallock.MainActivity;
import com.binish.parentallock.R;
import com.binish.parentallock.Services.Service;
import com.binish.parentallock.Utils.UsefulFunctions;

import java.util.Objects;

public class LockScreen extends AppCompatActivity {
    Intent mIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_lock_screen);
        Objects.requireNonNull(getSupportActionBar()).hide();

        String appName = getIntent().getStringExtra("appName");
        final String packageName = getIntent().getStringExtra("packageName");
        int appColor = getIntent().getIntExtra("appColor", Color.WHITE);
        byte[] b = getIntent().getByteArrayExtra("appIcon");
        Bitmap appIcon = BitmapFactory.decodeByteArray(b,0,b.length);

        TextView lockAppName = findViewById(R.id.lockAppName);
        ImageView lockAppIcon = findViewById(R.id.lockAppIcon);
        final EditText lockInput = findViewById(R.id.lockInput);

        findViewById(R.id.lockScreen).setBackgroundColor(appColor);
        lockAppName.setText(appName);
        lockAppIcon.setImageBitmap(appIcon);

        lockInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    if(lockInput.getText().toString().equals("password")) {
                        UsefulFunctions.changePassCheck(LockScreen.this,false,packageName);
                        finishAndRemoveTask();
                    }
                    return true;
                }
                return false;
            }
        });

        mIntent = new Intent(this,Service.class);
        if(!UsefulFunctions.isMyServiceRunning(this,mIntent.getClass())){
            startService(mIntent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(UsefulFunctions.isMyServiceRunning(this,mIntent.getClass())){
            stopService(mIntent);
        }
    }
}

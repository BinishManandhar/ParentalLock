package com.binish.parentallock.Activities;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.binish.parentallock.Database.DatabaseHelper;
import com.binish.parentallock.R;
import com.binish.parentallock.Utils.GlobalStaticVariables;
import com.binish.parentallock.Utils.UsefulFunctions;

public class ActivityCreateProfile extends AppCompatActivity implements View.OnClickListener {
    DatabaseHelper databaseHelper;
    String profileName="", unlockFrom, unlockTo;
    boolean isActive;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

        databaseHelper = new DatabaseHelper(this);

        Button fromSet = findViewById(R.id.unlock_from);
        Button toSet = findViewById(R.id.unlock_to);
        FloatingActionButton fab = findViewById(R.id.createProfileFab);
        FloatingActionButton fabCancel = findViewById(R.id.createProfileFabCancel);



        fabCancel.setOnClickListener(this);
        fab.setOnClickListener(this);
        fromSet.setOnClickListener(this);
        toSet.setOnClickListener(this);

    }

    @Override
    public void onBackPressed() {
        setResult(ActivityCreateProfile.RESULT_OK);
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.createProfileFab:
                EditText editProfileName = findViewById(R.id.createProfile_Name);
                Button fromSet = findViewById(R.id.unlock_from);
                Button toSet = findViewById(R.id.unlock_to);
                profileName = editProfileName.getText().toString();
                unlockFrom = fromSet.getText().toString();
                unlockTo = toSet.getText().toString();
                isActive = false;
                if (!profileName.equals("") && !unlockFrom.equalsIgnoreCase("SET") && !unlockTo.equalsIgnoreCase("SET")) {
                    Snackbar.make(v, "Profile Created", Snackbar.LENGTH_LONG)
                            .setAction("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    onBackPressed();
                                }
                            }).show();
                    databaseHelper.insertProfile(profileName, unlockFrom, unlockTo, isActive);
                    FloatingActionButton fabBack = findViewById(R.id.createProfileFabCancel);
                    fabBack.setImageResource(R.drawable.ic_arrow_back_white_24dp);
                }
                else
                    Snackbar.make(v,"Please fill all of the fields",Snackbar.LENGTH_SHORT).show();
                break;
            case R.id.createProfileFabCancel:
                onBackPressed();
                break;
            case R.id.unlock_from:
                UsefulFunctions.showTimeDialog(this, v, v.getId());
                break;
            case R.id.unlock_to:
                UsefulFunctions.showTimeDialog(this, v, v.getId());
                break;

        }
    }
}

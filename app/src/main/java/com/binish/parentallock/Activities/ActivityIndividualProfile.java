package com.binish.parentallock.Activities;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.binish.parentallock.Database.DatabaseHelper;
import com.binish.parentallock.Models.ProfileModel;
import com.binish.parentallock.R;
import com.binish.parentallock.Utils.UsefulFunctions;

import java.util.Objects;

public class ActivityIndividualProfile extends AppCompatActivity implements View.OnClickListener {
    DatabaseHelper databaseHelper;
    ProfileModel profileModel;
    EditText editProfileName;
    Button unlockFrom;
    Button unlockTo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);
        Objects.requireNonNull(getSupportActionBar()).hide();
        editProfileName = findViewById(R.id.createProfile_Name);
        unlockFrom = findViewById(R.id.unlock_from);
        unlockTo = findViewById(R.id.unlock_to);
        FloatingActionButton fabUpdate = findViewById(R.id.createProfileFab);
        FloatingActionButton fabCancel = findViewById(R.id.createProfileFabCancel);

        String profileName = getIntent().getStringExtra("ProfileName");

        databaseHelper = new DatabaseHelper(this);
        profileModel = databaseHelper.getIndividualProfile(profileName);

        editProfileName.setText(profileModel.getProfileName());
        unlockFrom.setText(profileModel.getUnlockFrom());
        unlockTo.setText(profileModel.getUnlockTo());
        fabUpdate.setOnClickListener(this);
        fabCancel.setOnClickListener(this);
        unlockFrom.setOnClickListener(this);
        unlockTo.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.createProfileFab:
                databaseHelper.updateProfile(editProfileName.getText().toString(),unlockFrom.getText().toString(),unlockTo.getText().toString(),profileModel.isActive());
                Snackbar.make(v,"Profile Updated",Snackbar.LENGTH_LONG)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onBackPressed();
                            }
                        }).show();
                FloatingActionButton fabBack = findViewById(R.id.createProfileFabCancel);
                fabBack.setImageResource(R.drawable.ic_arrow_back_white_24dp);
                break;
            case R.id.createProfileFabCancel:
                onBackPressed();
                break;
            case R.id.unlock_from:
                UsefulFunctions.showTimeDialog(this,v,v.getId());
                break;
            case R.id.unlock_to:
                UsefulFunctions.showTimeDialog(this,v,v.getId());
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

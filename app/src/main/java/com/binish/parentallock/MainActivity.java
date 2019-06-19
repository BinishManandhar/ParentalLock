package com.binish.parentallock;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.binish.parentallock.Activities.PasswordPage;
import com.binish.parentallock.Fragments.AppListFragment;
import com.binish.parentallock.Fragments.PasswordListFragment;
import com.binish.parentallock.Fragments.ProfilesListFragment;
import com.binish.parentallock.Policy.PolicyManager;
import com.binish.parentallock.Utils.GlobalStaticVariables;
import com.binish.parentallock.Utils.UsefulFunctions;
import com.binish.parentallock.Worker.ParentalWorker;
import com.binish.parentallock.services.Service;

import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import static com.binish.parentallock.Utils.UsefulFunctions.JOB_ID;
import static com.binish.parentallock.Utils.UsefulFunctions.checkUniversalPasswordExist;
import static com.binish.parentallock.Utils.UsefulFunctions.isJobServiceOn;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Intent mIntent;
    String where = "main";
    BroadcastReceiver broadcastReceiver;
    PolicyManager policyManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        policyManager = new PolicyManager(this);

        //***Important***//
        createFinishReceiver();
        //***************//

        if (!UsefulFunctions.usageAccessCheck(this)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission Required")
                    .setMessage("Usage Stats Permission is Required")
                    .setPositiveButton("Go to Settings", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //---------------------//
                            UsefulFunctions.usageAccessSettingsPage(MainActivity.this);

                            //---------------------//
                            changeFragmentTo(new AppListFragment());
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(MainActivity.this, "Permission is required", Toast.LENGTH_LONG).show();
                        }
                    }).show();
        } else {
            changeFragmentTo(new AppListFragment());

        }

        if(!checkUniversalPasswordExist(this)){
            Intent intent = new Intent(this,PasswordPage.class);
            startActivity(intent);
        }

        if (!policyManager.isAdminActive()) {
            new AlertDialog.Builder(this)
                    .setTitle("Admin access")
                    .setMessage("Grant Admin access")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent activateDeviceAdmin = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                            activateDeviceAdmin.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, policyManager.getAdminComponent());
                            activateDeviceAdmin.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Activate to let unwanted un-installation of this app");
                            startActivityForResult(activateDeviceAdmin, PolicyManager.DPM_ACTIVATION_REQUEST_CODE);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(MainActivity.this, "Admin Rights is required from unwanted un-installation of this app", Toast.LENGTH_LONG).show();
                        }
                    }).show();
        }

        //Starting the service
        startService();
        if(!isJobServiceOn(this))
            UsefulFunctions.startJobService(this);
        else {
            UsefulFunctions.cancelJobService(this,JOB_ID);
        }
//        initiateWorker();
        //-------------------//


        UsefulFunctions.appListDataFunction(this);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(0).setChecked(true);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == PolicyManager.DPM_ACTIVATION_REQUEST_CODE) {
            Toast.makeText(this, "Admin Rights Granted", Toast.LENGTH_LONG).show();
        } else if(requestCode == PolicyManager.DPM_ACTIVATION_REQUEST_CODE) {
            Toast.makeText(this, "Admin Rights is required from unwanted un-installation of this app", Toast.LENGTH_LONG).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_uninstall) {
            if (policyManager.isAdminActive()) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Uninstallation Rights")
                        .setMessage("Are you sure you want to unlock uninstallation rights ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                policyManager.disableAdmin();
                                Intent intent = new Intent(Intent.ACTION_DELETE);
                                intent.setData(Uri.parse("package:"+getPackageName()));
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();
            }
            else {
                Intent intent = new Intent(Intent.ACTION_DELETE);
                intent.setData(Uri.parse("package:"+getPackageName()));
                startActivity(intent);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profiles) {
            changeFragmentTo(new ProfilesListFragment());
        } else if (id == R.id.nav_apps) {
            changeFragmentTo(new AppListFragment());
        } else if (id == R.id.nav_manage) {
            changeFragmentTo(new PasswordListFragment());
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    @Override
    protected void onDestroy() {
        try {
            if (!where.equals("lockScreen"))
                stopService(mIntent);
        } catch (Exception e) {
            Log.i("MainActivityException", "" + e);
        }
//        initiateWorker();
        super.onDestroy();
    }

    private void initiateWorker(){
        /*PeriodicWorkRequest.Builder parentalWork = new PeriodicWorkRequest.Builder(ParentalWorker.class,60,TimeUnit.MILLISECONDS);
        PeriodicWorkRequest work = parentalWork.build();*/
        new Thread(){
            @Override
            public void run() {
                OneTimeWorkRequest work =
                        new OneTimeWorkRequest.Builder(ParentalWorker.class)
                                .build();
                WorkManager.getInstance().enqueueUniqueWork("ParentalCheck",ExistingWorkPolicy.REPLACE,work);
                super.run();
            }
        }.start();
    }




    private void startService(){
        mIntent = new Intent(this, Service.class);
        if (!UsefulFunctions.isMyServiceRunning(this, Service.class)) {
            Log.i("LockScreenLog", "Starting Service");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startService(mIntent);
//                bindService(mIntent,connection,BIND_AUTO_CREATE);
            }
            else {
                startService(mIntent);
//                bindService(mIntent,connection,BIND_AUTO_CREATE);
            }
        }
    }

    //*********************Finishing the activity************************//
    private void createFinishReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                assert action != null;
                if (action.equals("finish_activity")) {
                    where = getIntent().getStringExtra("where");
                    MainActivity.this.finish();
                }
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter("finish_activity"));

        //----------ScreenOff Detection---------------------------------//
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_USER_PRESENT);

        BroadcastReceiver broadcastReceiver1 = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(!UsefulFunctions.getForegroundApp(context).equals("com.binish.parentallock")) {
                    MainActivity.this.finishAndRemoveTask();
                    finishActivity(GlobalStaticVariables.profileFragmentRefresh);
                }
            }
        };
        registerReceiver(broadcastReceiver1,intentFilter);
        //---------------------------------------------------------------//
    }//*********************Finishing the activity************************//

    private void changeFragmentTo(Fragment fragment){
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentAppList,fragment).commit();
    }
}

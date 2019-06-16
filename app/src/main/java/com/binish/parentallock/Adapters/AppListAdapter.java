package com.binish.parentallock.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.binish.parentallock.Database.DatabaseHelper;
import com.binish.parentallock.Models.LockUnlockModel;
import com.binish.parentallock.Models.ProfileModel;
import com.binish.parentallock.R;

import java.util.List;

public class AppListAdapter extends RecyclerView.Adapter<ViewHolder> {
    private Context context;
    private List<LockUnlockModel> applicationInfoList;
    private DatabaseHelper databaseHelper;
    private NumberPicker numberPicker;

    public AppListAdapter(Context context, List<LockUnlockModel> applicationInfoList) {
        this.context = context;
        this.applicationInfoList = applicationInfoList;
        databaseHelper = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.design_app_list, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        final ApplicationInfo applicationInfo = applicationInfoList.get(i).getApplicationInfo();
        PackageManager packageManager = context.getPackageManager();
        viewHolder.appName.setText(packageManager.getApplicationLabel(applicationInfo));
        viewHolder.appIcon.setImageDrawable(packageManager.getApplicationIcon(applicationInfo));
        viewHolder.lockUnlock.setImageResource(applicationInfoList.get(i).getDrawableInt());
        viewHolder.appProfileName.setText(applicationInfoList.get(i).getLockUnlockProfile());


        viewHolder.lockUnlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!applicationInfo.packageName.equals("com.binish.parentallock")) {
                    lockUnlockToggle(applicationInfo.packageName);

                    if (applicationInfoList.get(viewHolder.getAdapterPosition()).getDrawableInt() == R.drawable.ic_lock_red_24dp)
                        applicationInfoList.get(viewHolder.getAdapterPosition()).setDrawableInt(R.drawable.ic_lock_open_green_24dp);
                    else
                        applicationInfoList.get(viewHolder.getAdapterPosition()).setDrawableInt(R.drawable.ic_lock_red_24dp);

                    changeProfileStatusDefault(viewHolder);
                    notifyItemChanged(viewHolder.getAdapterPosition());
                } else {
                    Toast.makeText(context, "Cannot lock this app", Toast.LENGTH_SHORT).show();
                }
            }
        });


        viewHolder.appNameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (applicationInfoList.get(viewHolder.getAdapterPosition()).getDrawableInt() == R.drawable.ic_lock_red_24dp) {
                    View view = LayoutInflater.from(context).inflate(R.layout.dialog_select_profile, null);
                    numberPicker = view.findViewById(R.id.profile_picker);
                    final List<ProfileModel> list = databaseHelper.getAllProfiles();
                    if (list.size() != 0) {
                        numberPicker.setMinValue(0);
                        numberPicker.setMaxValue(list.size() - 1);
                        String[] s = new String[list.size()];
                        for (int i = 0; i < list.size(); i++) {
                            s[i] = list.get(i).getProfileName();
                        }
                        numberPicker.setDisplayedValues(s);
                    } else {
                        numberPicker.setMinValue(0);
                        numberPicker.setMaxValue(0);
                        numberPicker.setDisplayedValues(new String[]{"None"});
                    }
                    new AlertDialog.Builder(context)
                            .setTitle("Select a profile")
                            .setIcon(R.drawable.ic_person_red_24dp)
                            .setView(view)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    databaseHelper.updateLockUnlockProfile
                                            (applicationInfo.packageName, list.get(numberPicker.getValue()).getProfileName());
                                    applicationInfoList.get(viewHolder.getAdapterPosition())
                                            .setLockUnlockProfile(list.get(numberPicker.getValue()).getProfileName());
                                    notifyItemChanged(viewHolder.getAdapterPosition());
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                } else
                    Toast.makeText(context, "Lock the app to set a profile", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void lockUnlockToggle(String packageName) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        if (databaseHelper.checkLockUnlock(packageName))
            databaseHelper.insertLockUnlock(packageName, false);
        else
            databaseHelper.insertLockUnlock(packageName, true);
    }

    private void changeProfileStatusDefault(ViewHolder viewHolder){
        applicationInfoList.get(viewHolder.getAdapterPosition())
                .setLockUnlockProfile("");
    }


    @Override
    public int getItemCount() {
        return applicationInfoList.size();
    }

}

class ViewHolder extends RecyclerView.ViewHolder {
    ImageView appIcon, lockUnlock;
    TextView appName;
    TextView appProfileName;
    LinearLayout appNameLayout;

    ViewHolder(@NonNull View itemView) {
        super(itemView);
        appIcon = itemView.findViewById(R.id.appIcon);
        appName = itemView.findViewById(R.id.appName);
        lockUnlock = itemView.findViewById(R.id.lockUnlock);
        appNameLayout = itemView.findViewById(R.id.app_name_layout);
        appProfileName = itemView.findViewById(R.id.app_profile_name);
    }
}



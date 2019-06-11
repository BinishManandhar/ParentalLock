package com.binish.parentallock.Adapters;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.binish.parentallock.Database.DatabaseHelper;
import com.binish.parentallock.Models.LockUnlockModel;
import com.binish.parentallock.R;

import java.util.List;

public class AppListAdapter extends RecyclerView.Adapter<ViewHolder> {
    private Context context;
    private List<LockUnlockModel> applicationInfoList;

    public AppListAdapter(Context context, List<LockUnlockModel> applicationInfoList) {
        this.context = context;
        this.applicationInfoList = applicationInfoList;
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

        viewHolder.lockUnlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!applicationInfo.packageName.equals("com.binish.parentallock")) {
                    lockUnlockToggle(applicationInfo.packageName);

                    if (applicationInfoList.get(viewHolder.getAdapterPosition()).getDrawableInt() == R.drawable.ic_lock_red_24dp)
                        applicationInfoList.get(viewHolder.getAdapterPosition()).setDrawableInt(R.drawable.ic_lock_open_green_24dp);
                    else
                        applicationInfoList.get(viewHolder.getAdapterPosition()).setDrawableInt(R.drawable.ic_lock_red_24dp);

                    notifyItemChanged(viewHolder.getAdapterPosition());
                }
                else {
                    Toast.makeText(context,"Cannot lock this app",Toast.LENGTH_SHORT).show();
                }
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


    @Override
    public int getItemCount() {
        return applicationInfoList.size();
    }

}

class ViewHolder extends RecyclerView.ViewHolder {
    ImageView appIcon, lockUnlock;
    TextView appName;

    ViewHolder(@NonNull View itemView) {
        super(itemView);
        appIcon = itemView.findViewById(R.id.appIcon);
        appName = itemView.findViewById(R.id.appName);
        lockUnlock = itemView.findViewById(R.id.lockUnlock);
    }
}



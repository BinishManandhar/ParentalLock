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

import com.binish.parentallock.R;

import java.util.List;

public class AppListAdapter extends RecyclerView.Adapter<ViewHolder> {
    private Context context;
    private List<ApplicationInfo> applicationInfoList;

    public AppListAdapter(Context context, List<ApplicationInfo> applicationInfoList) {
        this.context = context;
        this.applicationInfoList = applicationInfoList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.design_app_list, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        ApplicationInfo applicationInfo = applicationInfoList.get(i);
        PackageManager packageManager = context.getPackageManager();
        viewHolder.appName.setText(packageManager.getApplicationLabel(applicationInfo));
        viewHolder.appIcon.setImageDrawable(packageManager.getApplicationIcon(applicationInfo));


    }

    @Override
    public int getItemCount() {
        return applicationInfoList.size();
    }
}

class ViewHolder extends RecyclerView.ViewHolder {
    ImageView appIcon;
    TextView appName;

    ViewHolder(@NonNull View itemView) {
        super(itemView);
        appIcon = itemView.findViewById(R.id.appIcon);
        appName = itemView.findViewById(R.id.appName);
    }
}

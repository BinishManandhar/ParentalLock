package com.binish.parentallock.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.binish.parentallock.Database.DatabaseHelper;
import com.binish.parentallock.Models.LockUnlockModel;
import com.binish.parentallock.R;

import java.util.List;

public class PasswordListAdapter extends RecyclerView.Adapter<PasswordViewHolder> {
    Context context;
    List<LockUnlockModel> applicationList;

    public PasswordListAdapter(Context context, List<LockUnlockModel> applicationList) {
        this.context = context;
        this.applicationList = applicationList;
    }

    @NonNull
    @Override
    public PasswordViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new PasswordViewHolder(LayoutInflater.from(context).inflate(R.layout.design_password_list, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final PasswordViewHolder passwordViewHolder, int i) {
        final LockUnlockModel lockUnlockModel = applicationList.get(i);
        PackageManager packageManager = context.getPackageManager();
        final String packageName = lockUnlockModel.getApplicationInfo().packageName;
        final String appName = packageManager.getApplicationLabel(lockUnlockModel.getApplicationInfo()).toString();
        passwordViewHolder.appName.setText(appName);
        passwordViewHolder.appIcon.setImageDrawable(packageManager.getApplicationIcon(lockUnlockModel.getApplicationInfo()));
        passwordViewHolder.passwordCheck.setImageResource(lockUnlockModel.getLockUnlockPasswordDrawable());
        passwordViewHolder.fingerprintCheck.setImageResource(lockUnlockModel.getLockUnlockFingerprintDrawable());
        passwordViewHolder.passwordCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText editText = new EditText(context);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.setMargins(5, 5, 5, 5);
                editText.setLayoutParams(lp);
                editText.setTextSize(15);
                editText.requestFocus();
                editText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                editText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                AlertDialog.Builder alertBox = new AlertDialog.Builder(context);
                alertBox.setView(editText)
                        .setIcon(R.drawable.ic_vpn_key_red_24dp)
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                if (lockUnlockModel.getLockUnlockPasswordDrawable() == R.drawable.ic_vpn_key_green_24dp)
                    alertBox.setTitle("Set Password for " + appName)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    applicationList.get(passwordViewHolder.getAdapterPosition()).setLockUnlockPasswordDrawable(R.drawable.ic_vpn_key_red_24dp);
                                    new DatabaseHelper(context).updateLockUnlockPassword(packageName, editText.getText().toString());
                                    notifyItemChanged(passwordViewHolder.getAdapterPosition());
                                }
                            }).show();

                else
                    alertBox.setTitle("Change Password for " + appName)
                            .setPositiveButton("Change", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    new DatabaseHelper(context).updateLockUnlockPassword(packageName, editText.getText().toString());
                                    notifyItemChanged(passwordViewHolder.getAdapterPosition());
                                }
                            })
                            .setNeutralButton("Clear Password", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    new DatabaseHelper(context).updateLockUnlockPassword(packageName, "");
                                    applicationList.get(passwordViewHolder.getAdapterPosition()).setLockUnlockPasswordDrawable(R.drawable.ic_vpn_key_green_24dp);
                                    notifyItemChanged(passwordViewHolder.getAdapterPosition());
                                }
                            })
                            .show();


            }
        });
        passwordViewHolder.fingerprintCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lockUnlockModel.getLockUnlockFingerprintDrawable()==R.drawable.ic_fingerprint_green_24dp) {
                    lockUnlockModel.setLockUnlockFingerprintDrawable(R.drawable.ic_fingerprint_red_24dp);
                    new DatabaseHelper(context).setLockUnlockFingerprint(packageName,true);
                    notifyItemChanged(passwordViewHolder.getAdapterPosition());
                }
                else {
                    lockUnlockModel.setLockUnlockFingerprintDrawable(R.drawable.ic_fingerprint_green_24dp);
                    new DatabaseHelper(context).setLockUnlockFingerprint(packageName,false);
                    notifyItemChanged(passwordViewHolder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return applicationList.size();
    }
}

class PasswordViewHolder extends RecyclerView.ViewHolder {
    ImageView appIcon;
    TextView appName;
    ImageView passwordCheck;
    ImageView fingerprintCheck;

    PasswordViewHolder(@NonNull View itemView) {
        super(itemView);
        appName = itemView.findViewById(R.id.appName);
        appIcon = itemView.findViewById(R.id.appIcon);
        passwordCheck = itemView.findViewById(R.id.password_check);
        fingerprintCheck = itemView.findViewById(R.id.fingerprint_check);
    }
}

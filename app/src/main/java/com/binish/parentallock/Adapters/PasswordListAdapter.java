package com.binish.parentallock.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;
import com.binish.parentallock.Database.DatabaseHelper;
import com.binish.parentallock.Models.LockUnlockModel;
import com.binish.parentallock.R;
import com.binish.parentallock.Utils.BiometricUtils;
import com.binish.parentallock.Utils.GlobalStaticVariables;
import com.binish.parentallock.Utils.UsefulFunctions;

import java.util.List;

public class PasswordListAdapter extends RecyclerView.Adapter<PasswordViewHolder> {
    Context context;
    List<LockUnlockModel> applicationList;
    String nPattern="";
    String cPattern;

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
        if (!BiometricUtils.isHardwareSupported(context))
            passwordViewHolder.fingerprintCheck.setVisibility(View.GONE);
        passwordViewHolder.fingerprintCheck.setImageResource(lockUnlockModel.getLockUnlockFingerprintDrawable());

        passwordViewHolder.passwordCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UsefulFunctions.checkIndividualPasswordType(context, packageName).equals(GlobalStaticVariables.PASSWORDTYPE_TEXT)) {
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
                else {
                    patternLockWork(context,passwordViewHolder.getAdapterPosition());

                }
            }
        });

        passwordViewHolder.fingerprintCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lockUnlockModel.getLockUnlockFingerprintDrawable() == R.drawable.ic_fingerprint_green_24dp) {
                    lockUnlockModel.setLockUnlockFingerprintDrawable(R.drawable.ic_fingerprint_red_24dp);
                    new DatabaseHelper(context).setLockUnlockFingerprint(packageName, true);
                    notifyItemChanged(passwordViewHolder.getAdapterPosition());
                } else {
                    lockUnlockModel.setLockUnlockFingerprintDrawable(R.drawable.ic_fingerprint_green_24dp);
                    new DatabaseHelper(context).setLockUnlockFingerprint(packageName, false);
                    notifyItemChanged(passwordViewHolder.getAdapterPosition());
                }
            }
        });


        passwordViewHolder.appNameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = LayoutInflater.from(context).inflate(R.layout.dialog_select_profile, null);
                final NumberPicker numberPicker = view.findViewById(R.id.profile_picker);

                numberPicker.setMinValue(0);
                numberPicker.setMaxValue(1);
                numberPicker.setDisplayedValues(new String[]{"Text", "Pattern"});
                if (UsefulFunctions.checkIndividualPasswordType(context, packageName).equals(GlobalStaticVariables.PASSWORDTYPE_PATTERN)) {
                    numberPicker.setValue(1);
                }
                new AlertDialog.Builder(context)
                        .setTitle("Select password type")
                        .setIcon(R.drawable.ic_vpn_key_red_24dp)
                        .setView(view)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new DatabaseHelper(context).updateLockUnlockPassword(packageName,"");
                                UsefulFunctions.setIndividualPasswordType(context, packageName, numberPicker.getValue());
                                applicationList.get(passwordViewHolder.getAdapterPosition()).setLockUnlockPasswordDrawable(R.drawable.ic_vpn_key_green_24dp);
                                notifyItemChanged(passwordViewHolder.getAdapterPosition());
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return applicationList.size();
    }

    private void patternLockWork(final Context context, final int position){
        final LockUnlockModel lockUnlockModel = applicationList.get(position);
        final String packageName = lockUnlockModel.getApplicationInfo().packageName;
        View view = LayoutInflater.from(context).inflate(R.layout.design_pattern_lock,null);
        final PatternLockView patternLockView = view.findViewById(R.id.design_pattern);
        TextView clearBttn = view.findViewById(R.id.design_clear);
        final Dialog dialog = new Dialog(context);
        dialog.setTitle("Set pattern");
        dialog.setContentView(view);
        dialog.show();

        clearBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatabaseHelper(context).updateLockUnlockPassword(packageName, "");
                applicationList.get(position).setLockUnlockPasswordDrawable(R.drawable.ic_vpn_key_green_24dp);
                notifyItemChanged(position);
                dialog.dismiss();
            }
        });

        patternLockView.setTactileFeedbackEnabled(true);
        patternLockView.addPatternLockListener(new PatternLockViewListener() {
            @Override
            public void onStarted() {
                if(nPattern.equals(""))
                    Toast.makeText(context,"New Pattern",Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(context,"Confirm Pattern",Toast.LENGTH_LONG).show();
                }
                else {
                    cPattern = PatternLockUtils.patternToMD5(patternLockView, pattern);
                    if(nPattern.equals(cPattern)){
                        patternLockView.setViewMode(PatternLockView.PatternViewMode.CORRECT);
                        new DatabaseHelper(context).updateLockUnlockPassword(packageName, nPattern);
                        applicationList.get(position).setLockUnlockPasswordDrawable(R.drawable.ic_vpn_key_red_24dp);
                        Toast.makeText(context,"Pattern Confirmed",Toast.LENGTH_SHORT).show();
                        notifyItemChanged(position);
                        dialog.dismiss();
                    }
                    else {
                        patternLockView.setViewMode(PatternLockView.PatternViewMode.WRONG);
                        Toast.makeText(context,"Incorrect! Try again",Toast.LENGTH_LONG).show();
                        nPattern="";
                    }
                }
            }

            @Override
            public void onCleared() {

            }
        });
    }
}

class PasswordViewHolder extends RecyclerView.ViewHolder {
    ImageView appIcon;
    TextView appName;
    ImageView passwordCheck;
    ImageView fingerprintCheck;
    LinearLayout appNameLayout;

    PasswordViewHolder(@NonNull View itemView) {
        super(itemView);
        appName = itemView.findViewById(R.id.appName);
        appIcon = itemView.findViewById(R.id.appIcon);
        passwordCheck = itemView.findViewById(R.id.password_check);
        fingerprintCheck = itemView.findViewById(R.id.fingerprint_check);
        appNameLayout = itemView.findViewById(R.id.app_name_layout);
    }
}

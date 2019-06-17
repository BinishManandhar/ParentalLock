package com.binish.parentallock.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.binish.parentallock.Activities.ActivityIndividualProfile;
import com.binish.parentallock.Database.DatabaseHelper;
import com.binish.parentallock.Models.ProfileModel;
import com.binish.parentallock.R;
import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;

import java.util.ArrayList;

public class ProfilesListAdapter extends RecyclerView.Adapter<ProfileViewHolder>  {
    Context context;
    ArrayList<ProfileModel> profiles;
    DatabaseHelper databaseHelper;
    ViewBinderHelper binderHelper = new ViewBinderHelper();

    public  ProfilesListAdapter(Context context, ArrayList<ProfileModel> profiles){
        this.context = context;
        this.profiles = profiles;
        databaseHelper = new DatabaseHelper(context);
    }
    @NonNull
    @Override
    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ProfileViewHolder(LayoutInflater.from(context).inflate(R.layout.design_profiles,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ProfileViewHolder profileViewHolder, int i) {
        final ProfileModel profile = profiles.get(i);
        binderHelper.bind(profileViewHolder.swipeRevealLayout,profile.getProfileName());
        profileViewHolder.profileName.setText(profile.getProfileName());
        profileViewHolder.isActive.setChecked(profile.isActive());
        profileViewHolder.isActive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(profile.isActive()){
                    isActiveWork(profile.getProfileName(),profile.getUnlockFrom(),profile.getUnlockTo(),false,profileViewHolder.getAdapterPosition());
                    Snackbar.make(v,"Inactive",Snackbar.LENGTH_SHORT).show();
                }
                else {
                    isActiveWork(profile.getProfileName(),profile.getUnlockFrom(),profile.getUnlockTo(),true,profileViewHolder.getAdapterPosition());
                    Snackbar.make(v,"Active",Snackbar.LENGTH_SHORT).show();
                }
                notifyItemChanged(profileViewHolder.getAdapterPosition());
            }
        });
        profileViewHolder.profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,ActivityIndividualProfile.class);
                intent.putExtra("ProfileName",profile.getProfileName());
                context.startActivity(intent);
            }
        });
        profileViewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("Delete Profile")
                        .setMessage("Are you sure you want to delete '"+profile.getProfileName()+"' ?")
                        .setIcon(R.drawable.ic_delete_red_24dp)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                databaseHelper.deleteIndividualProfile(profile.getProfileName());

                                profiles.remove(profileViewHolder.getAdapterPosition());
                                notifyItemChanged(profileViewHolder.getAdapterPosition());
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return profiles.size();
    }

    private void isActiveWork(String profileName,String unlockFrom,String unlockTo,boolean isActive,int position){

        databaseHelper.updateProfile(profileName,unlockFrom,unlockTo,isActive);
        profiles.get(position).setActive(isActive);
    }
}

class ProfileViewHolder extends RecyclerView.ViewHolder{
    SwipeRevealLayout swipeRevealLayout;
    LinearLayout profile;
    LinearLayout delete;
    TextView profileName;
    Switch isActive;
    ProfileViewHolder(@NonNull View itemView) {
        super(itemView);
        swipeRevealLayout = itemView.findViewById(R.id.swipe_layout);
        profileName = itemView.findViewById(R.id.profile_name);
        isActive = itemView.findViewById(R.id.profile_is_active);
        profile = itemView.findViewById(R.id.design_profiles);
        delete = itemView.findViewById(R.id.delete_layout);
    }
}

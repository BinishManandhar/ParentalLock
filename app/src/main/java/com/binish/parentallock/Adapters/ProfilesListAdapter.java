package com.binish.parentallock.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.binish.parentallock.R;

public class ProfilesListAdapter extends RecyclerView.Adapter<ProfileViewHolder>  {
    Context context;

    public  ProfilesListAdapter(Context context){
        this.context = context;
    }
    @NonNull
    @Override
    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ProfileViewHolder(LayoutInflater.from(context).inflate(R.layout.design_profiles,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileViewHolder profileViewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}

class ProfileViewHolder extends RecyclerView.ViewHolder{

    ProfileViewHolder(@NonNull View itemView) {
        super(itemView);
    }
}

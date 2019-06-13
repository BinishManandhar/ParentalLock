package com.binish.parentallock.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.binish.parentallock.Activities.ActivityCreateProfile;
import com.binish.parentallock.Adapters.ProfilesListAdapter;
import com.binish.parentallock.Database.DatabaseHelper;
import com.binish.parentallock.Models.ProfileModel;
import com.binish.parentallock.R;
import com.binish.parentallock.Utils.GlobalStaticVariables;

import java.util.ArrayList;
import java.util.Objects;

public class ProfilesListFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profiles,container,false);
        FloatingActionButton fab = view.findViewById(R.id.fab);
        Objects.requireNonNull(getActivity()).setTitle("Profiles");

        DatabaseHelper databaseHelper = new DatabaseHelper(getActivity());
        ArrayList<ProfileModel> profiles = databaseHelper.getAllProfiles();

        if(profiles.size()==0){
            view.findViewById(R.id.empty_text).setVisibility(View.VISIBLE);
            view.findViewById(R.id.empty_icon).setVisibility(View.VISIBLE);
        }
        else {
            view.findViewById(R.id.empty_text).setVisibility(View.GONE);
            view.findViewById(R.id.empty_icon).setVisibility(View.GONE);
        }


        RecyclerView recyclerView = view.findViewById(R.id.profile_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(new ProfilesListAdapter(getActivity(),profiles));

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),ActivityCreateProfile.class);
                startActivityForResult(intent,GlobalStaticVariables.profileFragmentRefresh);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GlobalStaticVariables.profileFragmentRefresh && resultCode == Activity.RESULT_OK)
            Log.i(GlobalStaticVariables.ACTIVITY_RESULT_LOGS,"Here");
            if (getFragmentManager() != null) {
                getFragmentManager().beginTransaction().detach(this).attach(this).commit();
            }
    }
}

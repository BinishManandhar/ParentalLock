package com.binish.parentallock.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.binish.parentallock.Activities.PasswordPage;
import com.binish.parentallock.Adapters.AppListAdapter;
import com.binish.parentallock.Adapters.PasswordListAdapter;
import com.binish.parentallock.Models.LockUnlockModel;
import com.binish.parentallock.R;
import com.binish.parentallock.Utils.UsefulFunctions;

import java.util.List;

public class PasswordListFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_passwords,container,false);
        RecyclerView recyclerView = view.findViewById(R.id.password_recyclerView);
        CardView cardView = view.findViewById(R.id.password_cardView);

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),PasswordPage.class);
                startActivity(intent);
            }
        });

        List<LockUnlockModel> applicationList = UsefulFunctions.getPasswordListForRecycler(getActivity(),
                UsefulFunctions.getAppList(getActivity()));

        if(applicationList.size()==0){
            view.findViewById(R.id.empty_text).setVisibility(View.VISIBLE);
            view.findViewById(R.id.empty_icon).setVisibility(View.VISIBLE);
        }
        else {
            view.findViewById(R.id.empty_text).setVisibility(View.GONE);
            view.findViewById(R.id.empty_icon).setVisibility(View.GONE);
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(
                new PasswordListAdapter(
                        getActivity(),applicationList));
        return view;
    }
}

package com.binish.parentallock.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.binish.parentallock.Adapters.AppListAdapter;
import com.binish.parentallock.R;
import com.binish.parentallock.Utils.UsefulFunctions;

import java.util.Objects;

public class AppListFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_main,container,false);
        Objects.requireNonNull(getActivity()).setTitle("Parental Lock");
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(
                new AppListAdapter(
                        getActivity(),
                        UsefulFunctions.getListForRecycler(getActivity(),
                                UsefulFunctions.getAppList(getActivity()))));
        return view;
    }
}

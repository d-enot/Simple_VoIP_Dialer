package com.temp.simplevoipdialer.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.temp.simplevoipdialer.R;
import com.temp.simplevoipdialer.adapters.CallAdapter;
import com.temp.simplevoipdialer.services.MainService;

/**
 * Created by klim-mobile on 03.09.2015.
 */
public class CallFragment extends Fragment {

    private RecyclerView recyclerView;
    private LinearLayoutManager linManager;
    private static CallAdapter adapter;
    public static Activity activity;
    public static CallFragment callFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.calls_layout, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.calls_recycler_view);
        linManager = new LinearLayoutManager(MainService.context);
        recyclerView.setLayoutManager(linManager);
        adapter = new CallAdapter(MainService.calls);

        activity = getActivity();

        recyclerView.setAdapter(adapter);

        callFragment = this;
        return view;
    }

    public static void updateAdapter() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (callFragment != null) {
            callFragment = null;
        }
    }
}

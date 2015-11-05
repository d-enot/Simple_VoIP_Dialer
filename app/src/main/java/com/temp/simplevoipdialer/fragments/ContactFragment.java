package com.temp.simplevoipdialer.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.temp.simplevoipdialer.R;
import com.temp.simplevoipdialer.adapters.ContactAdapter;
import com.temp.simplevoipdialer.services.MainService;

/**
 * Created by klim-mobile on 03.09.2015.
 */
public class ContactFragment extends Fragment {

    private RecyclerView recyclerView;
    private EditText searchTxet;
    private LinearLayoutManager linManager;
    public static Activity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.contacts_layout, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        searchTxet = (EditText) view.findViewById(R.id.searchText);
        linManager = new LinearLayoutManager(MainService.context);
        recyclerView.setLayoutManager(linManager);
        final ContactAdapter adapter = new ContactAdapter(MainService.contacts);

        activity = getActivity();

        searchTxet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                adapter.getFilter().filter(s.toString());
            }
        });
        recyclerView.setAdapter(adapter);
        return view;
    }
}

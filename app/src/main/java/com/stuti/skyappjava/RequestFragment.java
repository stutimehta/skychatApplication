package com.stuti.skyappjava;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class RequestFragment extends Fragment
{
    private View Requestfragmentview;
    private RecyclerView myrequestlist;


    public RequestFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Requestfragmentview= inflater.inflate(R.layout.fragment_request, container, false);

        myrequestlist=(RecyclerView) Requestfragmentview.findViewById(R.id.requestlist);
        myrequestlist.setLayoutManager(new LinearLayoutManager(getContext()));

        return Requestfragmentview;
    }
}
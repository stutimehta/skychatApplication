package com.stuti.skyappjava;

import static android.content.Intent.getIntent;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.ContentInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class GroupsFragment extends Fragment
{
    private View groupFragmentView;
    private ListView listview;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> listofgroup=new ArrayList<>();
    private DatabaseReference grouprefernce;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public GroupsFragment() {

        // Required empty public constructor
    }

    public static GroupsFragment newInstance(String param1, String param2) {
        GroupsFragment fragment = new GroupsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {




        groupFragmentView= inflater.inflate(R.layout.fragment_groups, container, false);

        grouprefernce= FirebaseDatabase.getInstance().getReference().child("Groups");


        InitializeFields();

        RetrieveAndDisplayGroups();

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
            {
                String currentGroupname=adapterView.getItemAtPosition(position).toString();

                Intent groupchatIntent=new Intent(getContext(), GroupChatActivity.class);
                groupchatIntent.putExtra("groupname", currentGroupname);
                startActivity(groupchatIntent);
            }
        });

        return groupFragmentView;
    }



    private void InitializeFields()
    {
        listview=(ListView) groupFragmentView.findViewById(R.id.listview_groups);
        arrayAdapter=new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1,listofgroup);
        listview.setAdapter(arrayAdapter);
    }

    private void RetrieveAndDisplayGroups()
    {
        grouprefernce.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                Set<String> set=new HashSet<>();
                Iterator iterator=dataSnapshot.getChildren().iterator();
                while (iterator.hasNext())
                {
                    set.add(((DataSnapshot)iterator.next()).getKey());
                }
                listofgroup.clear();
                listofgroup.addAll(set);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}
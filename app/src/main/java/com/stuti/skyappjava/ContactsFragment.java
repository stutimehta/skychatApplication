package com.stuti.skyappjava;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class ContactsFragment extends Fragment
{
    private View contactsview;
    private RecyclerView mycontactsrecycleview;
    private DatabaseReference dataref,userref;
    private FirebaseAuth mauth;
    private String currentuserID;


    public ContactsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        contactsview= inflater.inflate(R.layout.fragment_contacts, container, false);

        mycontactsrecycleview=(RecyclerView) contactsview.findViewById(R.id.contactsfragment);
        mycontactsrecycleview.setLayoutManager(new LinearLayoutManager(getContext()));

        mauth=FirebaseAuth.getInstance();
        currentuserID=mauth.getCurrentUser().getUid();
        dataref= FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentuserID);
        userref=FirebaseDatabase.getInstance().getReference().child("Users");

        return contactsview;
    }

    @Override
    public void onStart()
    {
        super.onStart();

        FirebaseRecyclerOptions options=
                new FirebaseRecyclerOptions.Builder<Contacts>().setQuery(dataref, Contacts.class).build();

        FirebaseRecyclerAdapter<Contacts, ContactsViewHolder> adapter
                =new FirebaseRecyclerAdapter<Contacts, ContactsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ContactsViewHolder holder, int position, @NonNull Contacts model)
            {

                String userID=getRef(position).getKey();

                userref.child(userID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.hasChild("image"))
                        {
                            String profileimage=dataSnapshot.child("image").getValue().toString();
                            String username=dataSnapshot.child("name").getValue().toString();
                            String userstatus=dataSnapshot.child("status").getValue().toString();

                            holder.uname.setText(username);
                            holder.ustatus.setText(userstatus);
                            Picasso.get().load(profileimage).placeholder(R.drawable.user).into(holder.profileimg);


                        }
                        else
                        {
                            String username=dataSnapshot.child("name").getValue().toString();
                            String userstatus=dataSnapshot.child("status").getValue().toString();


                            holder.uname.setText(username);
                            holder.ustatus.setText(userstatus);


                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @NonNull
            @Override
            public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType)
            {
                View  view=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_layout, viewGroup,false);
                ContactsViewHolder viewHolder=new ContactsViewHolder(view);
                return viewHolder;
            }
        };
        mycontactsrecycleview.setAdapter(adapter);
        adapter.startListening();
    }





    public static class ContactsViewHolder extends RecyclerView.ViewHolder
    {

        TextView uname,ustatus;
        CircleImageView profileimg;
        public ContactsViewHolder(@NonNull View itemView)
        {
            super(itemView);

            uname=itemView.findViewById(R.id.profilenametext);
            ustatus=itemView.findViewById(R.id.profilestatus);
            profileimg=itemView.findViewById(R.id.profileimguser);
        }
    }
}
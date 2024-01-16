package com.stuti.skyappjava;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Toolbar mtoolbar;
    private DatabaseReference ref;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        ref= FirebaseDatabase.getInstance().getReference().child("Users");

        recyclerView=(RecyclerView) findViewById(R.id.findfriendsrecycleview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mtoolbar=(Toolbar) findViewById(R.id.findfriendsactivity);
        setSupportActionBar(mtoolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Find Friends");

    }

    @Override

    protected void onStart()
    {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options=
                new FirebaseRecyclerOptions.Builder<Contacts>()
                        .setQuery(ref,Contacts.class)
                        .build();

        FirebaseRecyclerAdapter<Contacts, FindFriendViewHolder> adapter=
                new FirebaseRecyclerAdapter<Contacts, FindFriendViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull FindFriendViewHolder holder, @SuppressLint("RecyclerView") int pos, @NonNull Contacts model)
                    {

                        holder.uname.setText(model.getName());
                        holder.ustatus.setText(model.getStatus());
                        Picasso.get().load(model.getImage()).placeholder(R.drawable.user).into(holder.pimg);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String visituserid=getRef(pos).getKey();
                                Intent profileintent=new Intent(FindFriendsActivity.this,ProfileActivity.class);
                                profileintent.putExtra("Visit UserID", visituserid);
                                startActivity(profileintent);
                            }
                        });

                    }

                    @NonNull
                    @Override
                    public FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType)
                    {
                        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_layout,viewGroup,false);
                        FindFriendViewHolder viewHolder=new FindFriendViewHolder(view);
                        return viewHolder;
                    }
                };


        recyclerView.setAdapter(adapter);

        adapter.startListening();


    }

    public static class FindFriendViewHolder extends RecyclerView.ViewHolder
    {
        TextView uname,ustatus;
        CircleImageView pimg;
        public FindFriendViewHolder(@NonNull View itemView)
        {
            super(itemView);

            uname=itemView.findViewById(R.id.profilenametext);
            ustatus=itemView.findViewById(R.id.profilestatus);
            pimg=itemView.findViewById(R.id.profileimguser);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
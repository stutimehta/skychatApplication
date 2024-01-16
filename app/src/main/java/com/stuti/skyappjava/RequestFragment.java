package com.stuti.skyappjava;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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


public class RequestFragment extends Fragment
{
    private View Requestfragmentview;
    private RecyclerView myrequestlist;

    private DatabaseReference ChatRequestRef,UserRef;

    private FirebaseAuth mAuth;
    private String currentUserID;


    public RequestFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Requestfragmentview= inflater.inflate(R.layout.fragment_request, container, false);
        UserRef=FirebaseDatabase.getInstance().getReference().child("Users");
        ChatRequestRef= FirebaseDatabase.getInstance().getReference().child("Chat Request");
        mAuth=FirebaseAuth.getInstance();
        currentUserID=mAuth.getCurrentUser().getUid();

        myrequestlist=(RecyclerView) Requestfragmentview.findViewById(R.id.requestlist);
        myrequestlist.setLayoutManager(new LinearLayoutManager(getContext()));

        return Requestfragmentview;


    }

    @Override
    public void onStart()
    {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options=
                new FirebaseRecyclerOptions.Builder<Contacts>()
                        .setQuery(ChatRequestRef.child(currentUserID),Contacts.class)
                        .build();

        FirebaseRecyclerAdapter<Contacts,RequestsViewHolder>adapter=
                new FirebaseRecyclerAdapter<Contacts, RequestsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull RequestsViewHolder holder, int position, @NonNull Contacts model)
                    {
                        holder.itemView.findViewById(R.id.requestacceptbtn).setVisibility(View.VISIBLE);
                        holder.itemView.findViewById(R.id.requestcancelbtn).setVisibility(View.VISIBLE);






                        final String list_user_id=getRef(position).getKey();
                        DatabaseReference getTypeRef=getRef(position).child("request_type").getRef();
                        getTypeRef.addValueEventListener(new ValueEventListener()
                        {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot)
                            {
                                if(snapshot.exists()){
                                    String type =snapshot.getValue().toString();
                                    if(type.equals("received"))
                                    {
                                        UserRef.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot)
                                            {
                                                if(snapshot.hasChild("image"))
                                                {
                                                    final String requestUserName =snapshot.child("name").getValue().toString();
                                                    final String requestUserStatus =snapshot.child("name").getValue().toString();
                                                    final String requestUserProfileImg =snapshot.child("name").getValue().toString();

                                                    holder.userName.setText(requestUserName);
                                                    holder.userStatus.setText(requestUserStatus);
                                                    Picasso.get().load(requestUserProfileImg).into(holder.profileImg);

                                                }
                                                else{
                                                    final String requestUserName =snapshot.child("name").getValue().toString();
                                                    final String requestUserStatus =snapshot.child("name").getValue().toString();

                                                    holder.userName.setText(requestUserName);
                                                    holder.userStatus.setText(requestUserStatus);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public RequestsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                    {
                      View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout,parent,false);
                   RequestsViewHolder holder = new RequestsViewHolder(view);
                   return holder;
                    }
                };
        myrequestlist.setAdapter(adapter);
        adapter.startListening();

    }
    public static class RequestsViewHolder extends RecyclerView.ViewHolder
    {
        TextView userName ,userStatus;
        CircleImageView profileImg;
        Button AcceptButton ,CancelButton;

        public RequestsViewHolder(@NonNull View itemView)
        {
            super(itemView);

            userName=itemView.findViewById(R.id.profilenametext);
            userStatus=itemView.findViewById(R.id.profilestatus);
              profileImg=itemView.findViewById(R.id.profileimguser);
           AcceptButton=itemView.findViewById(R.id.requestacceptbtn);
           CancelButton=itemView.findViewById(R.id.requestcancelbtn);



        }
    }
}
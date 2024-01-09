package com.stuti.skyappjava;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity
{
    private String recieveuserid,currentstate,senduserid;

    private CircleImageView uprofileimg;
    private TextView uprofilename,uprofilestatus;
    private Button sendbtn,deletebtn;
    private DatabaseReference ref,chatreqref,contactsref;
    private FirebaseAuth mauth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mauth=FirebaseAuth.getInstance();
        ref= FirebaseDatabase.getInstance().getReference().child("Users");
        chatreqref= FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        contactsref= FirebaseDatabase.getInstance().getReference().child("Contacts");



        Toast.makeText(this, "User ID: "+recieveuserid, Toast.LENGTH_SHORT).show();


        recieveuserid=getIntent().getExtras().get("Visit UserID").toString();
        senduserid=mauth.getCurrentUser().getUid();

        uprofileimg=(CircleImageView) findViewById(R.id.visitprofileimg);
        uprofilename=(TextView) findViewById(R.id.unamevisit);
        uprofilestatus=(TextView) findViewById(R.id.ustatusvisit);
        sendbtn=(Button) findViewById(R.id.sendmsgbtn);
        deletebtn=(Button) findViewById(R.id.deletemsgbtn);
        currentstate="new";

        RetrieveUserInfo();

    }

    private void RetrieveUserInfo()
    {
        ref.child(recieveuserid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("image")))

                {
                    String userimg=dataSnapshot.child("image").getValue().toString();
                    String username=dataSnapshot.child("name").getValue().toString();
                    String userstatus=dataSnapshot.child("status").getValue().toString();

                    Picasso.get().load(userimg).placeholder(R.drawable.user).into(uprofileimg);
                    uprofilename.setText(username);
                    uprofilestatus.setText(userstatus);

                    ManageChatRequest();
                }
                else
                {
                    String username=dataSnapshot.child("name").getValue().toString();
                    String userstatus=dataSnapshot.child("status").getValue().toString();

                    uprofilename.setText(username);
                    uprofilestatus.setText(userstatus);

                    ManageChatRequest();


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }




    private void ManageChatRequest()
    {
        chatreqref.child(senduserid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.hasChild(recieveuserid))
                        {
                            String request_type=dataSnapshot.child(recieveuserid).child("request_type").getValue().toString();

                            if (request_type.equals("sent"))
                            {
                                currentstate="request_sent";
                                sendbtn.setText("Cancel Chat Request");
                            }
                            else if (request_type.equals("recieved"))
                            {
                                currentstate="request_recieved";
                                sendbtn.setText("Accept Chat Request");

                                deletebtn.setVisibility(View.VISIBLE);
                                deletebtn.setEnabled(true);
                                deletebtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view)
                                    {
                                        CancelChatRequest();

                                    }
                                });
                            }

                        }
                        else
                        {
                            contactsref.child(senduserid)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                        {
                                            if (dataSnapshot.hasChild(recieveuserid))
                                            {
                                                currentstate="friends";
                                                sendbtn.setText("Remove this Contacts");
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        if (!senduserid.equals(recieveuserid))
        {
            sendbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    sendbtn.setEnabled(false);

                    if (currentstate.equals("new"))
                    {
                        SendChatRequest();
                    }

                    if (currentstate.equals("request_sent"))
                    {
                        CancelChatRequest();
                    }
                    if (currentstate.equals("request_recieved"))
                    {
                        AcceptChatRequest();
                    }
                    if (currentstate.equals("friends"))
                    {
                        RemoveSpecificContact();
                    }

                }
            });
        }
        else
        {
            sendbtn.setVisibility(View.INVISIBLE);
        }
    }

    private void RemoveSpecificContact()
    {
        contactsref.child(senduserid).child(recieveuserid)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            contactsref.child(recieveuserid).child(senduserid)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                sendbtn.setEnabled(true);
                                                currentstate="new";
                                                sendbtn.setText("Send Message");

                                                deletebtn.setVisibility(View.INVISIBLE);
                                                deletebtn.setEnabled(false);
                                            }

                                        }
                                    });
                        }

                    }
                });

    }

    private void AcceptChatRequest()
    {
        contactsref.child(senduserid).child(recieveuserid)
                .child("Contacts").setValue("Saved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            contactsref.child(recieveuserid).child(senduserid)
                                    .child("Contacts").setValue("Saved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                chatreqref.child(senduserid).child(recieveuserid)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task)
                                                            {
                                                                if (task.isSuccessful())
                                                                {
                                                                    chatreqref.child(recieveuserid).child(senduserid)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task)
                                                                                {
                                                                                    sendbtn.setEnabled(true);
                                                                                    currentstate="friends";
                                                                                    sendbtn.setText("Remove this Contact");

                                                                                    deletebtn.setVisibility(View.INVISIBLE);
                                                                                    deletebtn.setEnabled(false);


                                                                                }
                                                                            });
                                                                }

                                                            }
                                                        });

                                            }

                                        }
                                    });

                        }

                    }
                });
    }

    private void CancelChatRequest()
    {
        chatreqref.child(senduserid).child(recieveuserid)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            chatreqref.child(recieveuserid).child(senduserid)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                sendbtn.setEnabled(true);
                                                currentstate="new";
                                                sendbtn.setText("Send Message");

                                                deletebtn.setVisibility(View.INVISIBLE);
                                                deletebtn.setEnabled(false);
                                            }

                                        }
                                    });
                        }

                    }
                });
    }

    private void SendChatRequest()
    {
        chatreqref.child(senduserid).child(recieveuserid)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            chatreqref.child(recieveuserid).child(senduserid)
                                    .child("request_type").setValue("recieved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                sendbtn.setEnabled(true);
                                                currentstate="request_sent";
                                                sendbtn.setText("Cancel Chat Request");
                                            }

                                        }
                                    });
                        }

                    }
                });
    }
}
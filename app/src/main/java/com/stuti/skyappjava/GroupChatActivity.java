
package com.stuti.skyappjava;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;


import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class GroupChatActivity extends AppCompatActivity
{
    private FirebaseAuth mauth;
    private DatabaseReference userref,groupnameref,groupmessagekeyref;
    private Toolbar toolbar;
    private ImageButton sendmessagebtn;
    private EditText usermessagetext;
    private ScrollView scrollView;
    private TextView displaymessages;
    private String currentgroupname,currentUserID,currentUserName,currentdate,currenttime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        currentgroupname=getIntent().getExtras().get("groupname").toString();
        Toast.makeText(GroupChatActivity.this, currentgroupname, Toast.LENGTH_SHORT).show();


        mauth=FirebaseAuth.getInstance();
        currentUserID=mauth.getCurrentUser().getUid();
        userref= FirebaseDatabase.getInstance().getReference().child("Users");
        groupnameref=FirebaseDatabase.getInstance().getReference().child("Groups").child(currentgroupname);


        InitializeFields();

        GetUserInfo();

        sendmessagebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                SaveMessageInfoToDatabase();

                usermessagetext.setText("");

                scrollView.fullScroll(ScrollView.FOCUS_DOWN);

            }
        });
    }


    @Override
    protected void onStart()
    {
        super.onStart();

        groupnameref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName)
            {

                if (dataSnapshot.exists())
                {
                    DisplayMessages(dataSnapshot);
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName)
            {
                if (dataSnapshot.exists())
                {
                    DisplayMessages(dataSnapshot);
                }


            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void InitializeFields()
    {
        toolbar=(Toolbar) findViewById(R.id.group_chat_bar_layout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Group Name");
        sendmessagebtn=(ImageButton) findViewById(R.id.sendmessagebtn_groupchat);
        usermessagetext=(EditText) findViewById(R.id.input_message_groupchat);
        displaymessages=(TextView) findViewById(R.id.text_groupchat);
        scrollView=(ScrollView) findViewById(R.id.scrollview_groupchat);
    }

    private void GetUserInfo()
    {
        userref.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    currentUserName=dataSnapshot.child("name").getValue().toString();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }

    private void SaveMessageInfoToDatabase()
    {
        String message=usermessagetext.getText().toString();
        String messageKEY=groupnameref.push().getKey();

        if(TextUtils.isEmpty(message))
        {
            Toast.makeText(this, "Please Enter Message First...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Calendar calForDate= Calendar.getInstance();
            SimpleDateFormat currentDateformat=new SimpleDateFormat("MMM dd, yyyy");
            currentdate=currentDateformat.format(calForDate.getTime());

            Calendar calForTime= Calendar.getInstance();
            SimpleDateFormat currentTimeformat=new SimpleDateFormat("hh:mm a");
            currenttime=currentTimeformat.format(calForTime.getTime());

            HashMap<String, Object> groupMessageKey=new HashMap<>();
            groupnameref.updateChildren(groupMessageKey);

            groupmessagekeyref=groupnameref.child(messageKEY);

            HashMap<String, Object> messageInfoMap=new HashMap<>();
                  messageInfoMap.put("name",currentUserName);
                  messageInfoMap.put("message",message);
                  messageInfoMap.put("date",currentdate);
                  messageInfoMap.put("time",currenttime);
            groupmessagekeyref.updateChildren(messageInfoMap);






        }
    }

    private void DisplayMessages(DataSnapshot dataSnapshot) {
        Iterator iterator = dataSnapshot.getChildren().iterator();

        while (iterator.hasNext())
        {
            String chatDate=(String) ((DataSnapshot)iterator.next()).getValue();
            String chatMessage=(String) ((DataSnapshot)iterator.next()).getValue();
            String chatName=(String) ((DataSnapshot)iterator.next()).getValue();
            String chatTime=(String) ((DataSnapshot)iterator.next()).getValue();

            displaymessages.append(chatName+":\n" +chatMessage+"\n" +chatTime+"  "+chatDate+"\n\n\n");

            scrollView.fullScroll(ScrollView.FOCUS_DOWN);
        }
    }



}
package com.stuti.skyappjava;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private Button updatebtn;
    private EditText uname, ustatus;
    private CircleImageView uprofileimg;
    private String currentuserID;
    private FirebaseAuth mauth;
    private DatabaseReference rootref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mauth=FirebaseAuth.getInstance();
        currentuserID=mauth.getCurrentUser().getUid();
        rootref= FirebaseDatabase.getInstance().getReference();

        InitializeFields();

        uname.setVisibility(View.INVISIBLE);

        updatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateSettings();
            }
        });
        
        
        RetrieveUserInfo();


    }

   

    private void UpdateSettings()
    {
        String setuname=uname.getText().toString();
        String setstatus=ustatus.getText().toString();

        if (TextUtils.isEmpty(setuname))
        {
            Toast.makeText(this, "Please Write Your Username", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(setstatus))
        {
            Toast.makeText(this, "Please Write Your Status", Toast.LENGTH_SHORT).show();
        }
        else
        {
            HashMap<String, String> profilemap=new HashMap<>();
            profilemap.put("uid,", currentuserID);
            profilemap.put("name",setuname);
            profilemap.put("status",setstatus);
            rootref.child("Users").child(currentuserID).setValue(profilemap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                SendUserToMainActivity();
                                Toast.makeText(SettingsActivity.this, "Profile Updated Succesfully...", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                String message=task.getException().toString();
                                Toast.makeText(SettingsActivity.this, "Error"+ message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }

    private void RetrieveUserInfo()
    {
        rootref.child("Users").child(currentuserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name") && (dataSnapshot.hasChild("image"))))
                        {
                            String retrieveUserName=dataSnapshot.child("name").getValue().toString();
                            String retrieveStatus=dataSnapshot.child("status").getValue().toString();
                            String retrieveProfileImage=dataSnapshot.child("image").getValue().toString();

                            uname.setText(retrieveUserName);
                            ustatus.setText(retrieveStatus);

                        }
                        else if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name")))
                        {
                            String retrieveUserName=dataSnapshot.child("name").getValue().toString();
                            String retrieveStatus=dataSnapshot.child("status").getValue().toString();

                            uname.setText(retrieveUserName);
                            ustatus.setText(retrieveStatus);


                        }
                        else
                        {
                            uname.setVisibility(View.VISIBLE);
                            Toast.makeText(SettingsActivity.this, "Please set and update your profile information...", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    private void InitializeFields() {
        updatebtn = (Button) findViewById(R.id.update_button);
        uname = (EditText) findViewById(R.id.setname_text);
        ustatus = (EditText) findViewById(R.id.online_text);
        uprofileimg = (CircleImageView) findViewById(R.id.profile_image);
    }



    private void SendUserToMainActivity()
    {
        Intent mainIntent=new Intent(SettingsActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();

    }


}
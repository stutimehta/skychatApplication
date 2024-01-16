package com.stuti.skyappjava;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity
{

    private Toolbar mToolbar;
    private ViewPager myViewPager;
    private TabLayout myTablayout;

    private TabsAccessorAdapter myTabsaccessoradapter;
    private FirebaseUser currentUser;

    private FirebaseAuth mauth;
    private FirebaseUser currentuser;
    private DatabaseReference rootref;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mauth=FirebaseAuth.getInstance();
        currentuser=mauth.getCurrentUser();
        rootref=FirebaseDatabase.getInstance().getReference();

        mToolbar=(Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("SkyChat");

        myViewPager=(ViewPager) findViewById(R.id.main_tabs_pager);
        myTabsaccessoradapter=new TabsAccessorAdapter(getSupportFragmentManager());
        myViewPager.setAdapter(myTabsaccessoradapter);

        myTablayout=(TabLayout) findViewById(R.id.main_tabs_layout);
        myTablayout.setupWithViewPager(myViewPager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (currentuser==null)
        {
            SendUserToLoginActivity();
        }
        else
        {
            VerifyUserExistance();
            
        }
    }

    private void VerifyUserExistance()
    {
        String currentuserID=mauth.getCurrentUser().getUid();
        rootref.child("Users").child(currentuserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if ((dataSnapshot.child("name").exists()))
                {
                    Toast.makeText(MainActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    SendUserToSettingsActivity();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
         super.onOptionsItemSelected(item);

         if (item.getItemId()==R.id.logout_menu)
         {
             mauth.signOut();
             SendUserToLoginActivity();
         }
         if (item.getItemId()==R.id.settings_menu)
         {
             SendUserToSettingsActivity();
         }
        if (item.getItemId()==R.id.creategroup_menu)
        {
            RequestNewGroup();
        }

         if (item.getItemId()==R.id.findfriends_menu)
         {

             SendUserToFindFirendsActivity();

         }
         return true;
    }

    private void RequestNewGroup()
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this,R.style.AlertDialog);
        builder.setTitle("Enter Your Group Name:");

        final EditText groupnamefiled=new EditText(MainActivity.this);
        groupnamefiled.setHint("e.g Android Developers");
        builder.setView(groupnamefiled);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                String groupname=groupnamefiled.getText().toString();

                if (TextUtils.isEmpty(groupname))
                {
                    Toast.makeText(MainActivity.this, "Please Give The Name Of Your Group", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    CreateNewGroup(groupname);
                }

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
               dialogInterface.cancel();

            }
        });
        builder.show();

    }

    private void CreateNewGroup(String groupname)
    {
        rootref.child("Groups").child(groupname).setValue("")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(MainActivity.this, groupname +" Group Is Created Successfully...", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    private void SendUserToLoginActivity()
    {
        Intent loginIntent=new Intent(MainActivity.this,LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }
    private void SendUserToSettingsActivity()
    {
        Intent settingsIntent=new Intent(MainActivity.this,SettingsActivity.class);
        settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(settingsIntent);
        finish();
    }

    private void SendUserToFindFirendsActivity()
    {
        Intent findfriendIntent=new Intent(MainActivity.this,FindFriendsActivity.class);
        startActivity(findfriendIntent);

    }


}
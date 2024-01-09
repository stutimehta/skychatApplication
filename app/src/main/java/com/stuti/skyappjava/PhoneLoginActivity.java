package com.stuti.skyappjava;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity extends AppCompatActivity
{
    private ProgressDialog loadingbar;
    private Button sendverificationbtn,verifybtn;
    private EditText phonenumbertext,verificationcodetext;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private FirebaseAuth mauth;
    private String mverificationid;
    private PhoneAuthProvider.ForceResendingToken mresendtoken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);

        mauth=FirebaseAuth.getInstance();

        sendverificationbtn=(Button) findViewById(R.id.sendverificationcode_button);
        verifybtn=(Button) findViewById(R.id.verify_button);
        phonenumbertext=(EditText) findViewById(R.id.phoneno_input);
        verificationcodetext=(EditText) findViewById(R.id.phoneverification_input);
        loadingbar=new ProgressDialog(this);

        sendverificationbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {


               String phoneno=phonenumbertext.getText().toString();

               if (TextUtils.isEmpty(phoneno))
               {
                   Toast.makeText(PhoneLoginActivity.this, "Please Enter Your Phone Number", Toast.LENGTH_SHORT).show();
               }
               else
               {
                   loadingbar.setTitle("Phone Verification");
                   loadingbar.setMessage("Please Wait While We Are Authenticating Your Phone");
                   loadingbar.setCanceledOnTouchOutside(false);
                   loadingbar.show();
                   PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneno,60,TimeUnit.SECONDS,PhoneLoginActivity.this,callbacks);
               }

            }
        });

        verifybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                sendverificationbtn.setVisibility(View.INVISIBLE);
                phonenumbertext.setVisibility(View.INVISIBLE);

                String verificationcode=verificationcodetext.getText().toString();

                if (TextUtils.isEmpty(verificationcode))
                {
                    Toast.makeText(PhoneLoginActivity.this, "Please Write Verification Code First...", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    loadingbar.setTitle("Verification Code");
                    loadingbar.setMessage("Please Wait, Verifying Code");
                    loadingbar.setCanceledOnTouchOutside(false);
                    loadingbar.show();

                    PhoneAuthCredential credential=PhoneAuthProvider.getCredential(mverificationid, verificationcode);
                    signInWithPhoneAuthCredential(credential);
                }

            }
        });

        callbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks()
        {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential)
            {
                signInWithPhoneAuthCredential(phoneAuthCredential);

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e)
            {
                loadingbar.dismiss();
                Toast.makeText(PhoneLoginActivity.this, "Invalid Phone Number, Please Enter Your Correct Phone Number...", Toast.LENGTH_SHORT).show();
                sendverificationbtn.setVisibility(View.VISIBLE);
                phonenumbertext.setVisibility(View.VISIBLE);

                verifybtn.setVisibility(View.INVISIBLE);
                verificationcodetext.setVisibility(View.INVISIBLE);


            }

            @Override
            public void onCodeSent(@NonNull String verificationID, @NonNull PhoneAuthProvider.ForceResendingToken token)
            {
                mverificationid=verificationID;
                mresendtoken=token;

                loadingbar.dismiss();

                Toast.makeText(PhoneLoginActivity.this, "Code Has Been Sent", Toast.LENGTH_SHORT).show();

                sendverificationbtn.setVisibility(View.INVISIBLE);
                phonenumbertext.setVisibility(View.INVISIBLE);

                verifybtn.setVisibility(View.VISIBLE);
                verificationcodetext.setVisibility(View.VISIBLE);

            }
        };

    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential)
    {
        mauth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if (task.isSuccessful())
                {
                    loadingbar.dismiss();
                    Toast.makeText(PhoneLoginActivity.this, "Logged In Successfully", Toast.LENGTH_SHORT).show();
                    SendUserToMainActivity();

                }
                else
                {
                    String message=task.getException().toString();
                    Toast.makeText(PhoneLoginActivity.this, "Error:"+message, Toast.LENGTH_SHORT).show();

                }

            }
        });
    }

    private void SendUserToMainActivity()
    {
        Intent mainintent=new Intent(PhoneLoginActivity.this,MainActivity.class);
        startActivity(mainintent);
        finish();
    }
}
package com.philimonnag.mpc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ForgotPassword extends AppCompatActivity {
 EditText fogotEmail;
 Button recovery;
 FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        fogotEmail=findViewById(R.id.forgot_email);
        recovery=findViewById(R.id.recovery);

//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setTitle("Forgot Password");
//        actionBar.setDisplayShowHomeEnabled(true);
//        actionBar.setDisplayHomeAsUpEnabled(true);

        auth =FirebaseAuth.getInstance();
        recovery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            String email = fogotEmail.getText().toString();
           if(TextUtils.isEmpty(email)){
               fogotEmail.setError("Email is Required");
           }else {
               resetPassword(email);
           }
            }
        });

    }

    private void resetPassword(String email) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete( Task<Void> task) {
             if(task.isSuccessful()){
                 Toast.makeText(ForgotPassword.this, "A reset Link is Sent To Your Email Address", Toast.LENGTH_SHORT).show();
                 startActivity(new Intent(ForgotPassword.this,Signing.class));
             }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
             Toast.makeText(ForgotPassword.this,""+e,Toast.LENGTH_SHORT).show();
            }
        });
    }
}
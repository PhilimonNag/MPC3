package com.philimonnag.mpc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Signing extends AppCompatActivity {
    private EditText loginEmail, loginPassword;
    Button loginBtn;
    TextView forgotPassword, needAnAccount;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signing);
        loginEmail = findViewById(R.id.login_email);
        loginPassword = findViewById(R.id.login_password);
        forgotPassword= findViewById(R.id.forgot_password);
        needAnAccount = findViewById(R.id.need_an_account);
        loginBtn = findViewById(R.id.login_Btn);
        auth = FirebaseAuth.getInstance();
        needAnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Signing.this,Register.class));
            }
        });
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              startActivity(new Intent(Signing.this ,ForgotPassword.class));
            }
        });
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = loginEmail.getText().toString().trim();
                String password = loginPassword.getText().toString().trim();
                if(TextUtils.isEmpty(email)){
                    loginEmail.setError(" Email is Required");
                }else if(TextUtils.isEmpty(password)){
                    loginPassword.setError("Password is required");
                }else {
                    login(email,password);
                }

            }
        });
    }

    private void login(String email, String password) {
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(Signing.this, "Login Sucsessfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Signing.this,MainActivity.class));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Signing.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        FirebaseUser user = auth.getCurrentUser();
//        if (user==null){
//            startActivity(new Intent(Signing.this,MainActivity.class));
//        }
//    }
}

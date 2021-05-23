package com.philimonnag.mpc;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    EditText registerMail, registerPassword,username;
    Button registerBtn;
    TextView alreadyHaveAnAcc;
    FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    ImageView profile_pic;
    StorageReference storageReference;
    FirebaseFirestore fstore;
    String userId;
    Uri imgUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        registerMail = findViewById(R.id.register_email);
        registerPassword=findViewById(R.id.register_password);
        username=findViewById(R.id.register_username);
        alreadyHaveAnAcc = findViewById(R.id.already_have_an_account);
        registerBtn = findViewById(R.id.register_Btn);
        profile_pic =findViewById(R.id.pic);
//        ActionBar acctionBar = getSupportActionBar();
//        acctionBar.setTitle("Register");
//        acctionBar.setDisplayHomeAsUpEnabled(true);
//        acctionBar.setDisplayShowHomeEnabled(true);
        mAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        storageReference= FirebaseStorage.getInstance().getReference();
        progressDialog=new ProgressDialog(this);

        profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGallery,1000);

            }
        });

        alreadyHaveAnAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Register.this,Signing.class));
            }
        });
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri pImg=imgUri;
                String uName=username.getText().toString();
                String email = registerMail.getText().toString().trim();
                String password = registerPassword.getText().toString().trim();
                if(TextUtils.isEmpty(email)){
                    registerMail.setError("Email is Required");
                }else if(TextUtils.isEmpty(password)){
                    registerPassword.setError("Password is Required");
                }else if(password.length()<6){
                    Toast.makeText(Register.this, "Password length should Be 6", Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(uName)) {
                    username.setError("User Name is Required");
                }else if(pImg!=null) {
                    registerUser(email,password,uName,pImg);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1000){
            if(resultCode==Activity.RESULT_OK){
                imgUri=data.getData();
                profile_pic.setImageURI(imgUri);
                uploadImagetoFirebase(imgUri);
            }
        }
    }



    private void uploadImagetoFirebase(Uri imageuri) {
        StorageReference fileref=storageReference.child("profile.jpg");
        fileref.putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(Register.this, "Image Uploded", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(Register.this, "failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void registerUser(String email, String password,String uName,Uri pImg) {
        progressDialog.setTitle("Please Wait...");
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                        progressDialog.dismiss();
                        userId=mAuth.getCurrentUser().getUid();
                        DocumentReference documentReference=fstore.collection("users").document(userId);
                        Map<String,Object> user = new HashMap<>();
                         user.put("uName",uName);
                         user.put("pImg",pImg);
                         documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(Register.this, "Jesus Loves You", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                             @Override
                             public void onFailure( Exception e) {
                                 Toast.makeText(Register.this, "Please Jesus help me", Toast.LENGTH_SHORT).show();
                             }
                         });
                    startActivity(new Intent(Register.this,Signing.class));


                }else {
                    Toast.makeText(Register.this, "Register Failed", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Register.this,Signing.class));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Register.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
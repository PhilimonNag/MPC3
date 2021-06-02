package com.philimonnag.mpc;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    TextInputLayout registerMail, registerPassword,username;
    Button registerBtn;
    TextView alreadyHaveAnAcc;
    FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    ImageView profile_pic;
    StorageReference storageReference;
    FirebaseFirestore fstore;
    UploadTask uploadTask;
    DocumentReference documentReference;
   //DatabaseReference databaseReference;
    String userId;
    Uri imgUri;
    //Member member;
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
        mAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
//        FirebaseUser user =mAuth.getCurrentUser();
//        userId=user.getUid();
//        documentReference = fstore.collection("user").document(userId);
//        fstore = FirebaseFirestore.getInstance();
//        storageReference= FirebaseStorage.getInstance().getReference("Profile images");
//        databaseReference = FirebaseDatabase.getInstance().getReference("All Users");
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
                registerUser();

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{
            if(requestCode==1000 && resultCode==Activity.RESULT_OK && data!=null && data.getData()!=null){
                imgUri=data.getData();
                Picasso.get().load(imgUri).into(profile_pic);
 //               profile_pic.setImageURI(imgUri);
//                uploadImagetoFirebase(imgUri);

            }
        }catch (Exception e){
        Toast.makeText(this,"Sorry"+e, Toast.LENGTH_SHORT).show();
    }
    }

    private String getFileExt(Uri uri){
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }

    private void registerUser() {
        progressDialog.setTitle("Please Wait...");
        progressDialog.show();
        String uName=username.getEditText().getText().toString();
        String email = registerMail.getEditText().getText().toString().trim();
        String password = registerPassword.getEditText().getText().toString().trim();
        if(TextUtils.isEmpty(email)){
            registerMail.setError("Email is Required");
        }else if(TextUtils.isEmpty(password)){
            registerPassword.setError("Password is Required");
        }else if(password.length()<6){
            Toast.makeText(Register.this, "Password length should Be 6", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(uName)) {
            username.setError("User Name is Required");
        }else if(imgUri==null) {
            Toast.makeText(Register.this, "please Choose a image", Toast.LENGTH_SHORT).show();
        }else{
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        progressDialog.dismiss();
                        userId=mAuth.getCurrentUser().getUid();
                        documentReference = fstore.collection("user").document(userId);
                        storageReference= FirebaseStorage.getInstance().getReference("Profile images");
                       // databaseReference = FirebaseDatabase.getInstance().getReference("All Users");
                        final StorageReference reference = storageReference.child(System.currentTimeMillis()+"."+getFileExt(imgUri));
                        uploadTask =reference.putFile(imgUri);
                        Task<Uri> uriTask =uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull  Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }
                                return reference.getDownloadUrl();}

                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if(task.isSuccessful()){
                                    Uri downloadUri=task.getResult();
                                    String url=downloadUri.toString();
                                    Map<String,String>profile=new HashMap<>();
                                    profile.put("uName",uName);
                                    profile.put("email",email);
                                    profile.put("url",url);
                                   // databaseReference.child(userId).setValue(profile);
                                    documentReference.set(profile).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(Register.this, "God bless You", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(Register.this,Signing.class));

                                        }
                                    });
                                }
                            }
                        });

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

}
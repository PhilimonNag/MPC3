package com.philimonnag.mpc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private Animation rotateOpen,rotateclose,frombuttom,tobuttom;
    private TextView preachText,prayText,eventText,chapter,verse;
    private FloatingActionButton add_button, event_button, pray_button, preach_button;
    private  boolean isopen = false;
    FirebaseAuth mAuth;
    FirebaseFirestore fstore;
    String userId;

     FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();
        add_button = findViewById(R.id.add_button);
        event_button = findViewById(R.id.event_button);
        pray_button = findViewById(R.id.pray_button);
        preach_button = findViewById(R.id.preach_button);
        chapter= findViewById(R.id.chapter);
        verse =findViewById(R.id.verse);
        prayText=findViewById(R.id.prayText);
        preachText=findViewById(R.id.preachText);
        eventText=findViewById(R.id.eventText);
        mAuth=FirebaseAuth.getInstance();
        fstore=FirebaseFirestore.getInstance();
        Dialog me=new Dialog(MainActivity.this);
        rotateOpen = AnimationUtils.loadAnimation(this,R.anim.rotate_open_anim);
        rotateclose =AnimationUtils.loadAnimation(this,R.anim.roatate_close_anim);
        tobuttom = AnimationUtils.loadAnimation(this,R.anim.to_buttom);
        frombuttom = AnimationUtils.loadAnimation(this,R.anim.from_buttom);
        MyFunctions myFunctions = new MyFunctions(MainActivity.this);
        myFunctions.getBibleVerse( new MyFunctions.VolleyResponseListener() {
            @Override
            public void onError(String message) {
                Toast.makeText(MainActivity.this, "Someting Wrong", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(JSONArray response) {
                try {
                    JSONObject jsonObject = response.getJSONObject(0);
                    chapter.setText(jsonObject.getString("bookname")+" "+jsonObject.getString("chapter")+":"+jsonObject.getString("verse"));
                    verse.setText(jsonObject.getString("text"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animate();
            }
        });
        event_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                me.eventDialog(MainActivity.this, new Dialog.eventResponse() {
                    @Override
                    public void onResponse(Map response) {
                        userId=mAuth.getCurrentUser().getUid();
                        DocumentReference documentReference=fstore.collection("Events").document(userId);
                        documentReference.set(response).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(MainActivity.this, "thank You", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(Exception e) {
                                Toast.makeText(MainActivity.this, "Sorry", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });
        pray_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                me.prayDialog(MainActivity.this, new Dialog.prayResponse() {
                    @Override
                    public void onResponse(Map response) {
                        userId=mAuth.getCurrentUser().getUid();
                        DocumentReference documentReference=fstore.collection("Prayers").document(userId);
                        documentReference.set(response).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(MainActivity.this, "God Bless You", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(Exception e) {
                                Toast.makeText(MainActivity.this, "Jesus is With You", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });
        preach_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                me.preachDialog(MainActivity.this, new Dialog.preachResponse() {
                    @Override
                    public void onResponse(Map response) {
                        userId=mAuth.getCurrentUser().getUid();
                        DocumentReference documentReference = fstore.collection("Preachs").document(userId);
                        documentReference.set(response).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(MainActivity.this, "Thank For Preach", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure( Exception e) {
                                Toast.makeText(MainActivity.this, "Jesus is With You", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.action_logout){
            auth.signOut();
            startActivity(new Intent(MainActivity.this,Signing.class));
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
    public void animate(){
        if(isopen){
            add_button.startAnimation(rotateclose);
            event_button.startAnimation(tobuttom);
            eventText.startAnimation(tobuttom);
            event_button.setClickable(false);
            preach_button.startAnimation(tobuttom);
            preachText.startAnimation(tobuttom);
            preach_button.setClickable(false);
            pray_button.startAnimation(tobuttom);
            prayText.startAnimation(tobuttom);
            pray_button.setClickable(false);
            isopen=false;
        }else {
            add_button.startAnimation(rotateOpen);
            event_button.startAnimation(frombuttom);
            eventText.startAnimation(frombuttom);
            event_button.setClickable(true);
            preach_button.startAnimation(frombuttom);
            preachText.startAnimation(frombuttom);
            preach_button.setClickable(true);
            pray_button.startAnimation(frombuttom);
            prayText.startAnimation(frombuttom);
            pray_button.setClickable(true);
            isopen = true;
        }

    }
}
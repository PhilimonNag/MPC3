package com.philimonnag.mpc;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import org.jitsi.meet.sdk.BroadcastEvent;
import org.jitsi.meet.sdk.BroadcastIntentHelper;
import org.jitsi.meet.sdk.JitsiMeet;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    private TextView chapter,verse,name;
    private Button  event_button, pray_button, preach_button;

    FirebaseAuth mAuth;
    FirebaseFirestore fstore;
    DocumentReference documentReference;
    String userId;
    ImageView pImg;
    String url;
    private RecyclerView courseRV,prayerItems,eventitems;
    private ArrayList<PreachModel> preachModelArrayList;
    private ArrayList<PrayerModel>prayerModelArrayList;
    private ArrayList<EventModel>eventModelArrayList;
    private PreachAdapter preachAdapter;
    private PrayerAdapter prayerAdapter;
    private EventAdapter eventAdapter;
    private FirebaseFirestore db;
     String Im;
     SimpleDateFormat time;
     String timeStamp,share;
     Button send;
     FirebaseAuth auth;
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            onBroadcastReceived(intent);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();
        event_button = findViewById(R.id.event_button);
        pray_button = findViewById(R.id.pray_button);
        preach_button = findViewById(R.id.preach_button);
        chapter= findViewById(R.id.chapter);
        verse =findViewById(R.id.verse);
        name=findViewById(R.id.name);
        pImg=findViewById(R.id.profileImg);
        FirebaseMessaging.getInstance().subscribeToTopic("all");
        time=new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
        timeStamp=time.format(new Date());
        // initializing our variables.
        courseRV = findViewById(R.id.idRVItems);
        prayerItems=findViewById(R.id.prayerItems);
        eventitems=findViewById(R.id.eventItems);
        send=findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,share);
                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);
            }
        });

        // initializing our variable for firebase 
        // firestore and getting its instance.
        db = FirebaseFirestore.getInstance();

        // creating our new array list
        preachModelArrayList = new ArrayList<>();
        prayerModelArrayList=new ArrayList<>();
        eventModelArrayList=new ArrayList<>();
        courseRV.setHasFixedSize(true);
        prayerItems.setHasFixedSize(true);
        eventitems.setHasFixedSize(true);

        // adding horizontal layout manager for our recycler view.
        courseRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        prayerItems.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        eventitems.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));

        // adding our array list to our recycler view adapter class.
        preachAdapter = new PreachAdapter(preachModelArrayList, this);
        prayerAdapter=new PrayerAdapter(prayerModelArrayList,this);
        eventAdapter=new EventAdapter(eventModelArrayList,this);

        // setting adapter to our recycler view.
        courseRV.setAdapter(preachAdapter);
        prayerItems.setAdapter(prayerAdapter);
        eventitems.setAdapter(eventAdapter);

        loadrecyclerViewData();
        loadprayerViewData();
        loadeventViewData();

        mAuth=FirebaseAuth.getInstance();
        userId=mAuth.getCurrentUser().getUid();
        fstore=FirebaseFirestore.getInstance();
        documentReference = fstore.collection("user").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                name.setText(value.getString("uName"));
                Picasso.get().load(value.getString("url")).into(pImg);
                url=value.getString("url");
                Im=value.getString("uName");
            }
        });
        Dialog me=new Dialog(MainActivity.this);
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
                    share=jsonObject.getString("bookname")+" "+jsonObject.getString("chapter")+":"+jsonObject.getString("verse")+"\n"+jsonObject.getString("text");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        URL serverURL;
        try {
            // When using JaaS, replace "https://meet.jit.si" with the proper serverURL
            serverURL = new URL("https://meet.jit.si");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new RuntimeException("Invalid server URL!");
        }
        JitsiMeetConferenceOptions defaultOptions
                = new JitsiMeetConferenceOptions.Builder()
                .setServerURL(serverURL)
                // When using JaaS, set the obtained JWT here
                //.setToken("MyJWT")
                // Different features flags can be set
                // .setFeatureFlag("toolbox.enabled", false)
                // .setFeatureFlag("filmstrip.enabled", false)
                .setWelcomePageEnabled(false)
                .build();
        JitsiMeet.setDefaultConferenceOptions(defaultOptions);

        registerForBroadcastMessages();
        event_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                me.eventDialog(MainActivity.this, new Dialog.eventResponse() {
                    @Override
                    public void onResponse(Map response) {
                        JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
                                .setRoom(String.valueOf(response.get("where"))).setWelcomePageEnabled(false)
                                .build();
                        JitsiMeetActivity.launch(MainActivity.this,options);

                        DocumentReference documentReference=fstore.collection("Events").document();
                        response.put("url",url);
                        response.put("uName",Im);
                        response.put("timeStamp",timeStamp);
                        documentReference.set(response).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                FcmNotificationsSender fcmNotificationsSender= new FcmNotificationsSender("/topics/all",response.get("when").toString(),
                                        response.get("where").toString(),getApplicationContext(),MainActivity.this);
                                fcmNotificationsSender.SendNotifications();
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
                        DocumentReference documentReference=fstore.collection("Prayers").document();
                        response.put("url",url);
                        response.put("uName",Im);
                        response.put("timeStamp",timeStamp);
                        documentReference.set(response).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                FcmNotificationsSender fcmNotificationsSender= new FcmNotificationsSender("/topics/all",response.get("forwho").toString(),
                                        response.get("request").toString(),getApplicationContext(),MainActivity.this);
                                fcmNotificationsSender.SendNotifications();

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
                        DocumentReference documentReference = fstore.collection("Preachs").document();
                        response.put("url",url);
                        response.put("uName",Im);
                        response.put("timeStamp",timeStamp);
                        documentReference.set(response).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                preachAdapter.notifyDataSetChanged();
                                FcmNotificationsSender fcmNotificationsSender= new FcmNotificationsSender("/topics/all",response.get("uName").toString(),
                                        response.get("preach").toString(),getApplicationContext(),MainActivity.this);
                                fcmNotificationsSender.SendNotifications();
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

    private void loadeventViewData() {
        db.collection("Events").orderBy("timeStamp",Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(!queryDocumentSnapshots.isEmpty()){
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list) {
                              EventModel eventModel=d.toObject(EventModel.class);
                              eventModelArrayList.add(eventModel);
                            }
                            eventAdapter.notifyDataSetChanged();

                        }else {
                            Toast.makeText(MainActivity.this, "No Events data found in Database", Toast.LENGTH_SHORT).show();
                        }
                    }

                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull  Exception e) {
                Toast.makeText(MainActivity.this, "Fail to get the Event data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadprayerViewData() {
        db.collection("Prayers").orderBy("timeStamp",Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(!queryDocumentSnapshots.isEmpty()){
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list) {
                                PrayerModel prayerModel=d.toObject(PrayerModel.class);
                                prayerModelArrayList.add(prayerModel);
                            }
                            prayerAdapter.notifyDataSetChanged();

                        }else {
                            Toast.makeText(MainActivity.this, "No Prayer data found in Database", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Fail to get the Prayer data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadrecyclerViewData() {
        db.collection("Preachs").orderBy("timeStamp",Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // after getting the data we are calling on success method
                        // and inside this method we are checking if the received
                        // query snapshot is empty or not.
                        if (!queryDocumentSnapshots.isEmpty()) {
                            // if the snapshot is not empty we are hiding our
                            // progress bar and adding our data in a list.
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list) {
                                // after getting this list we are passing that
                                // list to our object class.
                                PreachModel preachModel = d.toObject(PreachModel.class);

                                // and we will pass this object class
                                // inside our arraylist which we have
                                // created for recycler view.
                                preachModelArrayList.add(preachModel);
                            }
                            // after adding the data to recycler view.
                            // we are calling recycler view notifyDataSetChanged
                            // method to notify that data has been changed in recycler view.
                            preachAdapter.notifyDataSetChanged();
                        } else {
                            // if the snapshot is empty we are
                            // displaying a toast message.
                            Toast.makeText(MainActivity.this, "No Preach data found in Database", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // if we do not get any data or any error we are displaying
                // a toast message that we do not get any data
                Toast.makeText(MainActivity.this, "Fail to get Preach the data.", Toast.LENGTH_SHORT).show();
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
        auth.signOut();
        startActivity(new Intent(MainActivity.this,Signing.class));
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);

        super.onDestroy();
    }
    private void registerForBroadcastMessages() {
        IntentFilter intentFilter = new IntentFilter();

        /* This registers for every possible event sent from JitsiMeetSDK
           If only some of the events are needed, the for loop can be replaced
           with individual statements:
           ex:  intentFilter.addAction(BroadcastEvent.Type.AUDIO_MUTED_CHANGED.getAction());
                intentFilter.addAction(BroadcastEvent.Type.CONFERENCE_TERMINATED.getAction());
                ... other events
         */
        for (BroadcastEvent.Type type : BroadcastEvent.Type.values()) {
            intentFilter.addAction(type.getAction());
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, intentFilter);
    }
    private void onBroadcastReceived(Intent intent) {
        if (intent != null) {
            BroadcastEvent event = new BroadcastEvent(intent);
              switch (event.getType()) {
                case CONFERENCE_JOINED:
                    Timber.i("Conference Joined with url%s", event.getData().get("url"));
                    break;
                case PARTICIPANT_JOINED:
                    Timber.i("Participant joined%s", event.getData().get("name"));
                    break;
            }
        }
    }
    // Example for sending actions to JitsiMeetSDK
    private void hangUp() {
        Intent hangupBroadcastIntent = BroadcastIntentHelper.buildHangUpIntent();
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(hangupBroadcastIntent);
    }
}
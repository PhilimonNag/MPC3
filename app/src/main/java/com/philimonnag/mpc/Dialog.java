package com.philimonnag.mpc;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Dialog {
    FirebaseAuth mAuth;
    FirebaseFirestore fstore;
    String userId;

    public FirebaseAuth getmAuth() {
        return mAuth;
    }

    public FirebaseFirestore getFstore() {
        return fstore;
    }

    public void setFstore(FirebaseFirestore fstore) {
        this.fstore = fstore;
    }

    Context context;

    public Dialog(Context context) {

    }
    public interface eventResponse {
        void onResponse(Map response);
    }
    protected void eventDialog(Context context,eventResponse eventResponse){
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        View view =layoutInflater.inflate(R.layout.add_event,null);
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setView(view);
        final TextInputLayout event= view.findViewById(R.id.Add_event);
        final TextInputLayout description=view.findViewById(R.id.Add_description);
        builder.setCancelable(false)
                .setPositiveButton("Amen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                       String when = event.getEditText().getText().toString();
                       String where= description.getEditText().getText().toString();
                        Map<String,Object> eventz=new HashMap<>();
                        eventz.put("when",when);
                        eventz.put("where",where);
                       eventResponse.onResponse(eventz);

                   }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }    public interface prayResponse {
        void onResponse(Map response);
    }

    protected void prayDialog(Context context, prayResponse preachResponse){
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        View view =layoutInflater.inflate(R.layout.add_prayer,null);
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setView(view);
        final TextInputLayout forwhom= view.findViewById(R.id.prayer_for);
        final TextInputLayout requestfor=view.findViewById(R.id.prayerRequest);
        builder.setCancelable(false)
                .setPositiveButton("Amen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String who = forwhom.getEditText().getText().toString();
                        String request= requestfor.getEditText().getText().toString();
                        Map<String,Object> prayer=new HashMap<>();
                        prayer.put("forwho",who);
                        prayer.put("request",request);
                        preachResponse.onResponse(prayer);

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    public interface preachResponse {
        void onResponse(Map response);
    }
    protected void preachDialog(Context context,preachResponse preachResponse){
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        View view =layoutInflater.inflate(R.layout.add_preach,null);
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setView(view);
        final TextInputLayout preachher= view.findViewById(R.id.preach_for);
        builder.setCancelable(false)
                .setPositiveButton("Amen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String preach=preachher.getEditText().getText().toString();
                        Map<String,Object> preachs= new HashMap<>();
                        preachs.put("preach",preach);
                        preachResponse.onResponse(preachs);

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}

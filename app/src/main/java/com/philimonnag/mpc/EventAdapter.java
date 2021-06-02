package com.philimonnag.mpc;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.jitsi.meet.sdk.JitsiMeet;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {
    private ArrayList<EventModel> eventModelArrayList;
    private Context context;

    // constructor class for our Adapter
    public EventAdapter(ArrayList<EventModel> eventModelArrayList, Context context) {
        this.eventModelArrayList = eventModelArrayList;
        this.context = context;
    }


    @Override
    public EventAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // passing our layout file for displaying our card item
        return new EventAdapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.event_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull EventAdapter.ViewHolder holder, int position) {
        // setting data to our views in Recycler view items.
       EventModel model = eventModelArrayList.get(position);
       holder.myName.setText(model.getuName());
        holder.when.setText(model.getWhen());
        holder.where.setText(model.getWhere());
        // we are using Picasso to load images
        // from URL inside our image view.
        Picasso.get().load(model.getUrl()).into(holder.proImg);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                URL serverUrl;
                try {
                    serverUrl = new URL("https://meet.jit.si");
                    JitsiMeetConferenceOptions defaultOperation=
                            new JitsiMeetConferenceOptions.Builder()
                                    .setServerURL(serverUrl)
                                    .setWelcomePageEnabled(false).build();
                    JitsiMeet.setDefaultConferenceOptions(defaultOperation);

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
                        .setRoom(String.valueOf(model.getWhere())).setWelcomePageEnabled(false)
                        .build();
                JitsiMeetActivity.launch(context,options);
                // setting on click listener
                // for our items of recycler items.
//                Intent i = new Intent(Intent.ACTION_VIEW);
//                i.setData(Uri.parse(model.getWhere()));
//                v.getContext().startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        // returning the size of array list.
        return eventModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView myName,where,when;
        private ImageView proImg;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing the views of recycler views.
            myName = itemView.findViewById(R.id.myName);
            when =itemView.findViewById(R.id.when_text);
            where=itemView.findViewById(R.id.where_text);
            proImg = itemView.findViewById(R.id.proImg);

        }
    }

}

package com.philimonnag.mpc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PrayerAdapter extends RecyclerView.Adapter<PrayerAdapter.ViewHolder> {
    private ArrayList<PrayerModel> prayerModelArrayList;
    private Context context;

    // constructor class for our Adapter
    public PrayerAdapter(ArrayList<PrayerModel> prayerModelArrayList, Context context) {
        this.prayerModelArrayList = prayerModelArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public PrayerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // passing our layout file for displaying our card item
        return new PrayerAdapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.prayer_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PrayerAdapter.ViewHolder holder, int position) {
        // setting data to our views in Recycler view items.
        PrayerModel modal = prayerModelArrayList.get(position);
        holder.userName.setText(modal.getuName());
        holder.forwhom.setText(modal.getForwho());
        holder.requestFor.setText(modal.getRequest());
        // we are using Picasso to load images
        // from URL inside our image view.
        Picasso.get().load(modal.getUrl()).into(holder.pImg);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // setting on click listener
                // for our items of recycler items.
                Toast.makeText(context, "Clicked item is " + modal.getuName(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        // returning the size of array list.
        return prayerModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView userName,forwhom,requestFor;
        private ImageView pImg;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing the views of recycler views.
            userName = itemView.findViewById(R.id.userName);
            forwhom =itemView.findViewById(R.id.forwhom);
            requestFor=itemView.findViewById(R.id.requestFor);
            pImg = itemView.findViewById(R.id.pImg);

        }
    }

}

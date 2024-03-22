package com.example.weatherapp.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.weatherapp.Domains.FutureDomain;
import com.example.weatherapp.Domains.Hourly;
import com.example.weatherapp.R;

import java.util.ArrayList;
import java.util.List;

public class FutureAdapter extends RecyclerView.Adapter<FutureAdapter.viewholder> {
    ArrayList<FutureDomain> items;
    Context context;

    public FutureAdapter(List<FutureDomain> items) {
        this.items = new ArrayList<>(items);
    }

    @NonNull
    @Override
    public FutureAdapter.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_future,parent, false);
        return new viewholder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull FutureAdapter.viewholder holder, int position) {
        holder.dayTxt.setText(items.get(position).getDay());
        holder.statusTxt.setText(items.get(position).getStatus());
        holder.tempTxt.setText(items.get(position).getTemp() + "°C");

        int drawableResourceId = holder.itemView.getResources()
                .getIdentifier(items.get(position).getPicPath(), "drawable", holder.itemView.getContext().getOpPackageName());


        try {
            Glide.with(context)
                    .load(items.get(position).getPicPath())
                    .into(holder.pic);
        } catch (Exception e) {
            Log.e("HourlyAdapter", "Error loading image with Glide: " + e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class viewholder extends RecyclerView.ViewHolder{
        TextView dayTxt, statusTxt, tempTxt;
        ImageView pic;
        public viewholder(@NonNull View itemView) {
            super(itemView);

            dayTxt = itemView.findViewById(R.id.dayTxt);
            statusTxt = itemView.findViewById(R.id.statusTxt);
            tempTxt = itemView.findViewById(R.id.tempTxt);

            pic = itemView.findViewById(R.id.pic);


        }
    }
}
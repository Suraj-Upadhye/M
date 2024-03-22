package com.example.weatherapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.Log;



import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.weatherapp.Domains.Hourly;
import com.example.weatherapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class HourlyAdapters extends RecyclerView.Adapter<HourlyAdapters.viewholder> {
    private ArrayList<Hourly> items;
    private Context context;

    public HourlyAdapters(List<Hourly> items) {
        this.items = new ArrayList<>(items);
    }


    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_hourly, parent, false);
        return new viewholder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {
        Hourly hourly = items.get(position);
        holder.hourTxt.setText(hourly.getHour());
        holder.tempTxt.setText(hourly.getTemp());

        int drawableResourceId = holder.itemView.getResources().getIdentifier(hourly.getPicPath(), "drawable", holder.itemView.getContext().getPackageName());


        try {
            Glide.with(context)
                    .load(hourly.getPicPath())
                    .into(holder.pic);
        } catch (Exception e) {
            Log.e("HourlyAdapter", "Error loading image with Glide: " + e.getMessage());
        }


    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {
        TextView hourTxt, tempTxt;
        ImageView pic;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            hourTxt = itemView.findViewById(R.id.hourTxt);
            tempTxt = itemView.findViewById(R.id.tempTxt);
            pic = itemView.findViewById(R.id.pic);
        }
    }
}

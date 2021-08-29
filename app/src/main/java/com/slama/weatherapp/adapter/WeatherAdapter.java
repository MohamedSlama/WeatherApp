package com.slama.weatherapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.slama.weatherapp.R;
import com.slama.weatherapp.model.WeatherAdapterModel;

import java.util.ArrayList;
import java.util.List;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder> {

    private static final String TAG = "WeatherAdapterTag";
    List<WeatherAdapterModel> list = new ArrayList<>();
    Context context;

    @NonNull
    @Override
    public WeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        return new WeatherViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherViewHolder holder, int position) {
//        Log.i(TAG, String.format("min:%s ,max:%s",list.get(position).getMinTemp(),list.get(position).getMaxTemp()));
        holder.weekName.setText(list.get(position).getWeekName());
        holder.minTemp.setText(list.get(position).getMinTemp());
        holder.maxTemp.setText(list.get(position).getMaxTemp());
        Glide.with(context).load(list.get(position).getWeatherIcon()).into(holder.weatherImage);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setList(List<WeatherAdapterModel> list) {
        this.list = list;
        notifyDataSetChanged();
    }


    static class WeatherViewHolder extends RecyclerView.ViewHolder {
        ImageView weatherImage;
        TextView weekName;
        TextView minTemp;
        TextView maxTemp;

        public WeatherViewHolder(@NonNull View itemView) {
            super(itemView);

            weatherImage = itemView.findViewById(R.id.item_weather_image);
            weekName = itemView.findViewById(R.id.item_week_name);
            minTemp = itemView.findViewById(R.id.item_weather_min_temp);
            maxTemp = itemView.findViewById(R.id.item_weather_max_temp);
        }
    }
}

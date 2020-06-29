package com.uzair.smarttravelmanagement.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.uzair.smarttravelmanagement.Models.WeatherForecastResult;
import com.uzair.smarttravelmanagement.R;
import com.uzair.smarttravelmanagement.WeatherModule.Common.Common;
import com.squareup.picasso.Picasso;


public class WeatherForecastAdapter extends RecyclerView.Adapter<WeatherForecastAdapter.MyViewHolder> {

    Context context;
    WeatherForecastResult weatherForecastResult;

    public WeatherForecastAdapter(Context context, WeatherForecastResult weatherForecastResult) {
        this.context = context;
        this.weatherForecastResult = weatherForecastResult;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.weather_forecast_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Picasso.get().load(new StringBuilder("https://openweathermap.org/img/w/")
                .append(weatherForecastResult.list.get(position).weather.get(0).getIcon())
                .append(".png").toString()).into(holder.imgWeather);

        holder.txtDate.setText(new StringBuilder(Common.convertUnixToDate(weatherForecastResult.list.get(position).dt)));

        holder.txtDescription.setText(new StringBuilder(weatherForecastResult.list.get(position).weather.get(0).getDescription()));

        holder.txtTemperature.setText(new StringBuilder(String.valueOf(weatherForecastResult.list.get(position).main.getTemp())).append("°C").toString());
    }

    @Override
    public int getItemCount() {
        return weatherForecastResult.list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtDate, txtDescription, txtTemperature;
        ImageView imgWeather;

        public MyViewHolder(View itemView) {
            super(itemView);

            txtDate = itemView.findViewById(R.id.forecast_row_txt_date);
            txtDescription = itemView.findViewById(R.id.forecast_row_txt_description);
            txtTemperature = itemView.findViewById(R.id.forecast_row_txt_temperature);
            imgWeather = itemView.findViewById(R.id.forecast_row_img_weather);
        }
    }

}

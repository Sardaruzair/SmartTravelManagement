package com.uzair.smarttravelmanagement.WeatherModule.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.uzair.smarttravelmanagement.Models.WeatherResult;
import com.uzair.smarttravelmanagement.R;
import com.uzair.smarttravelmanagement.WeatherModule.Common.Common;
import com.uzair.smarttravelmanagement.WeatherModule.Retrofit.IOpenWeatherMap;
import com.uzair.smarttravelmanagement.WeatherModule.Retrofit.RetrofitClient;
import com.squareup.picasso.Picasso;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;


public class TodayWeatherFragment extends Fragment {

    private static ImageView img_weather;
    private static TextView txtCityName, txtHumidity, txtSunrise, txtSunset, txtPressure, txtTemperature, txtDescription, txtDateTime, txtWind, txtGeoCoord;

    private static LinearLayout weatherPanel;
    private static ProgressBar loading;

    private static CompositeDisposable compositeDisposable;
    private static IOpenWeatherMap mService;

    String cityName = "Islamabad,pk";

    static TodayWeatherFragment instance;

    public static TodayWeatherFragment getInstance() {
        if (instance == null) {
            instance = new TodayWeatherFragment();
        }
        return instance;
    }

    public TodayWeatherFragment() {
        compositeDisposable = new CompositeDisposable();
        Retrofit retrofit = RetrofitClient.getInstance();
        mService = retrofit.create(IOpenWeatherMap.class);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View itemView = inflater.inflate(R.layout.fragment_today_weather, container, false);
        img_weather = itemView.findViewById(R.id.img_weather);
        txtCityName = itemView.findViewById(R.id.txt_city_name);
        txtHumidity = itemView.findViewById(R.id.txt_humidity);
        txtSunrise = itemView.findViewById(R.id.txt_sunrise);
        txtSunset = itemView.findViewById(R.id.txt_sunset);
        txtPressure = itemView.findViewById(R.id.txt_pressure);
        txtTemperature = itemView.findViewById(R.id.txt_temperature);
        txtDescription = itemView.findViewById(R.id.txt_description);
        txtDateTime = itemView.findViewById(R.id.txt_date_time);
        txtWind = itemView.findViewById(R.id.txt_wind);
        txtGeoCoord = itemView.findViewById(R.id.txt_geo_coord);

        weatherPanel = itemView.findViewById(R.id.weather_panel);
        loading = itemView.findViewById(R.id.loading);

        getWeatherInformation();

        return itemView;
    }

    public static void getWeatherInformation() {

        compositeDisposable.add(mService.getWeatherByCityName(Common.cityName,
                Common.OWAPI_KEY,
                "metric")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new io.reactivex.functions.Consumer<WeatherResult>() {
                    @Override
                    public void accept(WeatherResult weatherResult) {
                        Picasso.get().load(new StringBuilder("https://openweathermap.org/img/w/")
                                .append(weatherResult.getWeather().get(0).getIcon())
                                .append(".png").toString()).into(img_weather);
                        txtCityName.setText(weatherResult.getName());
                        txtDescription.setText(new StringBuilder("Weather in ")
                                .append(weatherResult.getName()).toString());
                        txtTemperature.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getTemp())).append("°C").toString());


                        txtDateTime.setText(Common.convertUnixToDate(weatherResult.getDt()));
                        txtPressure.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getPressure())).append("hpa").toString());
                        txtHumidity.setText(new StringBuilder(String.valueOf(String.valueOf(weatherResult.getMain().getHumidity()))).append("%").toString());
                        txtSunrise.setText(Common.convertUnixToHour(weatherResult.getSys().getSunrise()));
                        txtSunset.setText(Common.convertUnixToHour(weatherResult.getSys().getSunset()));
                        txtGeoCoord.setText(new StringBuilder("[").append(weatherResult.getCoord().toString()).append("]").toString());

                        weatherPanel.setVisibility(View.VISIBLE);
                        loading.setVisibility(View.GONE);
                    }
                }, new io.reactivex.functions.Consumer<Throwable>() {

                    @Override
                    public void accept(Throwable throwable) {
                        throwable.printStackTrace();
                        Log.e("Location", "accept: " + throwable.getCause());
                        Toast.makeText(instance.getActivity(), "" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
        );


//        compositeDisposable.add(mService.getWeatherByLatLng(String.valueOf(Common.current_location.getLatitude()),
//                String.valueOf(Common.current_location.getLongitude()),
//                Common.OWAPI_KEY,
//                "metric")
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new io.reactivex.functions.Consumer<WeatherResult>() {
//                    @Override
//                    public void accept(WeatherResult weatherResult) {
//                        Picasso.get().load(new StringBuilder("https://openweathermap.org/img/w/")
//                                .append(weatherResult.getWeather().get(0).getIcon())
//                                .append(".png").toString()).into(img_weather);
//                        txtCityName.setText(weatherResult.getName());
//                        txtDescription.setText(new StringBuilder("Weather in ")
//                                .append(weatherResult.getName()).toString());
//                        txtTemperature.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getTemp())).append("°C").toString());
//
//
//
//                        txtDateTime.setText(Common.convertUnixToDate(weatherResult.getDt()));
//                        txtPressure.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getPressure())).append("hpa").toString());
//                        txtHumidity.setText(new StringBuilder(String.valueOf(String.valueOf(weatherResult.getMain().getHumidity()))).append("%").toString());
//                        txtSunrise.setText(Common.convertUnixToHour(weatherResult.getSys().getSunrise()));
//                        txtSunset.setText(Common.convertUnixToHour(weatherResult.getSys().getSunset()));
//                        txtGeoCoord.setText(new StringBuilder("[").append(weatherResult.getCoord().toString()).append("]").toString());
//
//                        weatherPanel.setVisibility(View.VISIBLE);
//                        loading.setVisibility(View.GONE);
//                    }
//                }, new io.reactivex.functions.Consumer<Throwable>() {
//
//                    @Override
//                    public void accept(Throwable throwable) {
//                        throwable.printStackTrace();
//                        Log.e("Location", "accept: " + throwable.getCause() );
//                        Toast.makeText(getActivity(), "" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                })
//        );
    }
}
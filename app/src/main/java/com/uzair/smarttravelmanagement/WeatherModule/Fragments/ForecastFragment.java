package com.uzair.smarttravelmanagement.WeatherModule.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.uzair.smarttravelmanagement.Adapters.WeatherForecastAdapter;
import com.uzair.smarttravelmanagement.Models.WeatherForecastResult;
import com.uzair.smarttravelmanagement.R;
import com.uzair.smarttravelmanagement.WeatherModule.Common.Common;
import com.uzair.smarttravelmanagement.WeatherModule.Retrofit.IOpenWeatherMap;
import com.uzair.smarttravelmanagement.WeatherModule.Retrofit.RetrofitClient;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class ForecastFragment extends Fragment {

    static CompositeDisposable compositeDisposable;
    static IOpenWeatherMap mService;

    static TextView txtCityName, txtGeoCoord;
    static RecyclerView recyclerForecast;

    static ForecastFragment instance;

    private String cityName = "Islamabad,pk";

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public static ForecastFragment getInstance() {
        if (instance == null)
            instance = new ForecastFragment();
        return instance;
    }

    public ForecastFragment() {
        compositeDisposable = new CompositeDisposable();
        Retrofit retrofit = RetrofitClient.getInstance();
        mService = retrofit.create(IOpenWeatherMap.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View itemView = inflater.inflate(R.layout.fragment_forecast, container, false);
        txtCityName = itemView.findViewById(R.id.fragment_forecast_txt_city_name);
        txtGeoCoord = itemView.findViewById(R.id.fragment_forecast_txt_geo_coord);

        recyclerForecast = itemView.findViewById(R.id.recycler_forecast);
        recyclerForecast.setHasFixedSize(true);
        recyclerForecast.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        getForecastWeatherInformation();

        return itemView;
    }

    public static void getForecastWeatherInformation() {

        compositeDisposable.add(mService.getForecastWeatherByCityName(
                Common.cityName,
                Common.OWAPI_KEY,
                "metric")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WeatherForecastResult>() {
                    @Override
                    public void accept(WeatherForecastResult weatherForecastResult) throws Exception {
                        displayForecastWeather(weatherForecastResult);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.d("ERROR", "" + throwable.getMessage());
                    }
                })
        );

//        compositeDisposable.add(mService.getForecastWeatherByLatLng(
//                String.valueOf(Common.current_location.getLatitude()),
//                String.valueOf(Common.current_location.getLongitude()),
//                Common.OWAPI_KEY,
//                "metric")
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Consumer<WeatherForecastResult>() {
//                    @Override
//                    public void accept(WeatherForecastResult weatherForecastResult) throws Exception {
//                        displayForecastWeather(weatherForecastResult);
//                    }
//                }, new Consumer<Throwable>() {
//                    @Override
//                    public void accept(Throwable throwable) throws Exception {
//                        Log.d("ERROR", "" + throwable.getMessage());
//                    }
//                })
//        );
    }

    private static void displayForecastWeather(WeatherForecastResult weatherForecastResult) {
        txtCityName.setText(new StringBuilder(weatherForecastResult.city.name));
        txtGeoCoord.setText(new StringBuilder(weatherForecastResult.city.coord.toString()));

        WeatherForecastAdapter adapter = new WeatherForecastAdapter(instance.getContext(), weatherForecastResult);
        recyclerForecast.setAdapter(adapter);
    }

}

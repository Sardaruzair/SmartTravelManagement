package com.uzair.smarttravelmanagement.WeatherModule.Retrofit;

import com.uzair.smarttravelmanagement.Models.WeatherForecastResult;
import com.uzair.smarttravelmanagement.Models.WeatherResult;

import io.reactivex.Observable;

import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IOpenWeatherMap {
    @GET("weather")
    Observable<WeatherResult> getWeatherByLatLng(@Query("lat") String lat,
                                                 @Query("lon") String lng,
                                                 @Query("appid") String appid,
                                                 @Query("units") String unit);

//    https://samples.openweathermap.org/data/2.5/weather?q=Islamabad,pk&appid=b6907d289e10d714a6e88b30761fae22
//    https://samples.openweathermap.org/data/2.5/weather?lat=35&lon=139&appid=b6907d289e10d714a6e88b30761fae22

    @GET("weather")
    Observable<WeatherResult> getWeatherByCityName(@Query("q") String city,
                                                   @Query("appid") String appid,
                                                   @Query("units") String unit);

    @GET("forecast")
    Observable<WeatherForecastResult> getForecastWeatherByLatLng(@Query("lat") String lat,
                                                                 @Query("lon") String lng,
                                                                 @Query("appid") String appid,
                                                                 @Query("units") String unit);

    @GET("forecast")
    Observable<WeatherForecastResult> getForecastWeatherByCityName(@Query("q") String city,
                                                                 @Query("appid") String appid,
                                                                 @Query("units") String unit);
}

package com.slama.weatherapp.network;

import com.slama.weatherapp.model.WeatherResponse;

import io.reactivex.rxjava3.core.Observable;

import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ForecastApiService {
    @GET("forecast")
    Observable<WeatherResponse> getWeatherForecast(@Query("q") String cityname,
                                                   @Query("appid") String apikey);
}

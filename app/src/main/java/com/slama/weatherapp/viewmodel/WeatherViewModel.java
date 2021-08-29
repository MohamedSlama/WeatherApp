package com.slama.weatherapp.viewmodel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.slama.weatherapp.db.WeatherDB;
import com.slama.weatherapp.db.WeatherDao;
import com.slama.weatherapp.model.WeatherDetails;
import com.slama.weatherapp.model.WeatherForecast;
import com.slama.weatherapp.model.WeatherResponse;
import com.slama.weatherapp.module.RetrofitModule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class WeatherViewModel extends ViewModel {
    private static final String TAG = "WeatherViewModelTag";
    private MutableLiveData<ArrayList<WeatherDetails>> weatherList = new MutableLiveData<>();

    public MutableLiveData<ArrayList<WeatherDetails>> getWeatherList() {
        return weatherList;
    }


    public void initWeather(String city, Context context) {
        RetrofitModule.provideForecastApiService().getWeatherForecast(city, "ad737e77d9b062d894c7feb8b13b21c3")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<WeatherResponse, Object>() {
                    @Override
                    public ArrayList<WeatherDetails> apply(WeatherResponse weatherResponse) throws Throwable {
//                        Log.i(TAG, "Response is: " + weatherResponse.getCod());
//                        Log.i(TAG, "Size is: " + weatherResponse.getList().size());
                        List<WeatherForecast> weatherForecasts = weatherResponse.getList();
                        ArrayList<WeatherDetails> weatherDetailsList = new ArrayList<>();
                        for (WeatherForecast wf : weatherForecasts) {
//                            Log.i(TAG, String.format("min:%s ,max:%s",wf.getFmain().getTemp_min(),wf.getFmain().getTemp_max()));
                            WeatherDetails weatherDetails = new WeatherDetails(
                                    wf.getFmain().getTemp_min(),
                                    wf.getFmain().getTemp_max(),
                                    wf.getFmain().getHumidity(),
                                    wf.getFweather().get(0).getMain(),
                                    wf.getFwind().getSpeed(),
                                    wf.getDt_txt(),
                                    String.format("https://openweathermap.org/img/wn/%s.png", wf.getFweather().get(0).getIcon()),
                                    weatherResponse.getCity().getName()
                            );
                            weatherDetailsList.add(weatherDetails);
                        }
//                        Log.i(TAG, "Size is: " + weatherDetailsList.size());
                        return weatherDetailsList;
                    }
                })
                .subscribe(
                        r -> weatherList.setValue((ArrayList<WeatherDetails>) r),
                        e -> {
                            Log.e(TAG, "onCreate: " + e.getMessage());
                            if (e.getMessage().contains("404")) {
                                weatherList.setValue(new ArrayList<>());
                            }else if (e.getMessage().contains("400")){
                                Toast.makeText(context,"Check Internet Connection!!",Toast.LENGTH_LONG).show();
                            }else if (e.getMessage().contains("401")){
                                Toast.makeText(context,"Token Expired",Toast.LENGTH_LONG).show();
                            }else{
                                Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
                            }
                        }
                );
    }


    private MutableLiveData<List<WeatherDetails>> weatherListDB = new MutableLiveData<>();

    public MutableLiveData<List<WeatherDetails>> getWeatherListDB() {
        return weatherListDB;
    }

    public void initListDB(Single<List<WeatherDetails>> getWeatherList) {
//        weatherDetailsList = new ArrayList<>();
        getWeatherList.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<WeatherDetails>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        Log.i(TAG, "onSubscribe: ");
                    }

                    @Override
                    public void onSuccess(@NonNull List<WeatherDetails> weatherDetails) {
                        Log.i(TAG, "onSuccess: " + weatherDetails.size());
                        weatherListDB.setValue(weatherDetails);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e(TAG, "onError: " + e.getMessage());
                    }
                });
//        return weatherDetailsList;
    }
}

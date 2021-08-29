package com.slama.weatherapp.db;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.slama.weatherapp.model.WeatherDetails;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@Database(entities = WeatherDetails.class, version = 1, exportSchema = false)
public abstract class WeatherDB extends RoomDatabase {
    private static final String TAG = "WeatherDBTag";

    public abstract WeatherDao weatherDao();

    private static WeatherDB instance;

    public static synchronized WeatherDB getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), WeatherDB.class, "weather_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }

    public void insertWeather(WeatherDetails weatherDetails) {
        weatherDao().insertWeatherItem(weatherDetails)
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onComplete() {
//                        Log.i(TAG, "Saved To DB");
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e(TAG, "Error Saving to DB: " + e.getMessage());
                    }
                });
    }

//    private MutableLiveData<List<WeatherDetails>> weatherListDB = new MutableLiveData<>();
//
//    public MutableLiveData<List<WeatherDetails>> getWeatherListDB() {
//        return weatherListDB;
//    }
//
//    public void initListDB() {
////        weatherDetailsList = new ArrayList<>();
//        weatherDao().getWeatherList().subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new SingleObserver<ArrayList<WeatherDetails>>() {
//                    @Override
//                    public void onSubscribe(@NonNull Disposable d) {
//                        Log.i(TAG, "onSubscribe: ");
//                    }
//
//                    @Override
//                    public void onSuccess(@NonNull ArrayList<WeatherDetails> weatherDetails) {
//                        Log.i(TAG, "onSuccess: " + weatherDetails.size());
//                        weatherListDB.setValue(weatherDetails);
//                    }
//
//                    @Override
//                    public void onError(@NonNull Throwable e) {
//                        Log.e(TAG, "onError: " + e.getMessage());
//                    }
//                });
////        return weatherDetailsList;
//    }

    public void deleteAll() {
        weatherDao().deleteAll().subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                Log.i(TAG, "onSubscribe: ");
            }

            @Override
            public void onComplete() {
                Log.i(TAG, "onComplete: ");
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.e(TAG, "onError: " + e.getMessage());
            }
        });
    }

    public Single<List<WeatherDetails>> getWeatherList() {
        return weatherDao().getWeatherList();
    }
}

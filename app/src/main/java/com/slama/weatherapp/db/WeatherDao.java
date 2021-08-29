package com.slama.weatherapp.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.slama.weatherapp.model.WeatherDetails;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface WeatherDao {
    @Query("select * from weather_table")
    Single<List<WeatherDetails>> getWeatherList();

    @Insert
    Completable insertWeatherItem(WeatherDetails weatherDetails);

    @Query("select exists(select * from weather_table where dateTime =:key)")
    Single<List<String>> search(String key);

    @Query("delete from weather_table")
    Completable deleteAll();
}

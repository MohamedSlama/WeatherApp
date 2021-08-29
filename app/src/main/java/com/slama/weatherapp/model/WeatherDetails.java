package com.slama.weatherapp.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Calendar;

@Entity(tableName = "weather_table")
public class WeatherDetails {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "minTemp")
    private final double minTemp;
    @ColumnInfo(name = "maxTemp")
    private final double maxTemp;
    @ColumnInfo(name = "humidity")
    private final int humidity;
    @ColumnInfo(name = "weatherMain")
    private final String weatherMain;
    @ColumnInfo(name = "windSpeed")
    private final double windSpeed;
    @ColumnInfo(name = "dateTime")
    private final String dateTime;
    @ColumnInfo(name = "weatherIcon")
    private final String weatherIcon;
    @ColumnInfo(name = "cityName")
    private final String cityName;

    public WeatherDetails(double minTemp, double maxTemp, int humidity, String weatherMain, double windSpeed, String dateTime, String weatherIcon, String cityName) {
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.humidity = humidity;
        this.weatherMain = weatherMain;
        this.windSpeed = windSpeed;
        this.dateTime = dateTime;
        this.weatherIcon = weatherIcon;
        this.cityName = cityName;
    }

    public double getMinTemp() {
        return minTemp;
    }

    public double getMaxTemp() {
        return maxTemp;
    }

    public int getHumidity() {
        return humidity;
    }

    public String getWeatherMain() {
        return weatherMain;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getWeatherIcon() {
        return weatherIcon;
    }

    public String getCityName() {
        return cityName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

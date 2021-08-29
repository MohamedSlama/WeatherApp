package com.slama.weatherapp.model;

public class WeatherAdapterModel {
    String weekName;
    String minTemp;
    String maxTemp;
    String weatherIcon;

    public WeatherAdapterModel(String weekName, String minTemp, String maxTemp, String weatherIcon) {
        this.weekName = weekName;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.weatherIcon = weatherIcon;
    }

    public String getWeekName() {
        return weekName;
    }

    public String getMinTemp() {
        return minTemp;
    }

    public String getMaxTemp() {
        return maxTemp;
    }

    public String getWeatherIcon() {
        return weatherIcon;
    }
}

package com.slama.weatherapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WeatherForecast {
    @SerializedName("dt")
    @Expose
    private long dt;
    @SerializedName("main")
    @Expose
    private Fmain fmain;
    @SerializedName("weather")
    @Expose
    private List<Fweather> fweather;
    @SerializedName("clouds")
    @Expose
    private Fclouds fclouds;
    @SerializedName("wind")
    @Expose
    private Fwind fwind;
    @SerializedName("visibility")
    @Expose
    private int visibility;
    @SerializedName("pop")
    @Expose
    private int pop;
    @SerializedName("sys")
    @Expose
    private Fsys fsys;
    @SerializedName("dt_txt")
    @Expose
    private String dt_txt;

    public long getDt() {
        return dt;
    }

    public Fmain getFmain() {
        return fmain;
    }

    public List<Fweather> getFweather() {
        return fweather;
    }

    public Fclouds getFclouds() {
        return fclouds;
    }

    public Fwind getFwind() {
        return fwind;
    }

    public int getVisibility() {
        return visibility;
    }

    public int getPop() {
        return pop;
    }

    public Fsys getFsys() {
        return fsys;
    }

    public String getDt_txt() {
        return dt_txt;
    }

    public void setDt(long dt) {
        this.dt = dt;
    }

    public void setFmain(Fmain fmain) {
        this.fmain = fmain;
    }

    public void setFweather(List<Fweather> fweather) {
        this.fweather = fweather;
    }

    public void setFclouds(Fclouds fclouds) {
        this.fclouds = fclouds;
    }

    public void setFwind(Fwind fwind) {
        this.fwind = fwind;
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }

    public void setPop(int pop) {
        this.pop = pop;
    }

    public void setFsys(Fsys fsys) {
        this.fsys = fsys;
    }

    public void setDt_txt(String dt_txt) {
        this.dt_txt = dt_txt;
    }


}

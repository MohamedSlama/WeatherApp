package com.slama.weatherapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.snackbar.Snackbar;
import com.slama.weatherapp.adapter.WeatherAdapter;
import com.slama.weatherapp.databinding.ActivityMainBinding;
import com.slama.weatherapp.db.WeatherDB;
import com.slama.weatherapp.helper.Helper;
import com.slama.weatherapp.model.WeatherAdapterModel;
import com.slama.weatherapp.model.WeatherDetails;
import com.slama.weatherapp.viewmodel.WeatherViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivityTag";

    private ActivityMainBinding binding;
    WeatherViewModel weatherViewModel;
    WeatherAdapter adapter;
    WeatherDB weatherDB;

    List<WeatherDetails> sun = new ArrayList<>();
    List<WeatherDetails> mon = new ArrayList<>();
    List<WeatherDetails> tue = new ArrayList<>();
    List<WeatherDetails> wed = new ArrayList<>();
    List<WeatherDetails> thu = new ArrayList<>();
    List<WeatherDetails> fri = new ArrayList<>();
    List<WeatherDetails> sat = new ArrayList<>();

    @SuppressLint("WrongThread")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Data Binding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        // ActionBar Style
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar_style);

        // Init DB
        weatherDB = WeatherDB.getInstance(this);
        // Init View Model
        weatherViewModel = new ViewModelProvider(this).get(WeatherViewModel.class);

        // Init RecyclerView
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        adapter = new WeatherAdapter();
        binding.recyclerView.setAdapter(adapter);

        // Init City input
        binding.cityEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.cityEditText.setCursorVisible(true);
            }
        });
        binding.cityEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (Helper.isConnectingToInternet(getApplicationContext())) {
                        binding.loadingProgress.setVisibility(View.VISIBLE);
                        weatherViewModel.initWeather(String.valueOf(binding.cityEditText.getText()), getApplicationContext());
                    }
                    else {
                        Snackbar.make(binding.getRoot(), "Check Internet Connection!!", Snackbar.LENGTH_LONG).show();
                    }
                    Helper.hideKeyboard(MainActivity.this);
                    binding.cityEditText.setText("");
                    binding.cityEditText.setCursorVisible(false);
                    return true;
                } else
                    return false;
            }
        });


        // Init DB Call
        weatherViewModel.initListDB(weatherDB.getWeatherList());
        weatherViewModel.getWeatherListDB().observe(MainActivity.this, new Observer<List<WeatherDetails>>() {
            @Override
            public void onChanged(List<WeatherDetails> weatherDetails) {
                initView(new ArrayList<>(weatherDetails));
            }
        });


        // Init API CALL
        binding.loadingProgress.setVisibility(View.VISIBLE);
        weatherViewModel.getWeatherList()
                .observe(MainActivity.this, new Observer<ArrayList<WeatherDetails>>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onChanged(ArrayList<WeatherDetails> weatherDetails) {
                        initView(weatherDetails);
                    }
                });


        if (!Helper.isConnectingToInternet(getApplicationContext()))
            Snackbar.make(binding.getRoot(), "Check Internet Connection!!", Snackbar.LENGTH_LONG).show();


        // Init Line Chart
        initLineChart();

    }

    // UI Data
    private void initView(ArrayList<WeatherDetails> weatherDetails) {
        if (weatherDetails.isEmpty()) {
            binding.cityName.setText("Not Found");
            binding.cityName.setVisibility(View.VISIBLE);
            binding.loadingProgress.setVisibility(View.GONE);
            return;
        }
        weatherDB.deleteAll();

//                            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        binding.cityName.setText(weatherDetails.get(0).getCityName());
        binding.include.temperature.setText(Helper.convertToCelsius(weatherDetails.get(0).getMaxTemp()) + "");
        binding.include.minTemperature.setText(Helper.convertToCelsius(weatherDetails.get(0).getMinTemp()) + getString(R.string.celsius));
        binding.include.humidity.setText(weatherDetails.get(0).getHumidity() + " %");
        binding.include.wind.setText((int) Math.round(weatherDetails.get(0).getWindSpeed()) + " Km/h");
        binding.include.weatherMain.setText(weatherDetails.get(0).getWeatherMain());
        Glide.with(getApplicationContext())
                .load(weatherDetails.get(0).getWeatherIcon())
                .into(binding.include.weatherImage);

        binding.cityName.setVisibility(View.VISIBLE);
        binding.include.weatherCard.setVisibility(View.VISIBLE);

        ArrayList<Float> temps = new ArrayList<>();
        for (WeatherDetails wd : weatherDetails) {
//                                Log.i(TAG, String.format("min:%s ,max:%s",wd.getMinTemp(),wd.getMaxTemp()));
            weatherDB.insertWeather(wd);
            Calendar calendar = Helper.convertToCalender(wd.getDateTime());
            int day = calendar.get(Calendar.DAY_OF_WEEK);
            setWeatherList(day, wd);
            if (temps.size() < 8) {
                temps.add((float) Helper.convertToCelsius(wd.getMaxTemp()));
            }
        }
        drawLineChart(temps).subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread()).subscribe();
        adapter.setList(getDaysWeatherList());

        binding.loadingProgress.setVisibility(View.GONE);
    }

    // Line Chart Data
    private void initLineChart() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        Log.i(TAG, "HOUR_OF_DAY: " + hour);
        int index = getMyHourIndex(hour);
        List<Integer> indexs = shiftDays(index, 8);



        List<String> xAxisLables = new ArrayList<>();
        xAxisLables.add("12 AM");
        xAxisLables.add("3 AM");
        xAxisLables.add("6 AM");
        xAxisLables.add("9 AM");
        xAxisLables.add("12 PM");
        xAxisLables.add("3 PM");
        xAxisLables.add("6 PM");
        xAxisLables.add("9 PM");

        List<String> lables = new ArrayList<>();
        for (Integer i:indexs){
//            Log.i(TAG, "initLineChart: "+i + " & xAxisLables: "+xAxisLables.get(i-1));
            lables.add(xAxisLables.get(i-1));
        }

        binding.lineChart.setTouchEnabled(false);
        binding.lineChart.setClickable(false);
        binding.lineChart.setDoubleTapToZoomEnabled(false);
        binding.lineChart.setDrawBorders(false);
        binding.lineChart.setDrawGridBackground(false);
        binding.lineChart.getDescription().setEnabled(false);
        binding.lineChart.getLegend().setEnabled(false);
        binding.lineChart.getLegend().setEnabled(false);

        binding.lineChart.getAxisLeft().setDrawGridLines(false);
        binding.lineChart.getAxisLeft().setDrawLabels(false);
        binding.lineChart.getAxisLeft().setDrawAxisLine(false);

        binding.lineChart.getXAxis().setDrawGridLines(false);
        binding.lineChart.getXAxis().setDrawAxisLine(false);
        binding.lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        binding.lineChart.getAxisRight().setDrawGridLines(false);
        binding.lineChart.getAxisRight().setDrawLabels(false);
        binding.lineChart.getAxisRight().setDrawAxisLine(false);

        binding.lineChart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int temp =(int) value + 1;
                if (temp == lables.size())
                    temp = 0;
                return lables.get(temp);
            }
        });

    }

    private Observable drawLineChart(ArrayList<Float> yValues) {
        return Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> emitter) throws Throwable {
                ArrayList<Entry> yData = new ArrayList<>();
                for (int i = 0; i < yValues.size(); i++) {
                    yData.add(new Entry(i, yValues.get(i)));
                }
                LineDataSet lineDataSet = new LineDataSet(yData, getString(R.string.app_name));
                lineDataSet.setColors(Color.rgb(255, 244, 125));
                lineDataSet.setDrawCircles(false);
                lineDataSet.setDrawFilled(true);
                lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                Drawable lineDrawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.linechart_color);
                lineDataSet.setFillDrawable(lineDrawable);
                LineData lineData = new LineData(lineDataSet);
                lineData.setValueTextColor(Color.BLACK);
                lineData.setValueTextSize(10f);
                binding.lineChart.setData(lineData);
                binding.lineChart.invalidate();

                binding.lineChart.setVisibility(View.VISIBLE);
            }
        });
    }

    // Search for hour o(1)
    private Integer getMyHourIndex(int hour) {
        if (hour >= 0 && hour < 3)
            return 0;
        else if (hour >= 3 && hour < 6)
            return 1;
        else if (hour >= 6 && hour < 9)
            return 2;
        else if (hour >= 9 && hour < 12)
            return 3;
        else if (hour >= 12 && hour < 15)
            return 4;
        else if (hour >= 15 && hour < 18)
            return 5;
        else if (hour >= 18 && hour < 21)
            return 6;
        else return 7;
    }

    // Insert Weather List o(1)
    private void setWeatherList(int day, WeatherDetails item) {
        switch (day) {
            case 1:
                sun.add(item);
                break;
            case 2:
                mon.add(item);
                break;
            case 3:
                tue.add(item);
                break;
            case 4:
                wed.add(item);
                break;
            case 5:
                thu.add(item);
                break;
            case 6:
                fri.add(item);
                break;
            case 7:
                sat.add(item);
                break;
        }
    }

    // Search for day o(1)
    private List<WeatherDetails> getMyDayList(int day) {
        switch (day) {
            case 1:
                return sun;
            case 2:
                return mon;
            case 3:
                return tue;
            case 4:
                return wed;
            case 5:
                return thu;
            case 6:
                return fri;
            case 7:
                return sat;
            default:
                return new ArrayList<>();
        }
    }

    // init week day name
    private String getMyDayName(int today) {
        switch (today) {
            case 1:
                return "Sun";
            case 2:
                return "Mon";
            case 3:
                return "Tue";
            case 4:
                return "Wed";
            case 5:
                return "Thu";
            case 6:
                return "Fri";
            case 7:
                return "Sat";
            default:
                return "";
        }
    }

    // Init custom adapter model to ignore an unwanted data
    private List<WeatherAdapterModel> getDaysWeatherList() {
        Calendar calendar = Calendar.getInstance();
        int today = calendar.get(Calendar.DAY_OF_WEEK);
        List<WeatherAdapterModel> list = new ArrayList<>();
        List<Integer> days = shiftDays(today, 6);
        for (Integer day : days) {
            List<WeatherDetails> todayList = getMyDayList(day);
            if (!todayList.isEmpty()) {
                list.add(new WeatherAdapterModel(getMyDayName(day), Helper.convertToCelsius(todayList.get(0).getMinTemp()) + "°", Helper.convertToCelsius(todayList.get(0).getMinTemp()) + "°", todayList.get(0).getWeatherIcon()));
            }
        }
        return list;
    }

    // Simple algorithm to shift days list needed in line chart and recycler view
    private List<Integer> shiftDays(int input, final int MAX) {
        List<Integer> days = new ArrayList<>();
//        final int MAX = 6;
        for (int i = 0; i < MAX; i++) {
            int temp = i + input;
            if (temp <= MAX)
                days.add(temp);
            else
                days.add(temp - MAX);
        }
        return days;
    }
}
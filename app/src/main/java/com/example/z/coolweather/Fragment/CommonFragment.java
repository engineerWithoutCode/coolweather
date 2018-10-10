package com.example.z.coolweather.Fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.z.coolweather.MainActivity;
import com.example.z.coolweather.MyApplication;
import com.example.z.coolweather.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Network.HttpUtil;
import Network.Utility;
import db.ForecastData;
import db.WeatherData;
import gson.Forecast;
import gson.Weather;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class CommonFragment extends Fragment {
    private View view;
    private TextView addressName;
    private TextView tmpNow;
    private TextView typeNow;
    private TextView updateTime;
    private TextView aqi,pm25;
    private TextView forecastDate,forecastType,forecastTmp;
    private TextView comfort,carWash,sport;
    private LinearLayout linearLayout;
    private SwipeRefreshLayout swipeRefreshLayout;

    private String weatherId;

    private Weather weather;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    initView(weather);
                    update(weather);
                    swipeRefreshLayout.setRefreshing(false);
                    break;
            }
        }
    };



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        view = inflater.inflate(R.layout.fragment_common, container, false);
        linearLayout = (LinearLayout)view.findViewById(R.id.forecast_layout);
        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe_refresh);

        addressName = (TextView)view.findViewById(R.id.locate_address);
        tmpNow = (TextView)view.findViewById(R.id.current_type);
        typeNow = (TextView)view.findViewById(R.id.current_type);
        updateTime = (TextView)view.findViewById(R.id.update_time);

        aqi = (TextView)view.findViewById(R.id.aqi_now);
        pm25 = (TextView)view.findViewById(R.id.pm_now);


        comfort = (TextView)view.findViewById(R.id.tv_comfort);
        carWash = (TextView)view.findViewById(R.id.tv_car_wash);
        sport = (TextView)view.findViewById(R.id.tv_sport);

        Bundle bundle = getArguments();
        if(bundle != null){
            weatherId = bundle.getString("weatherId");
            queryWeather(weatherId);
        }
        requestWeather(weatherId);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                requestWeather(weatherId);
            }
        });
        return view;
    }

    public View getView(){
        return view;
    }


    //根据天气ID请求城市天气信息
    public void requestWeather(final String weatherId){
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=6dd28da05b4847dcb46d98b6ab877fec";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseDate = response.body().string();
                weather = Utility.handleWeatherResponse(responseDate);
                if (weather != null){
                    Message msg = new Message();
                    msg.what = 1;
                    handler.sendMessage(msg);
                }else{
                    Message msg = new Message();
                    msg.what = 2;
                    handler.sendMessage(msg);
                }
            }
        });
    }

    private void initView(Weather weather){
        addressName.setText(weather.basic.admin_area + weather.basic.parent_city + "  " + weather.basic.location);
        updateTime.setText(weather.basic.update.loc);
        tmpNow.setText(weather.now.temperature);
        typeNow.setText(weather.now.cond_txt);
        if (weather.aqi != null){
            aqi.setText(weather.aqi.city.aqi);
            pm25.setText(weather.aqi.city.pm25);
        }
        linearLayout.removeAllViews();
        for (Forecast forecast : weather.daily_forecast){
            View v = LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.forecast_item,linearLayout,false);
            forecastDate = (TextView)v.findViewById(R.id.forecast_date);
            forecastType = (TextView)v.findViewById(R.id.forecast_type);
            forecastTmp = (TextView)v.findViewById(R.id.forecast_tmp);
            forecastDate.setText(forecast.date+"");
            forecastTmp.setText(forecast.temperature.min + "-" +forecast.temperature.max + "℃");
            forecastType.setText(forecast.cond.txt_d);
            linearLayout.addView(v);
        }
        comfort.setText("舒适度：" + weather.suggestion.comf.txt);
        carWash.setText("洗车指数：" + weather.suggestion.cw.txt);
        sport.setText("运动建议：" + weather.suggestion.sport.txt);
    }


    private void update(Weather weather){
        List<WeatherData> weatherDataList = DataSupport.where("weatherid = ?",weatherId).find(WeatherData.class);
        List<String> day = new ArrayList<>();
        day.add("one");
        day.add("two");
        day.add("three");
        if(weatherDataList.size() > 0) {
            //更新当前天气数据
            Log.d("更新数据","执行");
            WeatherData weatherData = new WeatherData();
            weatherData.setWeatherId(weatherId);
            weatherData.setName(weather.basic.admin_area + weather.basic.parent_city + "  " + weather.basic.location);
            weatherData.setTmpNow(weather.now.temperature);
            weatherData.setTypeNow(weather.now.cond_txt);
            weatherData.setUpdateTime(weather.basic.update.loc);
            weatherData.setAqi(weather.aqi.city.aqi);
            weatherData.setPm25(weather.aqi.city.pm25);
            weatherData.setComfort(weather.suggestion.comf.txt);
            weatherData.setCarWash(weather.suggestion.cw.txt);
            weatherData.setSport(weather.suggestion.sport.txt);
            weatherData.updateAll("weatherid = ?", weatherId);
            //更新天气预报
            int i = 0;
            for (Forecast forecast : weather.daily_forecast) {
                ForecastData forecastData = new ForecastData();
                forecastData.setWeatherId(weatherId);
                forecastData.setDate(forecast.date);
                forecastData.setDay(i);
                forecastData.setType(forecast.cond.txt_d);
                forecastData.setTmp(forecast.temperature.min + "-" + forecast.temperature.max + "℃");
                String s = i+"";
                Log.d("s",s);
                forecastData.updateAll("weatherid = ? and day = ?", weatherId, s);
                i++;
            }
        }else {
            //保存当前天气数据
            Log.d("保存数据","执行");
            WeatherData weatherData = new WeatherData();
            weatherData.setWeatherId(weatherId);
            weatherData.setName(weather.basic.admin_area + weather.basic.parent_city + "  " + weather.basic.location);
            weatherData.setTmpNow(weather.now.temperature);
            weatherData.setTypeNow(weather.now.cond_txt);
            weatherData.setUpdateTime(weather.basic.update.loc);
            weatherData.setAqi(weather.aqi.city.aqi);
            weatherData.setPm25(weather.aqi.city.pm25);
            weatherData.setComfort(weather.suggestion.comf.txt);
            weatherData.setCarWash(weather.suggestion.cw.txt);
            weatherData.setSport(weather.suggestion.sport.txt);
            weatherData.save();
            //保存天气预报
            int i = 0;
            for (Forecast forecast : weather.daily_forecast){
                ForecastData forecastData = new ForecastData();
                forecastData.setWeatherId(weatherId);
                forecastData.setDate(forecast.date);
                forecastData.setDay(i);
                forecastData.setType(forecast.cond.txt_d);
                forecastData.setTmp(forecast.temperature.min + "-" +forecast.temperature.max + "℃");
                forecastData.save();
                i++;
            }
        }
    }

    public void queryWeather(String weatherId){
        List<WeatherData> weatherData = DataSupport.where("weatherid = ?",weatherId).find(WeatherData.class);
        List<ForecastData> forecastDataList = DataSupport.where("weatherid = ?",weatherId).find(ForecastData.class);
        if (weatherData.size() > 0){
            addressName.setText(weatherData.get(0).getName());
            updateTime.setText(weatherData.get(0).getUpdateTime());
            tmpNow.setText(weatherData.get(0).getTmpNow());
            typeNow.setText(weatherData.get(0).getTypeNow());
            aqi.setText(weatherData.get(0).getAqi());
            pm25.setText(weatherData.get(0).getPm25());
            comfort.setText("舒适度：" + weatherData.get(0).getComfort());
            carWash.setText("洗车指数：" + weatherData.get(0).getCarWash());
            sport.setText("运动建议：" + weatherData.get(0).getSport());
            linearLayout.removeAllViews();
            for (ForecastData forecastData : forecastDataList){
                View v = LayoutInflater.from(getContext()).inflate(R.layout.forecast_item,linearLayout,false);
                forecastDate = (TextView)v.findViewById(R.id.forecast_date);
                forecastType = (TextView)v.findViewById(R.id.forecast_type);
                forecastTmp = (TextView)v.findViewById(R.id.forecast_tmp);
                forecastDate.setText(forecastData.getDate());
                forecastType.setText(forecastData.getType());
                forecastTmp.setText(forecastData.getTmp());
                linearLayout.addView(v);
            }
        }else {

        }
    }


    public String getWeatherId(){
        return weatherId;
    }

}

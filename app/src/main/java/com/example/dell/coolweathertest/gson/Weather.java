package com.example.dell.coolweathertest.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/*
 * 需要再创建一个总的实体类来引用刚刚创建的各个实体类
 * Weather类中，对Basic、AQI、Now、Suggestion、
 * Forecast类进行了引用，由于daily_forecast包含的是一个数组，
 * 因此使用List集合来引用Forecast类。
 * 此外，除了天气信息数据还会包含一项status数据，
 * 成功获取天气数据的时候会返回ok,失败时会返回具体原因。
 * */
public class Weather {
    /**
     * Weather类作为总的实例类来引用以上各个实体类
     */

    public String status;

    public Basic basic;

    public AQI aqi;

    public Now now;

    public Suggestion suggestion;

    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;
}

package com.example.dell.coolweathertest.util;

import android.text.TextUtils;

import com.example.dell.coolweathertest.db.City;
import com.example.dell.coolweathertest.db.County;
import com.example.dell.coolweathertest.db.Province;
import com.example.dell.coolweathertest.gson.Weather;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Utility {
    /**
     * 解析和处理服务器返回的省级数据
     */
    public static boolean handleProvinceRequest(String response) {
        if (!TextUtils.isEmpty (response)) {
            try {
                //将服务器中返回的数据传入到JSONArray对象中
                JSONArray allProvinces = new JSONArray (response);
                //循环遍历JSONArray
                for (int i = 0; i < allProvinces.length (); i++) {
                    //从中取出的每一个对象都是一个JSONObject对象
                    JSONObject provincesObject = allProvinces.getJSONObject (i);
                    //将数据组装成实体类对象
                    Province province = new Province ();
                    province.setProvinceName (provincesObject.getString ("name"));
                    province.setProvinceCode (provincesObject.getInt ("id"));
                    //调用sava()方法将数据存储到数据库中
                    province.save ();
                }
            } catch (JSONException e) {
                e.printStackTrace ();
            }
        }
        return true;
    }

    /**
     * 解析和处理服务器返回的市级数据
     */
    public static boolean handleCityRequest(String response, int provinceId) {
        if (!TextUtils.isEmpty (response)) {
            try {
                //将服务器中返回的数据传入到JSONArray对象中
                JSONArray allCitys = new JSONArray (response);
                //循环遍历JSONArray
                for (int i = 0; i < allCitys.length (); i++) {
                    //从中取出的每一个对象都是一个JSONObject对象
                    JSONObject cityObject = allCitys.getJSONObject (i);
                    //将数据组装成实体类对象
                    City city = new City ();
                    city.setCityName (cityObject.getString ("name"));
                    city.setCityCode (cityObject.getInt ("id"));
                    city.setProvinceId (provinceId);
                    //调用sava()方法将数据存储到数据库中
                    city.save ();
                }
            } catch (JSONException e) {
                e.printStackTrace ();
            }
        }
        return true;
    }

    /**
     * 解析和处理服务器返回的县级数据
     */
    public static boolean handleCountyRequest(String response, int cityId) {
        if (!TextUtils.isEmpty (response)) {
            try {
                //将服务器中返回的数据传入到JSONArray对象中
                JSONArray allCounties = new JSONArray (response);
                //循环遍历JSONArray
                for (int i = 0; i < allCounties.length (); i++) {
                    //从中取出的每一个对象都是一个JSONObject对象
                    JSONObject cityObject = allCounties.getJSONObject (i);
                    //将数据组装成实体类对象
                    County county = new County ();
                    county.setCountyName (cityObject.getString ("name"));
                    county.setWeatherId (cityObject.getString ("weather_id"));
                    county.setCityId (cityId);
                    //调用sava()方法将数据存储到数据库中
                    county.save ();
                }
            } catch (JSONException e) {
                e.printStackTrace ();
            }
        }
        return true;
    }

    /*
     * 将返回的JSON数据转化为Weather对象
     * */
    public static Weather handleWeatherResponse(String response) {
        try {
            //将天气信息的主体内容解析出来
            JSONObject jsonObject = new JSONObject (response);
            JSONArray jsonArray = jsonObject.getJSONArray ("HeWeather");
            String weatherContent = jsonArray.getJSONObject (0).toString ();
            //将JSON数据转换为weather对象
            return new Gson ().fromJson (weatherContent, Weather.class);
        } catch (JSONException e) {
            e.printStackTrace ();
        }
        return null;
    }
}

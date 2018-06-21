package com.example.dell.coolweathertest.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.dell.coolweathertest.R;

public class MainActivity extends AppCompatActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main);
        //缓存数据
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences (this);
        //缓存数据判断
        if(preferences.getString ("weather", null) != null){
            Intent intent = new Intent (this, WeatherAcitivity.class);
            startActivity (intent);
            finish ();
        }
    }
}

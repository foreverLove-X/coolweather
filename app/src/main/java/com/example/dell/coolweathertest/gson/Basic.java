package com.example.dell.coolweathertest.gson;

import com.google.gson.annotations.SerializedName;

/*
 * 使用了@SerializedName() 来命名JSON中的一些字段，
 * 由于JSON中的一些字段不适合直接用来使用，因为不好理解，所以可以使用@SerializedName()的方式 ，将JSON字段写在里面，
 * 然后在下面一行写上自己需要用的命名(可随意写，
 * 只要自己理解就可以)。
 * */
public class Basic {
    @SerializedName("city")
    public String cityName;//城市名

    @SerializedName("id")
    public String weatherId;//城市对应的天气的id

    @SerializedName("lat")
    public String cityLat;//城市的经度

    @SerializedName("lon")
    public String cityLon;//城市的纬度

    public Update update;

    public class Update {
        @SerializedName("loc")
        public String updateTime;//接口更新时间
    }
}

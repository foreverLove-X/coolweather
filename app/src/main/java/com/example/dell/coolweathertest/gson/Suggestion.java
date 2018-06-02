package com.example.dell.coolweathertest.gson;

import com.google.gson.annotations.SerializedName;

public class Suggestion {
    public Air air;//空气质量指数

    public class Air {

        @SerializedName("txt")
        public String info;

    }

    @SerializedName("comf")
    public Comfort comfort;//舒适度指数

    public class Comfort {

        @SerializedName("txt")
        public String info;

    }

    @SerializedName("cw")
    public CarWash carWash;//洗车指数

    public class CarWash {

        @SerializedName("txt")
        public String info;
    }

    public Drsg drsg;//穿衣指数

    public class Drsg {

        @SerializedName("txt")
        public String info;
    }

    public Flu flu;//感冒指数

    public class Flu {

        @SerializedName("txt")
        public String info;
    }

    public Sport sport;//运动指数

    public class Sport {

        @SerializedName("txt")
        public String info;
    }

    public Trav trav;//旅游指数

    public class Trav {

        @SerializedName("txt")
        public String info;
    }

    public Uv uv;//紫外线指数

    public class Uv {

        @SerializedName("txt")
        public String info;
    }
}

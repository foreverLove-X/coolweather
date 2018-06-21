package com.example.dell.coolweathertest.activity;

// 使用support-v4库中的Fragment，
// 因为它可以让碎片在所有的Android版本中保持功能一致性）

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dell.coolweathertest.R;
import com.example.dell.coolweathertest.db.City;
import com.example.dell.coolweathertest.db.County;
import com.example.dell.coolweathertest.db.Province;
import com.example.dell.coolweathertest.util.HttpUtils;
import com.example.dell.coolweathertest.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChooseAreaFragment extends Fragment {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;
    private ProgressDialog progressDialog; //进度条（加载省市县信息会出现）
    private TextView titleTest;
    private Button backBtn;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<> ();
    /*
     * 省列表
     * */
    private List<Province> provinceList;
    /*
     * 市列表
     * */
    private List<City> cityList;
    /*
     * 县列表
     * */
    private List<County> countyList;
    /*
     * 选中的省份
     * */
    private Province selectedProvince;
    /*
     * 选中的城市
     * */
    private City selectedCity;
    /*
     * 当前选中的级别
     * */
    private int currentLevel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate (R.layout.chose_area, container, false);
        titleTest = (TextView) view.findViewById (R.id.title_text);
        backBtn = (Button) view.findViewById (R.id.back_button);
        listView = (ListView) view.findViewById (R.id.list_view);

        adapter = new ArrayAdapter<> (getContext (), android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter (adapter);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated (savedInstanceState);
        //ListView的点击事件
        listView.setOnItemClickListener (new AdapterView.OnItemClickListener () {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {//在省级列表
                    selectedProvince = provinceList.get (position);//选则省
                    queryCities ();//查找城市
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get (position);
                    queryCounties ();
                } else if(currentLevel == LEVEL_COUNTY) {
                    //如果当前级别为LEVEL_COUNTY 则启动WeatherActivity 并把当前选中的县的天气id传递过去
                    String weatherId = countyList.get (position).getWeatherId ();
                    if(getActivity () instanceof  MainActivity) {
                        Intent intent = new Intent (getActivity (), WeatherAcitivity.class);
                        intent.putExtra ("weather_id", weatherId);
                        startActivity (intent);
                        getActivity ().finish ();
                    } else if(getActivity () instanceof WeatherAcitivity) {
                        WeatherAcitivity acitivity = (WeatherAcitivity) getActivity ();
                        acitivity.drawerLayout.closeDrawers ();//关闭滑动菜单
                        acitivity.swipeRefresh.setRefreshing (true);//显示下拉刷新进度条
                        acitivity.requestWeather (weatherId);//请求城市天气信息
                    }
                }
            }
        });
        //Button的点击事件
        backBtn.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                if (currentLevel == LEVEL_COUNTY) {
                    queryCities ();
                } else if (currentLevel == LEVEL_CITY) {
                    queryProvinces ();
                }
            }
        });
        queryProvinces ();//加载省级数据
    }

    /*
     * 查询全国所有的省，优先从数据库查，如果没有查询到再去服务器上查
     * */
    private void queryProvinces() {
        titleTest.setText ("中国");//头标题
        backBtn.setVisibility (View.GONE);//当处于省级列表时，返回键隐藏
        //从数据库中读取省级数据
        provinceList = DataSupport.findAll (Province.class);
        //如果读到数据，则直接显示到界面上
        if (provinceList.size () > 0) {
            dataList.clear ();
            for (Province province : provinceList) {
                dataList.add (province.getProvinceName ());
            }
            adapter.notifyDataSetChanged ();
            listView.setSelection (0);
            currentLevel = LEVEL_PROVINCE;
        } else {
            //如果没有读到数据，则组装一个请求地址
            //调用queryFromServer()方法从服务器上查询数据
            String address = "http://guolin.tech/api/china";///郭霖地址服务器
            queryFromServer (address, "province");
        }
    }

    /*
     * 查询选中省内的所有市，优先从数据库查，如果没有查询到再去服务器上查
     * */
    private void queryCities() {
        titleTest.setText (selectedProvince.getProvinceName ());
        backBtn.setVisibility (View.VISIBLE);//当处于市级列表时，返回按键显示
        cityList = DataSupport.where ("provinceid = ?", String.valueOf (selectedProvince.getId ())).find (City.class);
        if (cityList.size () > 0) {
            dataList.clear ();
            for (City city : cityList) {
                dataList.add (city.getCityName ());
            }
            adapter.notifyDataSetChanged ();
            listView.setSelection (0);
            currentLevel = LEVEL_CITY;
        } else {
            int provinceCode = selectedProvince.getProvinceCode ();
            String address = "http://guolin.tech/api/china/" + provinceCode;
            queryFromServer (address, "city");
        }
    }

    /*
     * 查询选中市内的所有的县，优先从数据库查，如果没有查询到再去服务器上查
     * */
    private void queryCounties() {
        titleTest.setText (selectedCity.getCityName ());
        backBtn.setVisibility (View.VISIBLE);
        countyList = DataSupport.where ("cityid = ?", String.valueOf (selectedCity.getId ())).find (County.class);
        if (countyList.size () > 0) {
            dataList.clear ();
            for (County county : countyList) {
                dataList.add (county.getCountyName ());
            }
            adapter.notifyDataSetChanged ();
            listView.setSelection (0);
            currentLevel = LEVEL_COUNTY;
        } else {
            int provinceCode = selectedProvince.getProvinceCode ();
            int cityCode = selectedCity.getCityCode ();
            String address = "http://guolin.tech/api/china/" + provinceCode + "/" + cityCode;
            queryFromServer (address, "county");
        }
    }

    /*
     * 根据传入的地址和类型从服务器上查询省市县数据
     * */
    private void queryFromServer(String address, final String type) {
        showProgressDialog ();
        //向服务器发生请求，响应的数据会回调到onResponse()方法中
        HttpUtils.sendOkHttpRequest (address, new Callback () {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body ().string ();
                boolean result = false;
                if ("province".equals (type)) {
                    //解析和处理从服务器返回的数据，并存储到数据库中
                    result = Utility.handleProvinceRequest (responseText);
                } else if ("city".equals (type)) {
                    result = Utility.handleCityRequest (responseText, selectedProvince.getId ());
                } else if ("county".equals (type)) {
                    result = Utility.handleCountyRequest (responseText, selectedCity.getId ());
                }
                if (result) {
                    //由于query方法用到UI操作，必须要在主线程中调用。
                    //借助runOnUiThread()方法实现从子线程切换到主线程
                    getActivity ().runOnUiThread (new Runnable () {
                        @Override
                        public void run() {
                            closeProgressDialog ();
                            if ("province".equals (type)) {
                                //数据库已经存在数据，调用queryProvinces直接将数据显示到界面上
                                queryProvinces ();
                            } else if ("city".equals (type)) {
                                queryCities ();
                            } else if ("county".equals (type)) {
                                queryCounties ();
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                //通过runOnUiThread()方法回到主线程处理逻辑
                getActivity ().runOnUiThread (new Runnable () {
                    @Override
                    public void run() {
                        closeProgressDialog ();
                        Toast.makeText (getContext (), "加载失败", Toast.LENGTH_SHORT).show ();
                    }
                });
            }
        });
    }

    /*
     * 显示进度对话框
     * */
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog (getActivity ());
            progressDialog.setMessage ("正在加载...");
            progressDialog.setCanceledOnTouchOutside (false);
        }
        progressDialog.show ();
    }

    /*
     * 关闭对话框
     * */
    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss ();
        }
    }
}



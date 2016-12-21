package com.example.zuo.weather;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zuo.weather.db.City;
import com.example.zuo.weather.db.County;
import com.example.zuo.weather.db.Province;
import com.example.zuo.weather.gson.Utility;
import com.example.zuo.weather.util.HttpUtil;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChooseAreaFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();
    /*
     省列表
     */

    private List<Province> provinceList;
    /*
    市列表
    */
    private List<City> cityList;
    /*
    县列表
    */
    private List<County> countyList;
    /*
    选中的省份
     */
    private Province selectedProvince;
    /*
    选中的城市
     */
    private City selectedCity;


    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;
    /*
    当前选中的级别
     */
    private int currentLevel;
    private ProgressDialog progressDialog;
    private ListView listview;
    private TextView titleBarLable;
    private ImageView titleBarLeft;

    public ChooseAreaFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ChooseAreaFragment newInstance(String param1, String param2) {
        ChooseAreaFragment fragment = new ChooseAreaFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);
        //ButterKnife.bind(this, view);
        listview = (ListView) view.findViewById(R.id.listview);
        titleBarLable = (TextView) view.findViewById(R.id.title_bar_lable);
        titleBarLeft = (ImageView) view.findViewById(R.id.title_bar_left);

        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dataList);
        listview.setAdapter(adapter);
        titleBarLeft.setOnClickListener(v -> {
            if (currentLevel == LEVEL_COUNTY) {
                queryCities();
            } else if (currentLevel == LEVEL_CITY) {
                queryProvinces();
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listview.setOnItemClickListener((adapterView, view, i, l) -> {
            if (currentLevel == LEVEL_PROVINCE) {
                selectedProvince = provinceList.get(i);
                queryCities();
            } else if (currentLevel == LEVEL_CITY) {
                selectedCity = cityList.get(i);
                queryCounties();
            }
        });
        queryProvinces();
    }

    private void queryProvinces() {
/**
 * 查询全国所有的省，优先从数据库查询，如果没有查到再去服务器上查询
 */
        titleBarLable.setText("中国");
        titleBarLeft.setVisibility(View.GONE);
        provinceList = DataSupport.findAll(Province.class);
        if (provinceList.size() > 0) {
            dataList.clear();
            for (Province province : provinceList) {
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listview.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        } else {
            String address = "http://guolin.tech/api/china";
            quaryFromSever(address, "province");
        }
    }

    private void queryCities() {
/**
 * 查询选中省内所有的市，优先从数据库查询，如果没有查到再去服务器上查询
 */
        titleBarLable.setText(selectedProvince.getProvinceName());
        titleBarLeft.setVisibility(View.VISIBLE);
        cityList = DataSupport.where("provinceid = ?", String.valueOf(selectedProvince.getId())).find(City.class);
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listview.setSelection(0);
            currentLevel = LEVEL_CITY;
        } else {
            int provinceCode = selectedProvince.getProvinceCode();
            String address = "http://guolin.tech/api/china/" + provinceCode;
            quaryFromSever(address, "city");
        }
    }

    private void queryCounties() {
        /**
         * 查询选中市内所有的县，优先从数据库查询，如果没有查到再去服务器上查询
         */
        titleBarLable.setText(selectedCity.getCityName());
        titleBarLeft.setVisibility(View.VISIBLE);
        countyList = DataSupport.where("cityid = ?", String.valueOf(selectedCity.getId())).find(County.class);
        if (countyList.size() > 0) {
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listview.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        } else {
            int provinceCode = selectedProvince.getProvinceCode();
            int cityCode = selectedCity.getCityCode();
            String address = "http://guolin.tech/api/china/" + provinceCode + "/" + cityCode;
            quaryFromSever(address, "county");
        }
    }

    //根据传入的地址和类型从服务器上查询省市县的数据
    private void quaryFromSever(String address, String type) {
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(() -> {
                    closeProgressDialog();
                    Toast.makeText(getContext(), "加载失败", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                boolean result = false;
                if ("province".equals(type)) {
                    result = Utility.handleProvinceResponse(responseText);
                } else if ("city".equals(type)) {
                    result = Utility.handleCityResponse(responseText, selectedProvince.getId());
                } else if ("county".equals(type)) {
                    result = Utility.handleCountyResponse(responseText, selectedCity.getId());
                }
                if (result) {
                    getActivity().runOnUiThread(() -> {
                        closeProgressDialog();
                        if ("province".equals(type)) {
                            queryProvinces();
                        } else if ("city".equals(type)) {
                            queryCities();
                        } else if ("county".equals(type)) {
                            queryCounties();
                        }
                    });
                }
            }
        });
    }

    //关闭进度对话框
    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    //显示进度对话框
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
}

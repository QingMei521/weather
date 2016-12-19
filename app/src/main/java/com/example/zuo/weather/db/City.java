package com.example.zuo.weather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by zuo on 2016/12/19.
 * 省
 */

public class City extends DataSupport {
    /**
     * id
     * 市的名字
     * 市的代号
     * 当前市所属的省的id
     */
    private int id;
    private String cityName;
    private int cityCode;
    private int provinceId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getCityCode() {
        return cityCode;
    }

    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }
}

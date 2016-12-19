package com.example.zuo.weather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by zuo on 2016/12/19.
 * 省
 */

public class County extends DataSupport {
    /**
     * id
     * 县的名字
     * 县所对应的天气id
     * 当前县所属的市的id
     */
    private int id;
    private String countyName;
    private String weatherId;
    private int cityId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }
}

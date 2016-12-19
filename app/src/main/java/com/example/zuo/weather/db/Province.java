package com.example.zuo.weather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by zuo on 2016/12/19.
 * 省
 */

public class Province extends DataSupport {
    /**
     * id
     * 省的名字
     * 省的代号
     */
    private int id;
    private String provinceName;
    private int provinceCode;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public int getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }
}

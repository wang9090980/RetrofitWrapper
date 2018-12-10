package com.wl.lib.retrofitwrapper.entity;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

/**
 * 城市和IP信息
 */
public class CityInfo {

    private static final String UNKNOWN = "未知";

    @SerializedName("cid")
    private String cityId;
    @SerializedName("cname")
    private String cityName;
    @SerializedName("cip")
    private String ipAddress;

    public String getCityId() {
        if (TextUtils.isEmpty(cityId)) {
            return UNKNOWN;
        }
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        if (TextUtils.isEmpty(cityName)) {
            return UNKNOWN;
        }
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getIpAddress() {
        if (TextUtils.isEmpty(ipAddress)) {
            return UNKNOWN;
        }
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @Override
    public String toString() {
        return "CityInfo{" +
                "cityId='" + cityId + '\'' +
                ", cityName='" + cityName + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                '}';
    }
}

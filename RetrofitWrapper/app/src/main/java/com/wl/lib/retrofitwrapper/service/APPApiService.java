package com.wl.lib.retrofitwrapper.service;


import com.wl.devlib.http.config.UrlConfig;
import com.wl.devlib.http.model.BaseEntity;
import com.wl.lib.retrofitwrapper.entity.CityInfo;
import com.wl.lib.retrofitwrapper.entity.NewsWrapper;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface APPApiService {

    /**
     * 获取城市信息
     */
    @GET("/cityjson?ie=utf-8&qq-pf-to=pcqq.c2c")
    Observable<ResponseBody> getCityInfo();

    /**
     * 获取城市信息
     */
    @GET("/cityjson?ie=utf-8&qq-pf-to=pcqq.c2c")
    Flowable<BaseEntity<CityInfo>> getCityInfoModel();


    @Headers({UrlConfig.FLAG_MULTIPLE_BASE_URL_KEY + UrlConfig.FLAG_MULTIPLE_BASE_URL_B})
    @POST("/login")
    @FormUrlEncoded
    Flowable<BaseEntity> login(@Field("name") String name, @Field("password") String password);


    @Headers({UrlConfig.FLAG_MULTIPLE_BASE_URL_KEY + UrlConfig.FLAG_MULTIPLE_BASE_URL_B})
    @GET("/method/get/list")
    Flowable<BaseEntity<NewsWrapper>> getNews(@Query("pageNum") int pageNum, @Query("pageSize") int pageSize);


    /**
     * 切换域名的特使接口
     * Add the Domain-Name header
     */
    @Headers({UrlConfig.FLAG_MULTIPLE_BASE_URL_KEY + UrlConfig.FLAG_MULTIPLE_BASE_URL_A})
    @GET("/v2/book/{id}")
    Observable<ResponseBody> testSwitchBaseUrl(@Path("id") int id);


}
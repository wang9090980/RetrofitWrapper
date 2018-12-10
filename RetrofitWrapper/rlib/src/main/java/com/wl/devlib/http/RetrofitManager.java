package com.wl.devlib.http;


import com.wl.devlib.http.config.HttpConfig;
import com.wl.devlib.http.config.UrlConfig;
import com.wl.devlib.http.converter.wrapper.gson.GsonConverterFactoryWrapper;
import com.wl.devlib.http.interceptor.HttpUrlInterceptor;
import com.wl.devlib.http.interceptor.NetworkInterceptor;
import com.wl.devlib.http.model.HttpHeaders;
import com.wl.devlib.http.model.HttpParams;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * HttpClient管理器(单例)
 */
public class RetrofitManager {

    private HttpClient httpClient;

    private final static Boolean DEFAULT_LOG_ENABLED = true;

    private volatile static RetrofitManager instance;

    private RetrofitManager() {
    }

    public static RetrofitManager getInstance() {
        if (instance == null) {
            synchronized (RetrofitManager.class) {
                if (instance == null) {
                    instance = new RetrofitManager();
                }
            }
        }

        return instance;
    }

    private HttpClient getHttpClient() {
        if (httpClient == null) {
            httpClient = new HttpClient.Builder(BaseApplication.getInstance())
                    .baseUrl(UrlConfig.getDominUrl())
                    .readTimeout(HttpConfig.READ_TIME_OUT)
                    .writeTimeout(HttpConfig.WRITE_TIME_OUT)
                    .connectTimeout(HttpConfig.CONNECT_TIME_OUT)
                    .validateEagerly(true)
                    .setCacheEnabled(false)
                    .setCookieEnabled(true)
                    .addHeader(HttpConfig.getBaseHeaders())
                    .setLogEnabled(DEFAULT_LOG_ENABLED)
                    .addInterceptor(new HttpUrlInterceptor())
                    .addInterceptor(new NetworkInterceptor(BaseApplication.getInstance()))
                    .addConverterFactory(GsonConverterFactoryWrapper.create())
                    // .addCallAdapterFactory(RxJava2CallAdapterFactoryWrapper.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
        }
        return httpClient;
    }

    /**
     * 重新初始化，登录登出时更新token
     */
    public void reinitialize() {
        httpClient = null;
        getInstance();
    }

    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    /**
     * 返回{@link Retrofit}对象
     */
    public Retrofit getRetrofit() {
        return getHttpClient().getRetrofit();
    }

    /**
     * 返回{@link OkHttpClient}对象
     */
    public OkHttpClient getOkHttpClient() {
        return getHttpClient().getOkHttpClient();
    }

    /**
     * 返回{@link HttpHeaders}对象
     */
    public HttpHeaders getHeaders() {
        return getHttpClient().getHeaders();
    }

    /**
     * 返回{@link HttpParams}对象
     */
    public HttpParams getBaseParams() {
        return getHttpClient().getParams();
    }

    /**
     * 更新baseUrl
     */
    public void updateBaseUrl(String baseUrl) {
        getHttpClient().updateBaseUrl(baseUrl);
    }


    /**
     * 创建ApiService
     */
    public <T> T create(final Class<T> service) {
        //修改baseUrl,不需要直接newBuild，和apiService中的@Hearder注解结合
        return getHttpClient().create(service);
    }

    /*
     *当增加一个baseUrl时，需要在if 语句中添加UrlConfig.Falg判断
     *
     * @param baseUrl baseUrl
     * @return 根据 baseUrl获取Flag, from RetrofitUrlManager
     */
    private String getBaseUrlFlag(String baseUrl) {
        //如果不是默认的baseUrl
        if (!UrlConfig.getDominUrl().equals(baseUrl)) {
            return UrlConfig.FLAG_MULTIPLE_BASE_URL_A;
        } else {
            return UrlConfig.FLAG_MULTIPLE_BASE_URL_B;
        }
    }

    /**
     * 设置是否开启日志打印
     */
    public void setLogEnabled(boolean logEnabled) {
        getHttpClient().setLogEnabled(logEnabled);
    }


}

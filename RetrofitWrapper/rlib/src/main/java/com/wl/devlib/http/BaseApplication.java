package com.wl.devlib.http;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.wl.devlib.http.config.UrlConfig;
import com.wl.devlib.http.utils.LoggerUtils;

import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;
import me.jessyan.retrofiturlmanager.RetrofitUrlManager;


public abstract class BaseApplication extends Application {

    static {
        // 设置全局异常处理(处理无法传递的异常)
        RxJavaPlugins.setErrorHandler(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                LoggerUtils.e(TAG, throwable);
            }
        });
    }

    /**
     * TAG
     */
    private static final String TAG = "BaseApplication";
    /**
     * {@link Application}实例
     */
    private static BaseApplication sInstance;

    public static BaseApplication getInstance() {
        return sInstance;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

        //声明默认BaseUrl之外的BaseUrl
        RetrofitUrlManager.getInstance().putDomain(UrlConfig.FLAG_MULTIPLE_BASE_URL_A, UrlConfig.getDominUrlA());

        RetrofitUrlManager.getInstance().putDomain(UrlConfig.FLAG_MULTIPLE_BASE_URL_B, UrlConfig.getDominUrlB());

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


}
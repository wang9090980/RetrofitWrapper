package com.wl.devlib.http.interceptor;

import android.content.Context;
import android.support.annotation.NonNull;

import com.wl.devlib.http.exception.NetErrorException;
import com.wl.devlib.http.utils.NetworkHelper;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 网络拦截器
 */
public class NetworkInterceptor implements Interceptor {

    private Context mContext;

    public NetworkInterceptor(Context context){
        this.mContext = context;
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        if (!NetworkHelper.isNetworkAvailable(mContext)) {
            throw new NetErrorException();
        }
        return chain.proceed(request);
    }

}

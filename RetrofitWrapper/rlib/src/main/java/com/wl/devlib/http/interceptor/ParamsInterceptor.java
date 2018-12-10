package com.wl.devlib.http.interceptor;

import android.support.annotation.NonNull;

import com.wl.devlib.http.config.HttpConfig;
import com.wl.devlib.http.utils.LoggerUtils;
import com.wl.devlib.http.model.HttpParams;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 请求参数拦截器(配置公共参数)
 */
public class ParamsInterceptor implements Interceptor {

    private static final String TAG = "HTTP";
    private HttpParams httpParams;

    public ParamsInterceptor(HttpParams httpParams) {
        this.httpParams = httpParams;
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request original = chain.request();
        Request.Builder builder = original.newBuilder();
        if (!"GET".equalsIgnoreCase(original.method())) {
            if (httpParams.urlParamsMap.isEmpty()) {
                return chain.proceed(builder.build());
            }
            HttpUrl originalHttpUrl = original.url();
            HttpUrl.Builder urlBuilder = originalHttpUrl.newBuilder();
            try {
                Set<Map.Entry<String, String>> entrySet = httpParams.urlParamsMap.entrySet();
                for (Map.Entry<String, String> entry : entrySet) {
                    urlBuilder.addQueryParameter(entry.getKey(), entry.getValue()).build();
                }
            } catch (Exception e) {
                LoggerUtils.e(HttpConfig.HTTP_TAG, e);
            }
            builder.url(urlBuilder.build());
        }
        return chain.proceed(builder.build());

    }
}

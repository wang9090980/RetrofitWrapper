package com.wl.devlib.http.interceptor;

import android.support.annotation.NonNull;

import com.wl.devlib.http.model.HttpHeaders;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 请求头拦截器(配置公共头部)
 */
public class HeadersInterceptor implements Interceptor {

    private HttpHeaders headers;

    public HeadersInterceptor(HttpHeaders headers) {
        this.headers = headers;
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        if (headers.headersMap.isEmpty()) {
            return chain.proceed(builder.build());
        }
        try {
            Set<Map.Entry<String, String>> entrySet = headers.headersMap.entrySet();
            for (Map.Entry<String, String> entry : entrySet) {
                builder.addHeader(entry.getKey(), entry.getValue()).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return chain.proceed(builder.build());
    }

}

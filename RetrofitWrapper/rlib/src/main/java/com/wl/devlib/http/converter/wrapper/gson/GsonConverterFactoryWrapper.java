package com.wl.devlib.http.converter.wrapper.gson;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.wl.devlib.http.model.BaseEntity;
import com.wl.devlib.http.config.TokenCode;
import com.wl.devlib.http.exception.ConverterIOException;
import com.wl.devlib.http.exception.TokenCheckException;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * {@link GsonConverterFactory}的包装类(用于处理异常)
 *
 */
public class GsonConverterFactoryWrapper extends Converter.Factory {

    private final GsonConverterFactory original;

    private GsonConverterFactoryWrapper() {
        original = GsonConverterFactory.create();
    }

    public static GsonConverterFactoryWrapper create() {
        return new GsonConverterFactoryWrapper();
    }

    @Nullable
    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        return new GsonRequestBodyConverterWrapper<>(original.requestBodyConverter(type, parameterAnnotations, methodAnnotations, retrofit));
    }

    @Nullable
    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        return new GsonResponseBodyConverterWrapper<>(original.responseBodyConverter(type, annotations, retrofit));
    }

    /**
     * {@link retrofit2.converter.gson.GsonRequestBodyConverter}的包装类(用于RxJava2处理异常)
     */
    private static class GsonRequestBodyConverterWrapper<T> implements Converter<T, RequestBody> {

        private final Converter<T, RequestBody> inner;

        GsonRequestBodyConverterWrapper(Converter<T, RequestBody> inner) {
            this.inner = inner;
        }

        @Override
        public RequestBody convert(@NonNull T value) throws IOException {
            try {
                return inner.convert(value);
            } catch (IOException e) {
                throw new ConverterIOException(e);
            }
        }
    }

    /**
     * {@link retrofit2.converter.gson.GsonResponseBodyConverter}的包装类(用于RxJava2处理异常)
     */
    private static class GsonResponseBodyConverterWrapper<T> implements Converter<ResponseBody, T> {

        private final Converter<ResponseBody, T> inner;

        GsonResponseBodyConverterWrapper(Converter<ResponseBody, T> inner) {
            this.inner = inner;
        }

        @Override
        public T convert(@NonNull ResponseBody value) throws IOException {
            try {
                T data = inner.convert(value);
                if (data != null && data instanceof BaseEntity) {
                    BaseEntity entity = (BaseEntity) data;
                    int code = entity.getCode();
                    if (TokenCode.isTokenCheckFailed(code)) {
                        throw new TokenCheckException(TokenCode.getFailedDesc(code), entity);
                    }
                }
                return data;
            } catch (IOException e) {
                if (e instanceof TokenCheckException) {
                    throw e;
                }
                throw new ConverterIOException(e, value);
            }
        }
    }

}

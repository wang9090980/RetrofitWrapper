package com.wl.devlib.http.function;


import com.wl.devlib.http.exception.ExceptionEngine;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 * 异常处理
 */
public class ErrorHandlerFunction<T> implements Function<Throwable, Observable<T>> {

    @Override
    public Observable<T> apply(@NonNull Throwable throwable) throws Exception {
        return Observable.error(ExceptionEngine.handleException(throwable));
    }
}

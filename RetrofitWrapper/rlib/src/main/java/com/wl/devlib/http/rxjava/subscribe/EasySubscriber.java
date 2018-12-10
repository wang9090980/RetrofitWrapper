package com.wl.devlib.http.rxjava.subscribe;

import android.content.Context;

import com.wl.devlib.http.exception.ExceptionCode;
import com.wl.devlib.http.exception.ExceptionEngine;
import com.wl.devlib.http.utils.NetworkHelper;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;


public abstract class EasySubscriber<T> implements Subscriber<T> {
    protected Context mContext;

    public EasySubscriber(Context context) {
        this.mContext = context;
    }

    protected Subscription mSubscription;

    @Override
    public void onSubscribe(Subscription s) {
        this.mSubscription = s;
        mSubscription.request(1);
    }

    @Override
    public void onComplete() {
    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();

        if (!NetworkHelper.isNetworkAvailable(mContext)) {
            _onError(ExceptionCode.NETWORD_ERROR, ExceptionCode.get(ExceptionCode.NETWORD_ERROR));
        } else {
            _onError(ExceptionEngine.getExceptionCode(e), e.getMessage());
        }
    }

    @Override
    public void onNext(T t) {
        _onNext(t);
        mSubscription.request(1);
    }


    protected abstract void _onNext(T t);
    protected abstract void _onError(int errorCode,String msg);

}

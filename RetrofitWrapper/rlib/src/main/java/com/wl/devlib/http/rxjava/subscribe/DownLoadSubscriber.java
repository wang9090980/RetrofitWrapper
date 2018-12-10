package com.wl.devlib.http.rxjava.subscribe;

import android.content.Context;

import com.wl.devlib.http.exception.ExceptionCode;
import com.wl.devlib.http.exception.ExceptionEngine;
import com.wl.devlib.http.utils.NetworkHelper;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;


public abstract class DownLoadSubscriber implements Subscriber<Object> {

    protected Context mContext;

    public DownLoadSubscriber(Context context) {
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
    public void onNext(Object o) {
        if (o instanceof Integer) {
            _onProgress((Integer) o);
        }

        if (o instanceof String) {
            _onNext((String) o);
        }
        mSubscription.request(1);
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

    protected abstract void _onNext(String result);

    protected abstract void _onProgress(Integer percent);

    protected abstract void _onError(int errorCode, String msg);

}

package com.wl.devlib.http.rxjava.subscribe;

import android.content.Context;


import com.wl.devlib.http.exception.ExceptionCode;
import com.wl.devlib.http.exception.ExceptionEngine;
import com.wl.devlib.http.model.BaseEntity;
import com.wl.devlib.http.utils.NetworkHelper;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public abstract class UploadSubscriber<T> implements Subscriber<Object> {

    protected Context mContext;

    public UploadSubscriber(Context context) {
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

        if (o instanceof BaseEntity) {
            BaseEntity baseModel = (BaseEntity) o;
            if (baseModel.isFailure()) {
                _onError(baseModel.getCode(), baseModel.getDesc());
            } else {
                if (baseModel.getData() != null) {
                    _onNext((T) baseModel.getData());
                }
            }
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

    protected abstract void _onNext(T result);

    protected abstract void _onProgress(Integer percent);

    protected abstract void _onError(int errorCode, String msg);


}

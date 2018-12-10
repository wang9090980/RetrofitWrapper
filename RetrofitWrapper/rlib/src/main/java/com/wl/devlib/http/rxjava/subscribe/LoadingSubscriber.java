package com.wl.devlib.http.rxjava.subscribe;

import android.content.Context;

import org.reactivestreams.Subscription;

public abstract class LoadingSubscriber<T> extends EasySubscriber<T> {

    public LoadingSubscriber(Context mContext) {
        super(mContext);
    }

    @Override
    public void onSubscribe(Subscription s) {
        super.onSubscribe(s);
        showLoadingView();
    }

    @Override
    public void onComplete() {
        super.onComplete();
        hideLoadingView();
    }

    @Override
    public void onError(Throwable e) {
        super.onError(e);
        hideLoadingView();
    }

    protected abstract void showLoadingView();

    protected abstract void hideLoadingView();

}

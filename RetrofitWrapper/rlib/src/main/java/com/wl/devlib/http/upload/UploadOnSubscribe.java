package com.wl.devlib.http.upload;


import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.annotations.NonNull;


public class UploadOnSubscribe implements FlowableOnSubscribe<Integer> {

    private FlowableEmitter<Integer> mObservableEmitter;
    private long mSumLength = 0l;
    private long uploaded = 0l;

    private int mPercent = 0;

    public UploadOnSubscribe(long sumLength) {
        this.mSumLength = sumLength;
    }

    public void onRead(long read) {
        uploaded += read;
        if (mSumLength <= 0) {
            onProgress(-1);
        } else {
            onProgress((int) (100 * uploaded / mSumLength));
        }
    }

    private void onProgress(int percent) {
        if (mObservableEmitter == null) { return; }
        if (percent == mPercent) { return; }
        mPercent = percent;
        if (percent >= 100) {
            percent = 100;
            mObservableEmitter.onNext(percent);
            mObservableEmitter.onComplete();
            return;
        }
        mObservableEmitter.onNext(percent);
    }

    @Override
    public void subscribe(@NonNull FlowableEmitter<Integer> e) throws Exception {
        this.mObservableEmitter = e;
    }

}

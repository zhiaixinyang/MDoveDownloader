package com.suapp.dcdownloader.retrofit.listener;

import android.support.annotation.NonNull;

import io.reactivex.observers.DisposableObserver;

/**
 * Created by zhaojing on 2018/3/5.
 */

public class DcCallbackSubscriber<T> extends DisposableObserver<T> {
    protected DcCallback mCallback;
    protected T mDownloadData;

    public DcCallbackSubscriber(@NonNull DcCallback callback) {
        mCallback = callback;
    }

    @Override
    public void onNext(T t) {
        mDownloadData = t;
        mCallback.onSuccess(t);
    }

    @Override
    public void onError(Throwable e) {
        mCallback.onFail(e.getMessage());
    }

    @Override
    public void onComplete() {

    }
}

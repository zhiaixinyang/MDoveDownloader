package com.suapp.dcdownloader.retrofit.listener;

import android.support.annotation.NonNull;

import com.suapp.dcdownloader.retrofit.model.DownLoadProgress;

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
        if (t instanceof DownLoadProgress && ((DownLoadProgress) t).getDownloadSize() <= 0
                && ((DownLoadProgress) t).getStream() != null) {
            mCallback.onPreDownload(t);
        }
        mDownloadData = t;
        mCallback.onProgress(t);
    }

    @Override
    public void onError(Throwable e) {
        mCallback.onFail(e.getMessage());
    }

    @Override
    public void onComplete() {

    }
}

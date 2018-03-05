package com.suapp.dcdownloader.retrofit.listener;

import android.support.annotation.NonNull;

/**
 * Created by zhaojing on 2018/3/5.
 */

public class DcDownloadSubscriber extends DcCallbackSubscriber<DcDownLoaderCallback> {
    public DcDownloadSubscriber(@NonNull DcCallback callback) {
        super(callback);
    }

    @Override
    public void onComplete() {
        super.onComplete();
        mCallback.onSuccess(super.mDownloadData);
    }
}

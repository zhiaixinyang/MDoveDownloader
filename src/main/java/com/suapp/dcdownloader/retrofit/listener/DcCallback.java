package com.suapp.dcdownloader.retrofit.listener;

import android.support.annotation.Nullable;

/**
 * Created by zhaojing on 2018/3/5.
 */

public abstract class DcCallback<T> {
    public abstract void onProgress(T progressData);

    public abstract void onSuccess(T progressData);

    public abstract void onFail(String errMsg);

    public abstract void onCancel(String url);

    public abstract void onPreDownload(T progressData);
}

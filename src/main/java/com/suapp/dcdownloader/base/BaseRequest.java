package com.suapp.dcdownloader.base;


import com.suapp.dcdownloader.retrofit.DownloadRequest;
import com.suapp.dcdownloader.retrofit.api.ApiService;
import com.suapp.dcdownloader.retrofit.listener.DcCallback;

import java.util.LinkedHashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;

/**
 * Created by zhaojing on 2018/3/5.
 */

public abstract class BaseRequest<R extends BaseRequest> {

    protected String mDownLoadUrl;//下载地址
    protected ApiService mApiService;
    protected Map<String, String> mParams = new LinkedHashMap<>();//请求参数


    protected R downloadUrl(String downLoadUrl) {
        if (downLoadUrl != null) {
            mDownLoadUrl = downLoadUrl;
        }
        return (R) this;
    }

    public abstract <T> void request(@NonNull DcCallback<T> callback);

    protected abstract Observable execute();
    protected abstract <T> void execute(@NonNull DcCallback<T> callback);


    protected abstract void initNet();
}

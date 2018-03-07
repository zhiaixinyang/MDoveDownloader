package com.suapp.dcdownloader.retrofit;

import android.content.Context;

import com.suapp.dcdownloader.base.BaseRequest;

/**
 * Created by zhaojing on 2018/3/5.
 */

public class RetrofitDownloader {
    public static DownloadRequest downloadUrl(Context context, String url) {
        return new DownloadRequest(context, url);
    }
}

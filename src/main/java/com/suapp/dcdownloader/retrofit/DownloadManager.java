package com.suapp.dcdownloader.retrofit;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.suapp.dcdownloader.utils.Singleton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.observers.DisposableObserver;

/**
 * Created by zhaojing on 2018/3/9.
 */

public class DownloadManager {
    private static List<String> mRunningReq;
    private static Map<String, DisposableObserver> mRunningReqMap;

    private static final Singleton<DownloadManager> INSTANCE = new Singleton<DownloadManager>() {
        @Override
        protected DownloadManager create() {
            return new DownloadManager();
        }
    };

    public static DownloadManager get() {
        return INSTANCE.get();
    }


    private DownloadManager() {
        mRunningReq = new ArrayList<>();
        mRunningReqMap = new HashMap<>();
    }

    public boolean addRequestUrl(@NonNull String url) {
        if (mRunningReq.contains(url)) {
            return false;
        }
        mRunningReq.add(url);
        return true;
    }

    public void addRequest(@NonNull String url, @NonNull DisposableObserver disposableObserver) {
        mRunningReqMap.put(url, disposableObserver);
    }

    //可以为null，如果为null，取消全部下载
    public void cancel(@Nullable String url) {
        if (TextUtils.isEmpty(url)) {
            cancelAll();
        }
        if (mRunningReqMap.containsKey(url)) {
            DisposableObserver disposableObserver = mRunningReqMap.get(url);
            if (!disposableObserver.isDisposed()) {
                disposableObserver.dispose();
            }
            mRunningReqMap.remove(url);
        }
    }

    public boolean isRunning(String url) {
        return mRunningReq.contains(url);
    }

    public void cancelAll() {
        for (Map.Entry<String, DisposableObserver> entry : mRunningReqMap.entrySet()) {
            DisposableObserver disposableObserver = entry.getValue();
            if (!disposableObserver.isDisposed()) {
                disposableObserver.dispose();
            }
        }
        mRunningReqMap.clear();
    }
}

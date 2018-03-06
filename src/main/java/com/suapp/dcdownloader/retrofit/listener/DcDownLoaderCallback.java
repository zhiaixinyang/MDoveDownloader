package com.suapp.dcdownloader.retrofit.listener;

import com.suapp.dcdownloader.retrofit.model.DownLoadProgress;

/**
 * Created by zhaojing on 2018/3/5.
 */

public abstract class DcDownLoaderCallback extends DcCallback<DownLoadProgress> {
    @Override
    public void onPreDownload(DownLoadProgress progressData) {
    }
}

package com.suapp.dcdownloader.httpurlconnect.listener;

/**
 * Created by zhaojing on 17/12/22.
 */

public interface DownloadStatusListener {
    void onInitErr();

    void onDownloadErr();

    void onStartInit();

    void downloadIsStart();

    void onStartDownload(String fileLocation,long fileLength);

    void onPercentProgress(int percent);

    void onProgress(long progress);

    void onDownloadSuccess();
}

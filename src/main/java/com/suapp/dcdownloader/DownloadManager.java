package com.suapp.dcdownloader;

import android.content.Context;

import com.suapp.dcdownloader.listener.DownloadStatusListener;
import com.suapp.dcdownloader.model.Request;
import com.suapp.dcdownloader.service.DownLoaderService;

/**
 * Created by zhaojing on 17/12/22.
 */

public class DownloadManager {
    private Request mRequest;
    private static DownloadStatusListener sDownloadStatusListener;

    private DownloadManager() {
    }

    public static void enqueue(Request request) {

    }

    public static void download(Context context, Request request) {
        if (sDownloadStatusListener != null) {
            DownLoaderService.setDownloadStatusListener(sDownloadStatusListener);
        }
        DownLoaderService.startDownLoad(context, request);
    }

    public static void setDownloadStatusListener(DownloadStatusListener downloadStatusListener) {
        sDownloadStatusListener = downloadStatusListener;
    }
}

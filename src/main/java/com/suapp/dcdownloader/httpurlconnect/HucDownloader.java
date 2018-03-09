package com.suapp.dcdownloader.httpurlconnect;

import android.content.Context;

import com.suapp.dcdownloader.httpurlconnect.listener.DownloadStatusListener;
import com.suapp.dcdownloader.httpurlconnect.model.Request;
import com.suapp.dcdownloader.httpurlconnect.service.DownLoaderService;

/**
 * Created by zhaojing on 17/12/22.
 */

public class HucDownloader {
    private Request mRequest;
    private static DownloadStatusListener sDownloadStatusListener;

    private HucDownloader() {
    }

    public static void enqueue(Request request) {

    }

    public static void download(Context context, Request request) {
        if (sDownloadStatusListener != null) {
            DownLoaderService.setDownloadStatusListener(sDownloadStatusListener);
        }
        DownLoaderService.startDownLoad(context, request);
    }

    public static void download(Context context, Request request, DownloadStatusListener downloadStatusListener) {
        DownLoaderService.setDownloadStatusListener(downloadStatusListener);
        DownLoaderService.startDownLoad(context, request);
    }

    public static void setDownloadStatusListener(DownloadStatusListener downloadStatusListener) {
        sDownloadStatusListener = downloadStatusListener;
    }
}

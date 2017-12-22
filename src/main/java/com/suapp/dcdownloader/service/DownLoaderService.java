package com.suapp.dcdownloader.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.suapp.dcdownloader.listener.DownloadStatusListener;
import com.suapp.dcdownloader.model.Request;
import com.suapp.dcdownloader.task.thread.InitDownloadFileThread;

import java.io.File;

/**
 * Created by zhaojing on 17/12/21.
 */

public class DownLoaderService extends Service {

    public static final String ACTION_START_DOWNLOAD_FILE = "action_start_download_file";
    public static final String ACTION_START_INIT_DOWNLOAD_FILE = "action_start_init_download_file";
    public static final String ACTION_DOWNLOAD_IS_START = "action_download_is_start";
    public static final String ACTION_UPDATE_PROGRESS_DOWNLOAD_FILE = "action_update_download_file";
    public static final String ACTION_PAUSE_DOWNLOAD_FILE = "action_pause_download_file";
    public static final String ACTION_CANCEL_DOWNLOAD_FILE = "action_cancel_download_file";
    public static final String ACTION_SUCCESS_DOWNLOAD_FILE = "action_success_download_file";
    public static final String ACTION_ERROR_INIT_DOWNLOAD_FILE = "action_error_init_download_file";
    public static final String ACTION_ERROR_DOWNLOAD_FILE = "action_error_download_file";

    public static final String EXTRA_DOWNLOAD_REQUEST = "extra_download_request";
    public static final String EXTRA_FILE_LOCATION = "extra_file_location";
    public static final String EXTRA_FILE_LENGTH = "extra_file_length";
    public static final String EXTRA_FILE_FINISHED_LENGTH = "extra_file_finished_length";
    public static boolean sIsStartDownload = false;
    private long mFileLength;

    private Request mRequest;
    private static DownloadStatusListener sDownloadStatusListener;

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_START_DOWNLOAD_FILE);
        intentFilter.addAction(ACTION_START_INIT_DOWNLOAD_FILE);
        intentFilter.addAction(ACTION_DOWNLOAD_IS_START);
        intentFilter.addAction(ACTION_UPDATE_PROGRESS_DOWNLOAD_FILE);
        intentFilter.addAction(ACTION_SUCCESS_DOWNLOAD_FILE);
        intentFilter.addAction(ACTION_ERROR_INIT_DOWNLOAD_FILE);
        intentFilter.addAction(ACTION_ERROR_DOWNLOAD_FILE);
        registerReceiver(mStatusReceiver, intentFilter);
    }

    public static void startDownLoad(Context context, @NonNull Request request) {
        Intent start = new Intent(context, DownLoaderService.class);
        start.setAction(ACTION_START_DOWNLOAD_FILE);
        start.putExtra(EXTRA_DOWNLOAD_REQUEST, request);
        context.startService(start);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            switch (intent.getAction()) {
                case ACTION_START_DOWNLOAD_FILE: {
                    Intent startInit = new Intent(ACTION_START_INIT_DOWNLOAD_FILE);
                    sendBroadcast(startInit);

                    if (!sIsStartDownload) {
                        sIsStartDownload = true;
                        mRequest = (Request) intent.getSerializableExtra(EXTRA_DOWNLOAD_REQUEST);
                        new InitDownloadFileThread(DownLoaderService.this, mRequest).start();
                    } else {
                        Intent isStart = new Intent(DownLoaderService.ACTION_DOWNLOAD_IS_START);
                        sendBroadcast(isStart);
                    }
                    break;
                }
                default: {
                    break;
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mStatusReceiver);
    }

    public static void setDownloadStatusListener(DownloadStatusListener downloadStatusListener) {
        sDownloadStatusListener = downloadStatusListener;
    }

    BroadcastReceiver mStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == null || sDownloadStatusListener == null) {
                return;
            }
            switch (intent.getAction()) {
                case DownLoaderService.ACTION_DOWNLOAD_IS_START: {
                    sDownloadStatusListener.downloadIsStart();
                    break;
                }
                case DownLoaderService.ACTION_START_DOWNLOAD_FILE: {
                    String fileLocation = intent.getStringExtra(EXTRA_FILE_LOCATION);
                    mFileLength = intent.getLongExtra(EXTRA_FILE_LENGTH, 0);
                    sDownloadStatusListener.onStartDownload(fileLocation, mFileLength);
                    break;
                }
                case DownLoaderService.ACTION_START_INIT_DOWNLOAD_FILE: {
                    sDownloadStatusListener.onStartInit();
                    break;
                }
                case DownLoaderService.ACTION_SUCCESS_DOWNLOAD_FILE: {
                    sDownloadStatusListener.onDownloadSuccess();
                    break;
                }
                case DownLoaderService.ACTION_UPDATE_PROGRESS_DOWNLOAD_FILE: {
                    if (mRequest == null) {
                        break;
                    } else {
                        long finish = intent.getLongExtra(EXTRA_FILE_FINISHED_LENGTH, 0);
                        sDownloadStatusListener.onProgress(finish);
                        if (mFileLength > 0) {
                            sDownloadStatusListener.onPercentProgress((int) ((float) finish / (float) mFileLength * 100));
                        }
                    }
                    break;
                }
                case DownLoaderService.ACTION_ERROR_DOWNLOAD_FILE: {
                    sDownloadStatusListener.onDownloadErr();
                    break;
                }
                case DownLoaderService.ACTION_ERROR_INIT_DOWNLOAD_FILE: {
                    sDownloadStatusListener.onInitErr();
                    break;
                }
            }
        }
    };
}

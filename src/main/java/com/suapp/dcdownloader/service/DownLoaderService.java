package com.suapp.dcdownloader.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.suapp.dcdownloader.model.Request;
import com.suapp.dcdownloader.task.thread.InitDownloadFileThread;

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
    public static final String EXTRA_FILE_FINISHED_LENGTH = "extra_file_finished_length";
    public static boolean sIsStartDownload = false;

    private Request mRequest;

    @Override
    public void onCreate() {
        super.onCreate();
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

}

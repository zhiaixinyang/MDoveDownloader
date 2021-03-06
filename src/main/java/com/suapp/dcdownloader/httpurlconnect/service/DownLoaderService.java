package com.suapp.dcdownloader.httpurlconnect.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.TextUtils;

import com.suapp.dcdownloader.httpurlconnect.Dispatcher;
import com.suapp.dcdownloader.httpurlconnect.listener.DownloadStatusListener;
import com.suapp.dcdownloader.httpurlconnect.model.Request;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.annotations.NonNull;

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
    public static final String EXTRA_FILE_URL = "extra_file_url";
    public static final String EXTRA_FILE_LENGTH = "extra_file_length";
    public static final String EXTRA_FILE_FINISHED_LENGTH = "extra_file_finished_length";
    private long mFileLength;
    private long mCurDownloadFileLength;

    private Request mRequest;
    private static DownloadStatusListener sDownloadStatusListener;
    private Dispatcher mDispatcher;
    private List<String> mDownloadingUrls;

    @Override
    public void onCreate() {
        super.onCreate();
        mDownloadingUrls = new ArrayList<>();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_START_DOWNLOAD_FILE);
        intentFilter.addAction(ACTION_START_INIT_DOWNLOAD_FILE);
        intentFilter.addAction(ACTION_DOWNLOAD_IS_START);
        intentFilter.addAction(ACTION_UPDATE_PROGRESS_DOWNLOAD_FILE);
        intentFilter.addAction(ACTION_SUCCESS_DOWNLOAD_FILE);
        intentFilter.addAction(ACTION_ERROR_INIT_DOWNLOAD_FILE);
        intentFilter.addAction(ACTION_ERROR_DOWNLOAD_FILE);
        registerReceiver(mStatusReceiver, intentFilter);

        mDispatcher = new Dispatcher();
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

                    if (!isExistUrl(mRequest.getFileUrl())) {
                        mCurDownloadFileLength = 0;
                        mRequest = (Request) intent.getSerializableExtra(EXTRA_DOWNLOAD_REQUEST);
                        mDispatcher.enqueue(DownLoaderService.this, mRequest);
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

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mStatusReceiver);
        if (sDownloadStatusListener != null) {
            sDownloadStatusListener = null;
        }
    }

    private boolean isExistUrl(String url) {
        for (String existUrl : mDownloadingUrls) {
            if (TextUtils.equals(existUrl, url)) {
                return true;
            }
        }
        return false;
    }

    public static void setDownloadStatusListener(DownloadStatusListener downloadStatusListener) {
        sDownloadStatusListener = downloadStatusListener;
    }

    private BroadcastReceiver mStatusReceiver = new BroadcastReceiver() {
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
                    String fileUrl = intent.getStringExtra(EXTRA_FILE_URL);
                    mDownloadingUrls.add(fileUrl);

                    sDownloadStatusListener.onStartDownload(fileLocation, mFileLength);
                    break;
                }
                case DownLoaderService.ACTION_START_INIT_DOWNLOAD_FILE: {
                    sDownloadStatusListener.onStartInit();
                    break;
                }
                case DownLoaderService.ACTION_SUCCESS_DOWNLOAD_FILE: {
                    String fileUrl = intent.getStringExtra(EXTRA_FILE_URL);
                    mDownloadingUrls.remove(fileUrl);
                    sDownloadStatusListener.onDownloadSuccess();
                    break;
                }
                case DownLoaderService.ACTION_UPDATE_PROGRESS_DOWNLOAD_FILE: {
                    if (mRequest == null) {
                        break;
                    } else {
                        int finish = intent.getIntExtra(EXTRA_FILE_FINISHED_LENGTH, 0);
                        mCurDownloadFileLength += finish;
                        if (mFileLength > 0) {
                            sDownloadStatusListener.onPercentProgress((int) (((float) mCurDownloadFileLength / (float) mFileLength) * 100));
                            sDownloadStatusListener.onProgress(mCurDownloadFileLength);

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

package com.suapp.dcdownloader.httpurlconnect.task;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;


import com.suapp.dcdownloader.httpurlconnect.model.FileInfo;
import com.suapp.dcdownloader.httpurlconnect.model.Request;
import com.suapp.dcdownloader.httpurlconnect.service.DownLoaderService;
import com.suapp.dcdownloader.httpurlconnect.task.base.IDownloadTask;
import com.suapp.dcdownloader.httpurlconnect.task.thread.AutoDownloadThread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by zhaojing on 2017/12/22.
 */

public class AutoDownloadTask implements IDownloadTask {
    private List<FileInfo> mFileBeans;
    private Context mContext;
    private final static int EXECUTOR_CORE_COUNT = 5;
    private ExecutorService downloadExecutor = Executors.newFixedThreadPool(EXECUTOR_CORE_COUNT);
    private long mLength;
    private Request mRequest;

    public AutoDownloadTask(Context context, @NonNull Request request, @NonNull long length) {
        mRequest = request;
        mContext = context;
        mLength = length;
    }

    private void autoDownload() {
        if (mRequest.getThreadCount() < 1) {
            //开始下载文件
            FileInfo fileInfo = new FileInfo(mRequest.getFileUrl(), 0, mLength, mLength, mRequest.getFileName(), mRequest.getFileLocation());
            List<FileInfo> fileInfos = new ArrayList<>();
            fileInfos.add(fileInfo);
        } else {
            //开始下载文件（多线程下载）
            long block = mLength % mRequest.getThreadCount() == 0 ? mLength / mRequest.getThreadCount()
                    : mLength / mRequest.getThreadCount() + 1;
            List<FileInfo> fileInfos = new ArrayList<>();
            for (int i = 0; i < mRequest.getThreadCount(); i++) {
                long start = i * block;
                long end = start + block >= mLength ? mLength : start + block - 1;
                FileInfo fileInfo = new FileInfo(mRequest.getFileUrl(), start, end, mLength, mRequest.getFileName(), mRequest.getFileLocation());
                fileInfos.add(fileInfo);
            }
        }

        for (FileInfo fileInfo : mFileBeans) {
            downloadExecutor.execute(new AutoDownloadThread(mContext, fileInfo));
        }
        //判断线程池任务是否全部完成
        downloadExecutor.shutdown();
        while (true) {
            if (downloadExecutor.isTerminated()) {
                Intent success = new Intent(DownLoaderService.ACTION_SUCCESS_DOWNLOAD_FILE);
                success.putExtra(DownLoaderService.EXTRA_FILE_URL, mRequest.getFileUrl());
                mContext.sendBroadcast(success);
                break;
            }
        }
    }

    @Override
    public void startDownload() {
        autoDownload();
    }
}

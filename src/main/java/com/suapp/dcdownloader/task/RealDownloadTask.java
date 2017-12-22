package com.suapp.dcdownloader.task;

import android.content.Context;
import android.content.Intent;


import com.suapp.dcdownloader.model.FileInfo;
import com.suapp.dcdownloader.service.DownLoaderService;
import com.suapp.dcdownloader.task.thread.AutoDownloadThread;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by zhaojing on 2017/12/22.
 */

public class RealDownloadTask {
    private List<FileInfo> mFileBeans;
    private Context mContext;
    private final static int EXECUTOR_CORE_COUNT = 5;
    private ExecutorService downloadExecutor = Executors.newFixedThreadPool(EXECUTOR_CORE_COUNT);

    public RealDownloadTask(Context context, List<FileInfo> fileBeans) {
        mFileBeans = fileBeans;
        mContext = context;
    }

    public void autoDownload() {
        for (FileInfo fileInfo : mFileBeans) {
            downloadExecutor.execute(new AutoDownloadThread(mContext, fileInfo));
        }
        //判断线程池任务是否全部完成
        downloadExecutor.shutdown();
        while (true) {
            if (downloadExecutor.isTerminated()) {
                Intent success = new Intent(DownLoaderService.ACTION_SUCCESS_DOWNLOAD_FILE);
                mContext.sendBroadcast(success);
                break;
            }
        }
    }
}

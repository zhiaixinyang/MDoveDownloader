package com.suapp.dcdownloader.task.thread;

import android.content.Context;
import android.content.Intent;

import com.suapp.dcdownloader.model.FileInfo;
import com.suapp.dcdownloader.model.Request;
import com.suapp.dcdownloader.service.DownLoaderService;
import com.suapp.dcdownloader.task.RealDownloadTask;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaojing on 17/12/22.
 */

public class InitDownloadFileThread extends Thread {
    private Context mContext;
    private Request mRequest;

    public InitDownloadFileThread(Context context, Request request) {
        mContext = context;
        mRequest = request;
    }

    @Override
    public void run() {
        HttpURLConnection conn = null;
        RandomAccessFile raf = null;
        try {
            //连接网络文件
            URL url = new URL(mRequest.getFileUrl());
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(3000);
            conn.setRequestMethod("GET");
            long length = -1;

            if (conn.getResponseCode() == 200 || conn.getResponseCode() == 206) {
                //获取文件长度
                length = conn.getContentLength();
            } else {
                Intent initErr = new Intent(DownLoaderService.ACTION_ERROR_INIT_DOWNLOAD_FILE);
                mContext.sendBroadcast(initErr);
                DownLoaderService.sIsStartDownload = false;
                return;
            }
            if (length < 0) {
                return;
            }
            File dir = new File(mRequest.getFileLocation());
            if (!dir.exists()) {
                dir.mkdir();
            }
            //在本地创建文件
            File file = new File(dir, mRequest.getFileName());
            raf = new RandomAccessFile(file, "rwd");
            //设置本地文件长度
            raf.setLength(length);

            Intent startDownload = new Intent(DownLoaderService.ACTION_START_DOWNLOAD_FILE);
            startDownload.putExtra(DownLoaderService.EXTRA_FILE_LOCATION, file.getAbsolutePath());
            startDownload.putExtra(DownLoaderService.EXTRA_FILE_LENGTH, length);
            mContext.sendBroadcast(startDownload);

            if (mRequest.getThreadCount() < 1) {
                //开始下载文件
                FileInfo fileInfo = new FileInfo(mRequest.getFileUrl(), 0, length, length, mRequest.getFileName(), mRequest.getFileLocation());
                List<FileInfo> fileInfos = new ArrayList<>();
                fileInfos.add(fileInfo);
                new RealDownloadTask(mContext, fileInfos).autoDownload();
            } else {
                //开始下载文件（多线程下载）
                long block = length % mRequest.getThreadCount() == 0 ? length / mRequest.getThreadCount()
                        : length / mRequest.getThreadCount() + 1;
                List<FileInfo> fileInfos = new ArrayList<>();
                for (int i = 0; i < mRequest.getThreadCount(); i++) {
                    long start = i * block;
                    long end = start + block >= length ? length : start + block - 1;
                    FileInfo fileInfo = new FileInfo(mRequest.getFileUrl(), start, end, length, mRequest.getFileName(), mRequest.getFileLocation());
                    fileInfos.add(fileInfo);
                }
                new RealDownloadTask(mContext, fileInfos).autoDownload();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Intent initErr = new Intent(DownLoaderService.ACTION_ERROR_INIT_DOWNLOAD_FILE);
            mContext.sendBroadcast(initErr);
            DownLoaderService.sIsStartDownload = false;
        } finally {
            try {
                if (conn != null) {
                    conn.disconnect();
                }
                if (raf != null) {
                    raf.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}

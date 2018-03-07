package com.suapp.dcdownloader.httpurlconnect.task.thread;

import android.content.Context;
import android.content.Intent;

import com.suapp.dcdownloader.httpurlconnect.model.FileInfo;
import com.suapp.dcdownloader.httpurlconnect.service.DownLoaderService;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static com.suapp.dcdownloader.httpurlconnect.service.DownLoaderService.EXTRA_FILE_FINISHED_LENGTH;

/**
 * Created by zhaojing on 17/12/22.
 */

public class AutoDownloadThread extends Thread {
    private FileInfo mFileInfo;
    private Context mContext;
    private long mFinishedLength;

    public AutoDownloadThread(Context context, FileInfo fileInfo) {
        mFileInfo = fileInfo;
        mContext = context;
    }

    @Override
    public void run() {
        HttpURLConnection conn = null;
        InputStream inputStream = null;
        RandomAccessFile raFile = null;
        try {
            URL url = new URL(mFileInfo.mFileUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            //设置下载开始位置
            conn.setRequestProperty("Range", "bytes=" + mFileInfo.mStartLocation + "-" + mFileInfo.mEndLocation);
            //设置文件存放位置
            File file = new File(mFileInfo.mFileLocation, mFileInfo.mFileName);
            raFile = new RandomAccessFile(file, "rwd");
            //设置从什么地方开始存内容。（跳过参数前面的位置）
            raFile.seek(mFileInfo.mStartLocation);

            Intent update = new Intent(DownLoaderService.ACTION_UPDATE_PROGRESS_DOWNLOAD_FILE);
            //开始下载（部分下载返回码为206）
            if (conn.getResponseCode() == 206 || conn.getResponseCode() == 200) {
                inputStream = conn.getInputStream();
                byte[] buffer = new byte[4096];
                int len = -1;
                int currTime = (int) System.currentTimeMillis();
                while ((len = inputStream.read(buffer)) != -1) {
                    raFile.write(buffer, 0, len);
                    if (System.currentTimeMillis() - currTime > 200) {
                        currTime = (int) System.currentTimeMillis();
                        update.putExtra(EXTRA_FILE_FINISHED_LENGTH, len);
                        mContext.sendBroadcast(update);
                    }
                }
            } else {
                Intent downloadErr = new Intent(DownLoaderService.ACTION_ERROR_DOWNLOAD_FILE);
                downloadErr.putExtra(DownLoaderService.EXTRA_FILE_URL, mFileInfo.mFileUrl);
                mContext.sendBroadcast(downloadErr);
                return;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Intent downloadErr = new Intent(DownLoaderService.ACTION_ERROR_DOWNLOAD_FILE);
            downloadErr.putExtra(DownLoaderService.EXTRA_FILE_URL, mFileInfo.mFileUrl);
            mContext.sendBroadcast(downloadErr);
        } catch (IOException e) {
            Intent downloadErr = new Intent(DownLoaderService.ACTION_ERROR_DOWNLOAD_FILE);
            downloadErr.putExtra(DownLoaderService.EXTRA_FILE_URL, mFileInfo.mFileUrl);
            mContext.sendBroadcast(downloadErr);
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.disconnect();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
                if (raFile != null) {
                    raFile.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}

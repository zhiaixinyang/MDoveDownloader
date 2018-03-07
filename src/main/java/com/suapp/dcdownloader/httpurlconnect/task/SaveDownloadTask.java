package com.suapp.dcdownloader.httpurlconnect.task;

import android.content.Context;
import android.content.Intent;

import com.suapp.dcdownloader.httpurlconnect.db.ThreadDownLoadDAOImpl;
import com.suapp.dcdownloader.httpurlconnect.model.FileInfo;
import com.suapp.dcdownloader.httpurlconnect.model.ThreadDownLoadInfo;
import com.suapp.dcdownloader.httpurlconnect.service.DownLoaderService;
import com.suapp.dcdownloader.httpurlconnect.task.base.IDownloadTask;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.suapp.dcdownloader.httpurlconnect.service.DownLoaderService.EXTRA_FILE_FINISHED_LENGTH;

/**
 * Created by zhaojing on 2017/1/11.
 */

public class SaveDownloadTask implements IDownloadTask {
    private FileInfo mFileBean;
    private Context mContext;
    private final static int EXECUTOR_CORE_COUNT = 5;
    private ExecutorService downloadExecutor = Executors.newFixedThreadPool(EXECUTOR_CORE_COUNT);
    private ThreadDownLoadDAOImpl mThreadDAO;
    private List<SaveDownloadThread> mThreadList = null;

    private long mFinished = 0;
    public boolean isPause = false;

    public SaveDownloadTask(Context context, FileInfo fileBean) {
        mFileBean = fileBean;
        mContext = context;
        mThreadDAO = new ThreadDownLoadDAOImpl(mContext);
    }

    private void saveDownload() {
        List<ThreadDownLoadInfo> threadInfos = mThreadDAO.getThreadList(mFileBean.mFileUrl);

        if (threadInfos.size() == 0) {
            //获得每个线程下载的长度
            long length = mFileBean.mFileLength / EXECUTOR_CORE_COUNT;
            for (int i = 0; i < EXECUTOR_CORE_COUNT; i++) {
                ThreadDownLoadInfo threadInfo = new ThreadDownLoadInfo(i, mFileBean.mFileUrl, length * i, (i + 1) * length - 1, 0);
                if (i + 1 == EXECUTOR_CORE_COUNT) {
                    threadInfo.mEndLength = mFileBean.mFileLength;
                }
                //添加到线程信息集合中
                threadInfos.add(threadInfo);

                //向数据库插入线程信息
                mThreadDAO.insertThread(threadInfo);
            }
        }
        mThreadList = new ArrayList<>();
        //启动多个线程进行下载
        for (ThreadDownLoadInfo thread : threadInfos) {
            SaveDownloadThread downloadThread = new SaveDownloadThread(thread);
            downloadExecutor.execute(downloadThread);
            //添加线程到集合中
            mThreadList.add(downloadThread);
        }
    }

    @Override
    public void startDownload() {
        saveDownload();
    }

    class SaveDownloadThread extends Thread {
        private ThreadDownLoadInfo mThreadInfo;
        public boolean isFinished = false;

        public SaveDownloadThread(ThreadDownLoadInfo thread) {
            mThreadInfo = thread;
        }

        @Override
        public void run() {
            HttpURLConnection connection;
            RandomAccessFile raf;
            InputStream is;
            try {
                URL url = new URL(mThreadInfo.mUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(3000);
                connection.setRequestMethod("GET");
                //设置下载位置
                long start = mThreadInfo.mStartLength + mThreadInfo.mEndLength;
                connection.setRequestProperty("Range", "bytes=" + start + "-" + mThreadInfo.mEndLength);
                //设置文件写入位置
                File file = new File(mFileBean.mFileLocation, mFileBean.mFileName);
                raf = new RandomAccessFile(file, "rwd");
                raf.seek(start);

                Intent update = new Intent(DownLoaderService.ACTION_UPDATE_PROGRESS_DOWNLOAD_FILE);
                mFinished += mThreadInfo.mFinishedLength;
                if (connection.getResponseCode() == HttpURLConnection.HTTP_PARTIAL) {
                    is = connection.getInputStream();
                    byte[] buffer = new byte[1024 * 4];
                    int len = -1;
                    long time = System.currentTimeMillis();
                    while ((len = is.read(buffer)) != -1) {
                        if (isPause) {
                            mThreadDAO.updateThread(mFileBean.mFileUrl, mThreadInfo.mId, mThreadInfo.mFinishedLength);
                            return;
                        }

                        raf.write(buffer, 0, len);
                        mFinished += len;
                        mThreadInfo.mFinishedLength = mThreadInfo.mFinishedLength + len;
                        if (System.currentTimeMillis() - time > 1000) {//减少UI负载
                            time = System.currentTimeMillis();

                            update.putExtra(EXTRA_FILE_FINISHED_LENGTH, len);
                            mContext.sendBroadcast(update);
                        }

                    }
                    //标识线程执行完毕
                    isFinished = true;
                    //检查下载任务是否完成
                    checkAllThreadFinished();
                    is.close();
                }
                raf.close();
                connection.disconnect();
            } catch (Exception e) {
                Intent downloadErr = new Intent(DownLoaderService.ACTION_ERROR_DOWNLOAD_FILE);
                downloadErr.putExtra(DownLoaderService.EXTRA_FILE_URL, mThreadInfo.mUrl);
                mContext.sendBroadcast(downloadErr);
                e.printStackTrace();
            }
        }
    }

    private synchronized void checkAllThreadFinished() {
        boolean allFinished = true;
        //编辑线程集合 判断是否执行完毕
        for (SaveDownloadThread thread : mThreadList) {
            if (!thread.isFinished) {
                allFinished = false;
                break;
            }
        }
        if (allFinished) {
            //删除线程信息
            mThreadDAO.deleteThread(mFileBean.mFileUrl);
            Intent success = new Intent(DownLoaderService.ACTION_SUCCESS_DOWNLOAD_FILE);
            success.putExtra(DownLoaderService.EXTRA_FILE_URL, mFileBean.mFileUrl);

            mContext.sendBroadcast(success);
        }
    }
}

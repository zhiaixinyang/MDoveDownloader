package com.suapp.dcdownloader.httpurlconnect.db;

import com.suapp.dcdownloader.httpurlconnect.model.ThreadDownLoadInfo;

import java.util.List;

/**
 * Created by zhaojing on 2017/12/21.
 */

public interface ThreadDownLoadDAO {
    void insertThread(ThreadDownLoadInfo threadDownLoadInfo);

    void deleteThread(String url, int threadId);

    void updateThread(String url, int threadId, long finishedLength);

    void deleteThread(String url);

    List<ThreadDownLoadInfo> getThreadList(String url);

    boolean isExists(String url, int threadId);
}

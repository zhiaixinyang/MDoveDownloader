package com.suapp.dcdownloader.httpurlconnect.model;

import java.io.Serializable;

/**
 * Created by zhaojing on 2017/12/21.
 */

public class ThreadDownLoadInfo implements Serializable {
    public int mId;
    public String mUrl;
    public long mStartLength;
    public long mEndLength;
    //已经下载了的进度
    public long mFinishedLength;

    public ThreadDownLoadInfo() {
    }

    public ThreadDownLoadInfo(int id, String url, long startLength, long endLength, long finishedLength) {
        mId = id;
        mUrl = url;
        mStartLength = startLength;
        mEndLength = endLength;
        mFinishedLength = finishedLength;
    }
}

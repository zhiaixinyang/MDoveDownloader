package com.suapp.dcdownloader.httpurlconnect.model;

import android.content.Context;
import android.text.TextUtils;

import java.io.File;
import java.io.Serializable;

import io.reactivex.annotations.NonNull;

/**
 * Created by zhaojing on 17/12/21.
 */

public class Request implements Serializable {
    private String mFileLocation;
    private String mFileUrl;
    private int mThreadCount;
    private String mFileName;
    private final static String sDefaultDirName = "DcDownloader";
    private DownloadMode mDownloadMode;

    public enum DownloadMode {
        AUTO,
        SAVE
    }

    public Request(Builder builder) {
        mFileUrl = builder.mFileUrl;
        mThreadCount = builder.mThreadCount;
        if (!TextUtils.isEmpty(builder.mFileLocation)) {
            mFileLocation = builder.mFileLocation;
        }
        mDownloadMode = builder.mMode;
        mFileName = builder.mFileName;
    }

    public String getFileUrl() {
        return mFileUrl;
    }

    public int getThreadCount() {
        return mThreadCount;
    }

    public String getFileLocation() {
        return mFileLocation;
    }

    public String getFileName() {
        return mFileName;
    }

    public DownloadMode getDownloadMode() {
        return mDownloadMode;
    }

    public static class Builder {
        private String mFileUrl;
        private int mThreadCount;
        private String mFileLocation;
        private String mFileName;
        private DownloadMode mMode;

        public Builder setFileUrl(@NonNull String fileUrl) {
            mFileUrl = fileUrl;
            return this;
        }

        public Builder setThreadCount(@NonNull int threadCount) {
            mThreadCount = threadCount;
            return this;
        }

        public Builder setDownloadMode(DownloadMode mode) {
            mMode = mode;
            return this;
        }

        public Builder setFileLocation(@NonNull String fileLocation) {
            mFileLocation = fileLocation;
            return this;
        }

        public Builder setFileLocation(@NonNull Context context) {
            File file = context.getExternalFilesDir(sDefaultDirName);
            if (!file.exists()) {
                file.mkdirs();
            }
            mFileLocation = file.getPath();
            return this;
        }

        public Builder setFileName(@NonNull String fileName) {
            mFileName = fileName;
            return this;
        }

        public Request builder() {
            return new Request(this);
        }
    }
}

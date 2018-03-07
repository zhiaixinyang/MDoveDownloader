package com.suapp.dcdownloader.httpurlconnect.model;

import java.io.Serializable;

/**
 * Created by zhaojing on 17/12/21.
 */

public class FileInfo implements Serializable {
    public String mFileUrl;
    public String mFileName;
    public String mFileLocation;
    public long mStartLocation;
    public long mEndLocation;
    public long mFileLength;


    public FileInfo(String fileUrl, long startLocation, long endLocation, long fileLength, String fileName, String fileLocation) {
        mFileUrl = fileUrl;
        mFileName = fileName;
        mFileLocation = fileLocation;
        mStartLocation = startLocation;
        mEndLocation = endLocation;
        mFileUrl = fileUrl;
        mFileLength = fileLength;
    }

}

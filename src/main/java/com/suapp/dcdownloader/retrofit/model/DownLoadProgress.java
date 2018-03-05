package com.suapp.dcdownloader.retrofit.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;
import java.math.BigInteger;
import java.text.NumberFormat;

/**
 * Created by zhaojing on 2018/3/5.
 */

public class DownLoadProgress implements Parcelable {
    private long mTotalSize;
    private long mDownloadSize;
    private File mDownloadFile;

    public DownLoadProgress() {
    }

    public DownLoadProgress(long totalSize, long downloadSize) {
        mTotalSize = totalSize;
        mDownloadSize = downloadSize;
    }

    protected DownLoadProgress(Parcel in) {
        mTotalSize = in.readLong();
        mDownloadSize = in.readLong();
    }

    public long getTotalSize() {
        return mTotalSize;
    }

    public DownLoadProgress setTotalSize(long totalSize) {
        mTotalSize = totalSize;
        return this;
    }

    public long getDownloadSize() {
        return mDownloadSize;
    }

    public DownLoadProgress setDownloadSize(long downloadSize) {
        mDownloadSize = downloadSize;
        return this;
    }

    public void setDownloadFile(File downloadFile) {
        mDownloadFile = downloadFile;
    }

    public File getDownloadFile() {
        return mDownloadFile;
    }

    /**
     * 是否下载完成
     *
     * @return
     */
    public boolean isDownComplete() {
        return mDownloadSize == mTotalSize;
    }

    /**
     * 获得格式化的总Size
     *
     * @return example: 2KB , 10MB
     */
    public String getFormatTotalSize() {
        return byteCountToDisplaySize(mTotalSize);
    }

    /**
     * 获得格式化的当前大小
     *
     * @return
     */
    public String getFormatDownloadSize() {
        return byteCountToDisplaySize(mDownloadSize);
    }

    /**
     * 获得格式化的状态字符串
     *
     * @return example: 6MB/66MB
     */
    public String getFormatStatusString() {
        return getFormatDownloadSize() + "/" + getFormatTotalSize();
    }

    /**
     * 获得下载的百分比, 保留两位小数
     *
     * @return example: 66.66%
     */
    public String getFormatPercent() {
        String percent;
        Double result;
        if (mTotalSize == 0L) {
            result = 0.0;
        } else {
            result = mDownloadSize * 1.0 / mTotalSize;
        }
        NumberFormat nf = NumberFormat.getPercentInstance();
        nf.setMinimumFractionDigits(2);//控制保留小数点后几位，2：表示保留2位小数点
        percent = nf.format(result);
        return percent;
    }

    public int getIntPercent() {
        int percent = 0;
        if (mTotalSize == 0L) {
            percent = 0;
        } else {
            percent = (int) (mDownloadSize * 100 / mTotalSize);
        }
        return percent;
    }

    public static final Creator<DownLoadProgress> CREATOR = new Creator<DownLoadProgress>() {
        @Override
        public DownLoadProgress createFromParcel(Parcel in) {
            return new DownLoadProgress(in);
        }

        @Override
        public DownLoadProgress[] newArray(int size) {
            return new DownLoadProgress[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(mTotalSize);
        parcel.writeLong(mDownloadSize);
    }

    public static final long ONE_KB = 1024;
    public static final BigInteger ONE_KB_BI = BigInteger.valueOf(ONE_KB);
    public static final BigInteger ONE_MB_BI = ONE_KB_BI.multiply(ONE_KB_BI);
    public static final BigInteger ONE_GB_BI = ONE_KB_BI.multiply(ONE_MB_BI);
    public static final BigInteger ONE_TB_BI = ONE_KB_BI.multiply(ONE_GB_BI);
    public static final BigInteger ONE_PB_BI = ONE_KB_BI.multiply(ONE_TB_BI);
    public static final BigInteger ONE_EB_BI = ONE_KB_BI.multiply(ONE_PB_BI);

    public static String byteCountToDisplaySize(BigInteger size) {
        String displaySize;

        if (size.divide(ONE_EB_BI).compareTo(BigInteger.ZERO) > 0) {
            displaySize = String.valueOf(size.divide(ONE_EB_BI)) + " EB";
        } else if (size.divide(ONE_PB_BI).compareTo(BigInteger.ZERO) > 0) {
            displaySize = String.valueOf(size.divide(ONE_PB_BI)) + " PB";
        } else if (size.divide(ONE_TB_BI).compareTo(BigInteger.ZERO) > 0) {
            displaySize = String.valueOf(size.divide(ONE_TB_BI)) + " TB";
        } else if (size.divide(ONE_GB_BI).compareTo(BigInteger.ZERO) > 0) {
            displaySize = String.valueOf(size.divide(ONE_GB_BI)) + " GB";
        } else if (size.divide(ONE_MB_BI).compareTo(BigInteger.ZERO) > 0) {
            displaySize = String.valueOf(size.divide(ONE_MB_BI)) + " MB";
        } else if (size.divide(ONE_KB_BI).compareTo(BigInteger.ZERO) > 0) {
            displaySize = String.valueOf(size.divide(ONE_KB_BI)) + " KB";
        } else {
            displaySize = String.valueOf(size) + " bytes";
        }
        return displaySize;
    }

    public static String byteCountToDisplaySize(long size) {
        return byteCountToDisplaySize(BigInteger.valueOf(size));
    }
}
package com.suapp.dcdownloader.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * Created by zhaojing on 2018/3/5.
 */

public class FileUtils {
    public static File getFileDir(String rootName, String dirName) {
        return new File(rootName + File.separator + dirName);
    }

    public static String getDiskCachePath(Context context) {
        String cachePath;
        if ((Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable())
                && context.getExternalCacheDir() != null) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return cachePath;
    }
}

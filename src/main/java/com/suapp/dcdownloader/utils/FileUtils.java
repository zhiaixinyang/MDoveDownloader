package com.suapp.dcdownloader.utils;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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

    public static void setPermissions(String path, int permissions) {
        ReflectUtils.callStaticMethod("android.os.FileUtils", "setPermissions", path, permissions, -1,
                -1);
    }

    public static boolean exists(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        File file = new File(path);
        return file.exists();
    }

    public static String readLine(String filePath) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(filePath));
            return reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(reader);
        }
        return "";
    }

    public static String readLine(InputStream is) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(is));
            return reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(reader);
        }
        return "";
    }
}

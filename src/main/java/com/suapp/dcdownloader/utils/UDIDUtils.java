package com.suapp.dcdownloader.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * @author xubin@jiandaola.com
 */
public class UDIDUtils {

    private static final String PREF_KEY_UDID = "udid";
    private static final String UDID_FILE_NAME = ".udid";
    private static final String UDID_FILE_PATH_SD_CARD = "/dailycast/.config/" + UDID_FILE_NAME;
    private static final byte[] LOCK_UDID = new byte[0];
    private static String UDID = null;

    public static String getUDID(Context context) {
        if (!TextUtils.isEmpty(UDID)) {
            return UDID;
        }
        synchronized (LOCK_UDID) {
            UDID = loadUDIDFromPrefs(context);
            if (isUDIDValid(context, UDID)) {
                asyncSaveUDID(context, UDID);
                return UDID;
            }
            UDID = loadUDIDFromInner(context);
            if (isUDIDValid(context, UDID)) {
                asyncSaveUDID(context, UDID);
                return UDID;
            }
            UDID = loadUDIDFromSDCard();
            if (isUDIDValid(context, UDID)) {
                asyncSaveUDID(context, UDID);
                return UDID;
            }
            UDID = generateUDID(context);
            asyncSaveUDID(context, UDID);
            return UDID;
        }
    }

    private static void asyncSaveUDID(final Context context, final String udid) {
        PriorityThreadPool.getInstance().executeBkgTask(new Runnable() {
            @Override
            public void run() {
                synchronized (LOCK_UDID) {
                    saveUDIDToPrefs(context, udid);
                    saveUDIDToInner(context, udid);
                    saveUDIDToSDCard(udid);
                }
            }
        });
    }

    private static String generateUDID(Context context) {
        String uuid = UUID.randomUUID().toString();
        uuid = uuid.replaceAll("-", "");
        NativeLibraryLoader.loadLibrarySafely(context, "dailycast");
        return generateUDIDNative(uuid);
    }

    private static native String generateUDIDNative(String udid);

    private static boolean isUDIDValid(Context context, String udid) {
        if (TextUtils.isEmpty(udid)) {
            return false;
        }
        NativeLibraryLoader.loadLibrarySafely(context, "dailycast");
        return isUDIDValidNative(udid);
    }

    private static native boolean isUDIDValidNative(String udid);

    private static String loadUDIDFromSDCard() {
        String filePath = getUDIDSDCardPath();
        if (!FileUtils.exists(filePath)) {
            return "";
        }
        return FileUtils.readLine(filePath);
    }

    private static void saveUDIDToSDCard(String udid) {
        String filePath = getUDIDSDCardPath();
        if (TextUtils.isEmpty(filePath)) {
            return;
        }
        File file = new File(filePath);
        try {
            //noinspection ResultOfMethodCallIgnored
            file.getParentFile().mkdirs();
            IOUtils.writeString(udid, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getUDIDSDCardPath() {
        if (SystemUtils.isSDCardMounted()) {
            return Environment.getExternalStorageDirectory().getAbsolutePath()
                    + UDID_FILE_PATH_SD_CARD;
        } else {
            return "";
        }
    }

    private static String loadUDIDFromInner(Context context) {
        FileInputStream is = null;
        try {
            is = context.openFileInput(UDID_FILE_NAME);
            return FileUtils.readLine(is);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(is);
        }
        return "";
    }

    private static void saveUDIDToInner(Context context, final String udid) {
        try {
            FileOutputStream os = context.openFileOutput(UDID_FILE_NAME, Context.MODE_PRIVATE);
            IOUtils.writeString(udid, os);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String loadUDIDFromPrefs(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref == null ? "" : pref.getString(PREF_KEY_UDID, "");
    }

    private static void saveUDIDToPrefs(final Context context, final String udid) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString(PREF_KEY_UDID, udid);
        editor.apply();
    }

}

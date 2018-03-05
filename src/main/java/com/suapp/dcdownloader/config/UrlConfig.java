package com.suapp.dcdownloader.config;

import com.suapp.dcdownloader.BuildConfig;

/**
 * Created by zhaojing on 2018/3/5.
 */

public class UrlConfig {
    private static final String HOST_API_ONLINE = "https://rock.dailycast.tv/";
    private static final String HOST_API_DEBUG = "http://54.201.208.127:8885/";

    public static String getHostApi() {
        if (BuildConfig.DEBUG) {
            return HOST_API_DEBUG;
        }
        return HOST_API_ONLINE;
    }

}

/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.suapp.dcdownloader.system;


import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.suapp.dcdownloader.system.utils.ArrayUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class RealSystemFacade implements SystemFacade {
    private Context mContext;

    public RealSystemFacade(Context context) {
        mContext = context;
    }

    @Override
    public long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    @Override
    public NetworkInfo getActiveNetworkInfo(int uid) {
        ConnectivityManager connectivity =
                (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            Log.w(Constants.TAG, "couldn't get connectivity manager");
            return null;
        }
        NetworkInfo activeInfo = null;
        try {
            //TODO 系统获取NetworkInfo被隐藏
            Method method = connectivity.getClass().getMethod("getActiveNetworkInfoForUid", int.class);
            activeInfo = (NetworkInfo) method.invoke(connectivity, uid);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        if (activeInfo == null && Constants.LOGVV) {
            Log.v(Constants.TAG, "network is not available");
        }
        return activeInfo;
    }

    @Override
    public boolean isActiveNetworkMetered() {
        final ConnectivityManager conn = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conn == null) {
            return false;
        }
        return conn.isActiveNetworkMetered();
    }

    @Override
    public boolean isNetworkRoaming() {
        ConnectivityManager connectivity =
                (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            Log.w(Constants.TAG, "couldn't get connectivity manager");
            return false;
        }

        NetworkInfo info = connectivity.getActiveNetworkInfo();
        boolean isMobile = (info != null && info.getType() == ConnectivityManager.TYPE_MOBILE);


        //TODO TelephonyManager.getDefault()被隐藏---
        TelephonyManager telephonyManager = null;
        try {
            Class<TelephonyManager> telephonyManagerClass = (Class<TelephonyManager>) Class.forName("android.telephony.TelephonyManager");
            Method method = telephonyManagerClass.getMethod("getDefault", new Class[]{});
            telephonyManager = (TelephonyManager) method.invoke(null, null);
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        if (telephonyManager==null){
            throw new NullPointerException("TelephonyManager invoke error , is NULL");
        }
        boolean isRoaming = isMobile && telephonyManager.isNetworkRoaming();
        //------

        if (Constants.LOGVV && isRoaming) {
            Log.v(Constants.TAG, "network is roaming");
        }
        return isRoaming;
    }

    @Override
    public Long getMaxBytesOverMobile() {
        return DownloadManager.getMaxBytesOverMobile(mContext);
    }

    @Override
    public Long getRecommendedMaxBytesOverMobile() {
        return DownloadManager.getRecommendedMaxBytesOverMobile(mContext);
    }

    @Override
    public void sendBroadcast(Intent intent) {
        mContext.sendBroadcast(intent);
    }

    @Override
    public boolean userOwnsPackage(int uid, String packageName) throws NameNotFoundException {
        return mContext.getPackageManager().getApplicationInfo(packageName, 0).uid == uid;
    }

    @Override
    public boolean isCleartextTrafficPermitted(int uid) {
        PackageManager packageManager = mContext.getPackageManager();
        String[] packageNames = packageManager.getPackagesForUid(uid);
        if (ArrayUtils.isEmpty(packageNames)) {
            // Unknown UID -- fail safe: cleartext traffic not permitted
            return false;
        }

        // Cleartext traffic is permitted from the UID if it's permitted for any of the packages
        // belonging to that UID.
        for (String packageName : packageNames) {
            if (isCleartextTrafficPermitted(packageName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns whether cleartext network traffic (HTTP) is permitted for the provided package.
     */
    private boolean isCleartextTrafficPermitted(String packageName) {
        PackageManager packageManager = mContext.getPackageManager();
        PackageInfo packageInfo;
        try {
            packageInfo = packageManager.getPackageInfo(packageName, 0);
        } catch (NameNotFoundException e) {
            // Unknown package -- fail safe: cleartext traffic not permitted
            return false;
        }
        ApplicationInfo applicationInfo = packageInfo.applicationInfo;
        if (applicationInfo == null) {
            // No app info -- fail safe: cleartext traffic not permitted
            return false;
        }
        return (applicationInfo.flags & ApplicationInfo.FLAG_USES_CLEARTEXT_TRAFFIC) != 0;
    }
}

package com.suapp.dcdownloader.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class NetworkUtils {

    public static final int NETWORK_TYPE_NONE = -1;
    public static final int NETWORK_TYPE_MOBILE = ConnectivityManager.TYPE_MOBILE;
    public static final int NETWORK_TYPE_WIFI = ConnectivityManager.TYPE_WIFI;

    private static final String ANDROID_HOTSPOT_IP_ADDRESS = "192.168.43.1";
    private static final String IOS_HOTSPOT_IP_ADDRESS = "172.20.10.1";

    // 采用 IOS 平台定义格式
    public static final String RESULT_TYPE_NONE = "none";
    public static final String RESULT_TYPE_GPRS = "GPRS";
    public static final String RESULT_TYPE_EDGE = "Edge";
    public static final String RESULT_TYPE_WCDMA = "WCDMA";//UMTS
    public static final String RESULT_TYPE_HSDPA = "HSDPA";
    public static final String RESULT_TYPE_HSUPA = "HSUPA";
    public static final String RESULT_TYPE_HSPA = "HSPA";
    public static final String RESULT_TYPE_IDEN = "iDen";
    public static final String RESULT_TYPE_CDMA1X = "CDMA1x";//CDMA
    public static final String RESULT_TYPE_1XRTT = "1xRTT";
    public static final String RESULT_TYPE_CDMAEVDOREV0 = "CDMAEVDORev0";//EVDO_0
    public static final String RESULT_TYPE_CDMAEVDOREVA = "CDMAEVDORevA";//EVDO_A
    public static final String RESULT_TYPE_CDMAEVDOREVB = "CDMAEVDORevB";//EVDO_B
    public static final String RESULT_TYPE_HRPD = "HRPD";//EHRPD
    public static final String RESULT_TYPE_LTE = "LTE";
    public static final String RESULT_TYPE_HSPAP = "HSPAP";//HSPA+
    public static final String RESULT_TYPE_WIFI = "WIFI";

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = null;
        try {
            activeNetworkInfo = connManager.getActiveNetworkInfo();
        } catch (Exception e) {
            // in some roms, here maybe throw a exception(like nullpoint).
            e.printStackTrace();
        }
        return (activeNetworkInfo != null && activeNetworkInfo.isConnected());
    }

    public static NetworkInfo getNetworkInfo(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        return connManager.getActiveNetworkInfo();
    }

    public static boolean isMobileNetworkConnected(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo =
                connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return networkInfo != null && networkInfo.isConnected();
    }

    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connManager == null) {
            return false;
        }
        NetworkInfo networkInfo = null;
        try {
            // maybe throw exception in android framework
            networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // can not use pingSupplicant (), on cm9 or some other roms it will
        // block whole wifi network!
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Convert a IPv4 address from an integer to an InetAddress.
     *
     * @param hostAddress is an Int corresponding to the IPv4 address in network byte order
     * @return the IP address as an {@code InetAddress}, returns null if
     * unable to convert or if the int is an invalid address.
     */
    private static InetAddress intToInetAddress(int hostAddress) {
        InetAddress inetAddress = null;
        byte[] addressBytes = {(byte) (0xff & hostAddress),
                (byte) (0xff & (hostAddress >> 8)),
                (byte) (0xff & (hostAddress >> 16)),
                (byte) (0xff & (hostAddress >> 24))};

        try {
            inetAddress = InetAddress.getByAddress(addressBytes);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return inetAddress;
    }

    /**
     * Check wifi is hotSpot or not.
     *
     * @return whether wifi is hotSpot or not.
     */
    public static boolean checkWifiIsHotSpot(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager == null) {
            return false;
        }
        DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
        if (dhcpInfo == null) {
            return false;
        }
        InetAddress address = intToInetAddress(dhcpInfo.gateway);
        if (address == null) {
            return false;
        }
        String currentGateway = address.getHostAddress();
        return TextUtils.equals(currentGateway, ANDROID_HOTSPOT_IP_ADDRESS)
                || TextUtils.equals(currentGateway, IOS_HOTSPOT_IP_ADDRESS);
    }

    public static int getNetworkType(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo;
        try {
            networkInfo = connManager.getActiveNetworkInfo();
        } catch (NullPointerException e) {
            // get some crash that getActiveNetworkInfo() may throw NullPointerException in some ROM...
            // so catch it here
            e.printStackTrace();
            return NETWORK_TYPE_NONE;
        }
        return parseNetworkType(networkInfo);
    }

    public static String parseNetworkTypeName(NetworkInfo networkInfo) {
        if (networkInfo == null || !networkInfo.isConnected()) {
            return RESULT_TYPE_NONE;
        }
        int type = networkInfo.getType();
        int subType = networkInfo.getSubtype();
        if (type == ConnectivityManager.TYPE_WIFI
                || type == ConnectivityManager.TYPE_WIMAX
                || type == ConnectivityManager.TYPE_ETHERNET) {
            return RESULT_TYPE_WIFI;
        }
        if (type == NETWORK_TYPE_MOBILE
                /* this patch for fix in some devices postType when apn connected, report postType is
                 * TYPE_BLUETOOTH and has subtype.  tested on CoolPad 7260+
                 */
                || (type == ConnectivityManager.TYPE_BLUETOOTH && subType > 0)) {
            switch (subType) {
                case TelephonyManager.NETWORK_TYPE_GPRS:
                    return RESULT_TYPE_GPRS;
                case TelephonyManager.NETWORK_TYPE_EDGE:
                    return RESULT_TYPE_EDGE;
                case TelephonyManager.NETWORK_TYPE_UMTS:
                    return RESULT_TYPE_WCDMA;
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    return RESULT_TYPE_CDMA1X;
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    return RESULT_TYPE_CDMAEVDOREV0;
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    return RESULT_TYPE_CDMAEVDOREVA;
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                    return RESULT_TYPE_1XRTT;
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                    return RESULT_TYPE_HSDPA;
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                    return RESULT_TYPE_HSUPA;
                case TelephonyManager.NETWORK_TYPE_HSPA:
                    return RESULT_TYPE_HSPA;
                case TelephonyManager.NETWORK_TYPE_IDEN:
                    return RESULT_TYPE_IDEN;
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    return RESULT_TYPE_CDMAEVDOREVB;
                case TelephonyManager.NETWORK_TYPE_LTE:
                    return RESULT_TYPE_LTE;
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                    return RESULT_TYPE_HRPD;
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                    return RESULT_TYPE_HSPAP;
                default:
                    return RESULT_TYPE_NONE;
            }

        } else {
            return RESULT_TYPE_NONE;
        }
    }

    public static int parseNetworkType(NetworkInfo networkInfo) {
        if (networkInfo == null || !networkInfo.isConnected()) {
            return NETWORK_TYPE_NONE;
        }
        if (networkInfo.getType() == NETWORK_TYPE_MOBILE) {
            return NETWORK_TYPE_MOBILE;
        } else {
            return NETWORK_TYPE_WIFI;
        }
    }

    /**
     * Get the network postType name. If currently connected to a mobile network, the detail mobile
     * network postType name will be returned.
     *
     * @param context
     * @return
     */
    public static String getNetworkTypeName(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        if (connManager == null) {
            return null;
        }
        try {
            // in some rom and a special time, it maybe throw NullPointer ex,
            // we have to catch it, and return a null value.
            final NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
            return parseNetworkTypeName(networkInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int[] getNetworkTypeInfo(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        int[] result = new int[]{NETWORK_TYPE_NONE, TelephonyManager.NETWORK_TYPE_UNKNOWN};
        if (connManager == null) {
            return result;
        }
        try {
            // in some rom and a special time, it maybe throw NullPointer ex,
            // we have to catch it, and return a null value.
            final NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
            if (networkInfo == null) {
                return result;
            } else if (networkInfo.getType() == NETWORK_TYPE_WIFI && networkInfo.isConnected()) {
                result[0] = NETWORK_TYPE_WIFI;
                return result;
            } else if (networkInfo.getType() == NETWORK_TYPE_MOBILE && networkInfo.isConnected()) {
                result[0] = NETWORK_TYPE_MOBILE;
                result[1] = networkInfo.getSubtype();
                return result;
            }
        } catch (Exception e) {
            return result;
        }
        return result;
    }

    public static String getISP(Context context) {
        try {
            TelephonyManager manager =
                    (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (manager == null) {
                return "unknown";
            }
            return manager.getNetworkOperatorName();
        } catch (Exception e) {
            return "unknown";
        }
    }

}

package com.suapp.dcdownloader.system.utils;

import android.util.ArrayMap;

import java.util.HashMap;

/**
 * Created by zhaojing on 2018/3/19.
 */

public class Maps {
    /**
     * Creates a {@code HashMap} instance.
     *
     * @return a newly-created, initially-empty {@code HashMap}
     */
    public static <K, V> HashMap<K, V> newHashMap() {
        return new HashMap<K, V>();
    }
    /**
     * Creates a {@code ArrayMap} instance.
     */
    public static <K, V> ArrayMap<K, V> newArrayMap() {
        return new ArrayMap<K, V>();
    }
}

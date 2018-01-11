package com.suapp.dcdownloader;

import android.content.Context;
import android.support.annotation.Nullable;

import com.suapp.dcdownloader.model.Request;
import com.suapp.dcdownloader.task.thread.InitDownloadFileThread;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhaojing on 18/1/11.
 */

public class Dispatcher {
    private @Nullable
    ExecutorService executorService;
    private final Deque<Request> runningAsyncCalls = new ArrayDeque<>();
    private final Deque<Request> readyAsyncCalls = new ArrayDeque<>();
    private int maxRequests = 64;

    public synchronized ExecutorService executorService() {
        if (executorService == null) {
            executorService = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60, TimeUnit.SECONDS,
                    new SynchronousQueue<Runnable>(), new ThreadFactory() {
                @Override
                public Thread newThread(Runnable runnable) {
                    Thread result = new Thread(runnable, "DC Dispatcher");
                    result.setDaemon(false);
                    return result;
                }
            });
        }
        return executorService;
    }

    public synchronized void enqueue(Context mContext,Request request) {
        if (runningAsyncCalls.size() < maxRequests) {
            runningAsyncCalls.add(request);
            executorService().execute(new InitDownloadFileThread(mContext, request));
        } else {
            readyAsyncCalls.add(request);
        }
    }
}

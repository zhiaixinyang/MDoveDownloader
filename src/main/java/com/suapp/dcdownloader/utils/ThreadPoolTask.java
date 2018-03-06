package com.suapp.dcdownloader.utils;

import android.os.Process;

/**
 * @author wangwei on 16/5/6
 *         wangwei@jiandaola.com
 */
public class ThreadPoolTask implements Runnable, Comparable<ThreadPoolTask> {

    private Runnable mRunable;
    /**
     * 高优先级
     */
    public static final int PRIORITY_HIGH = 4;
    /**
     * normal
     */
    public static final int PRIORITY_NORMAL = 5;
    /**
     * 低优先级
     */
    public static final int PRIORITY_LOW = 6;

    private int mPriority = PRIORITY_NORMAL;
    private long mQueuedTime;

    /**
     * Same to {@link ThreadPoolTask#ThreadPoolTask(Runnable, int)}
     *
     * @param r
     */
    public ThreadPoolTask(Runnable r) {
        mRunable = r;
    }

    /**
     * Construct a ThreadPoolTask object with specified target and priority.
     *
     * @param r
     * @param priority One of the priorities {@link #PRIORITY_NORMAL}, {@link #PRIORITY_LOW}
     *                 or {@link #PRIORITY_HIGH}
     */
    public ThreadPoolTask(Runnable r, int priority) {
        this.mRunable = r;
        this.mPriority = priority;
    }

    public Runnable getRunable() {
        return mRunable;
    }

    public void updateQueuedTime(long queuedTime) {
        mQueuedTime = queuedTime;
    }

    public int getPriority() {
        return mPriority;
    }

    @Override
    public int compareTo(ThreadPoolTask another) {
        if (mPriority < another.mPriority) {
            return -1;
        } else if (mPriority > another.mPriority) {
            return 1;
        } else {
            if (mQueuedTime < another.mQueuedTime) {
                return -1;
            } else if (mQueuedTime > another.mQueuedTime) {
                return 1;
            }
            return 0;
        }
    }

    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        mRunable.run();
    }
}

package com.suapp.dcdownloader.utils;



import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author wangwei on 16/5/6
 *         wangwei@jiandaola.com
 */
public class PriorityThreadPool {

    private static final String TAG ="PriorityThreadPool";
    private ThreadPoolExecutor mUiThreadPoolExecutor;
    private ThreadPoolExecutor mBkgThreadPoolExecutor;

    public static PriorityThreadPool getInstance() {
        return SingletonInstanceHolder.INSTANCE;
    }

    private PriorityThreadPool() {
        BlockingQueue<Runnable> blockingQueue = new PriorityBlockingQueue<Runnable>();
        int uiThreadSize = getInitUiThreadPoolSize();
        int bkgThreadSize = 2;
        mUiThreadPoolExecutor = new ThreadPoolExecutor(uiThreadSize, uiThreadSize, 1, TimeUnit.SECONDS, blockingQueue, new ThreadPoolFactory(true));
        mBkgThreadPoolExecutor = new ThreadPoolExecutor(bkgThreadSize, bkgThreadSize, 1, TimeUnit.SECONDS, blockingQueue, new ThreadPoolFactory(false));
    }

    /**
     * 启动之后，可以将线程池调大一些
     */
    public void expandThreadPoolSize() {
        int cpuCores = Runtime.getRuntime().availableProcessors();
        int uiPoolSize = Math.max(4, cpuCores / 2);
        mUiThreadPoolExecutor.setCorePoolSize(uiPoolSize);
        mUiThreadPoolExecutor.setMaximumPoolSize(uiPoolSize);
        int bkgPoolSize = Math.max(4, cpuCores / 2);
        mBkgThreadPoolExecutor.setCorePoolSize(bkgPoolSize);
        mBkgThreadPoolExecutor.setMaximumPoolSize(bkgPoolSize);
    }

    private int getInitUiThreadPoolSize() {
        int cpuCores = Runtime.getRuntime().availableProcessors();
        return Math.max(2, cpuCores / 2);
    }

    /**
     * 执行一个和UI相关的Task
     * @param r
     */
    public void executeUiTask(Runnable r) {
        ThreadPoolTask task = new ThreadPoolTask(r);
        task.updateQueuedTime(System.currentTimeMillis());
        mUiThreadPoolExecutor.execute(task);
    }

    /**
     * 执行一个和UI相关的Task，可以定义排队优先级
     * @param r
     * @param priority One of the priorities {@link ThreadPoolTask#PRIORITY_NORMAL},
     *                 {@link ThreadPoolTask#PRIORITY_LOW} or {@link ThreadPoolTask#PRIORITY_HIGH}
     */
    public void executeUiTask(Runnable r, int priority) {
        ThreadPoolTask task = new ThreadPoolTask(r, priority);
        task.updateQueuedTime(System.currentTimeMillis());
        mUiThreadPoolExecutor.execute(task);
    }

    /**
     * 执行一个与UI无关的Task
     * @param r
     */
    public void executeBkgTask(Runnable r) {
        ThreadPoolTask task = new ThreadPoolTask(r);
        task.updateQueuedTime(System.currentTimeMillis());
        mBkgThreadPoolExecutor.execute(task);
    }

    /**
     * 执行一个和UI无关的Task，可以定义排队优先级
     *
     * @param r
     * @param priority One of the priorities {@link ThreadPoolTask#PRIORITY_NORMAL},
     *                 {@link ThreadPoolTask#PRIORITY_LOW} or {@link ThreadPoolTask#PRIORITY_HIGH}
     */
    public void executeBkgTask(Runnable r, int priority) {
        ThreadPoolTask task = new ThreadPoolTask(r, priority);
        task.updateQueuedTime(System.currentTimeMillis());
        mBkgThreadPoolExecutor.execute(task);
    }

    private static class ThreadPoolFactory implements ThreadFactory {

        private AtomicInteger mCount = new AtomicInteger(1);
        private boolean isUiTask;

        public ThreadPoolFactory(boolean uiTask) {
            isUiTask = uiTask;
        }

        @Override
        public Thread newThread(final Runnable r) {
            if (isUiTask) {
                return new Thread(r, "PriorityUiThreadPool#" + mCount.getAndIncrement());
            } else {
                return new Thread(r, "PriorityBkgThreadPool#" + mCount.getAndIncrement());
            }
        }
    }

    private static class SingletonInstanceHolder {
        public static final PriorityThreadPool INSTANCE = new PriorityThreadPool();
    }
}

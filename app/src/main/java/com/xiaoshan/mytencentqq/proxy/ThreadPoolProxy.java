package com.xiaoshan.mytencentqq.proxy;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by xiaoshan on 2016/2/18.
 * 19:29
 */
public class ThreadPoolProxy {

    private ThreadPoolExecutor mThreadPoolExecutor;
    private int mCorePooSize;
    private int mMaximumPoolSize;
    private long mKeepAliveTime;

    public ThreadPoolProxy(int corePooSize, int maximumPoolSize, long keepAliveTime) {
        mCorePooSize = corePooSize;
        mMaximumPoolSize = maximumPoolSize;
        mKeepAliveTime = keepAliveTime;
    }

    public ThreadPoolExecutor getThreadPoolExecutor() {

        if (mThreadPoolExecutor == null) {
            synchronized (ThreadPoolProxy.class) {
                if (mThreadPoolExecutor == null) {
                    TimeUnit unit = TimeUnit.MILLISECONDS;
                    BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>();
                    ThreadFactory threadFactory = Executors.defaultThreadFactory();
                    RejectedExecutionHandler handler = new ThreadPoolExecutor.AbortPolicy();

                    mThreadPoolExecutor = new ThreadPoolExecutor(
                            mCorePooSize,
                            mMaximumPoolSize,
                            mKeepAliveTime,
                            unit,
                            workQueue,
                            threadFactory,
                            handler);
                }
            }
        }

        return mThreadPoolExecutor;
    }

}

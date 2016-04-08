package com.xiaoshan.mytencentqq.activity.factory;


import com.xiaoshan.mytencentqq.activity.proxy.ThreadPoolProxy;

/**
 * Created by xiaoshan on 2016/2/18.
 * 19:53
 */
public class ThreadPoolFactory {

    private static ThreadPoolProxy mNormalThreadPoolProxy;

    public static ThreadPoolProxy getNormalThreadPool() {
        if (mNormalThreadPoolProxy == null) {
            synchronized (ThreadPoolProxy.class) {
                if (mNormalThreadPoolProxy == null) {
                    mNormalThreadPoolProxy = new ThreadPoolProxy(5, 5, 3000);
                }
            }
        }

        return mNormalThreadPoolProxy;
    }
}

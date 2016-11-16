package com.liao.momo.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2016-10-20.
 */
public class ThreadUtil {

    private static ExecutorService executorService;

    /**
     * 线程池
     * 开启10条线程
     * @param task 任务
     */
    public static void executeThread(Runnable task){
        if (executorService==null) {
            executorService = Executors.newFixedThreadPool(10);
        }
        executorService.submit(task);
    }
}

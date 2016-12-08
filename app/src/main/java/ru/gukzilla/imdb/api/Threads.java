package ru.gukzilla.imdb.api;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Evgeniy on 08.12.2016.
 */

public class Threads {

    final static int THREADS_LIMIT = 5;

    private static ThreadPoolExecutor threadExecutor = null;

    public static ThreadPoolExecutor getService() {
        if(threadExecutor == null) {
            threadExecutor = new ThreadPoolExecutor(THREADS_LIMIT, THREADS_LIMIT,
                    0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>());
        }
        return threadExecutor;
    }

    public static void run(Runnable thread) {
        getService().execute(thread);
    }


    private static ExecutorService threadExecutorQueue = null;

    public static ExecutorService getServicePool() {
        if(threadExecutorQueue == null) {
            threadExecutorQueue = Executors.newSingleThreadExecutor();
        }
        return threadExecutorQueue;
    }

    public static void runInQueue(Runnable thread) {
        getServicePool().execute(thread);
    }

}

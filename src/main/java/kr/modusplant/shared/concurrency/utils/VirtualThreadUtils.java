package kr.modusplant.shared.concurrency.utils;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public abstract class VirtualThreadUtils {
    private static final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

    public static <T> Future<T> submit(Callable<T> callable) {
        return executorService.submit(callable);
    }
}

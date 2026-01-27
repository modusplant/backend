package kr.modusplant.shared.concurrency.utils;

import java.util.concurrent.*;

public abstract class VirtualThreadUtils {
    private static final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

    public static <T> Future<T> submit(Callable<T> callable) {
        return executorService.submit(callable);
    }

    public static <T> T submitAndGet(Callable<T> callable) {
        try {
            return executorService.submit(callable).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while getting from the Future object: ", e);
        } catch (ExecutionException e) {
            throw (e.getCause() instanceof RuntimeException runtimeException) ?
                    runtimeException : new RuntimeException(e.getCause());
        }
    }
}

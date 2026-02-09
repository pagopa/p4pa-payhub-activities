package it.gov.pagopa.payhub.activities.util;

import org.slf4j.MDC;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class ThreadUtils {
    private ThreadUtils() {}

    /** It will configure on the provided {@link Runnable} the thread context needed */
    public static Runnable configureThreadContext(Runnable runnable) {
        Map<String, String> mdcContext = MDC.getCopyOfContextMap();
        return configureThreadContext(runnable, mdcContext);
    }
    /** It will configure on the provided {@link Runnable} the thread context needed */
    public static Runnable configureThreadContext(Runnable runnable, Map<String, String> mdcContext) {
        return () -> {
            MDC.setContextMap(mdcContext);
            runnable.run();
        };
    }

    /** It will configure on the provided {@link Callable} the thread context needed */
    public static <T> Callable<T> configureThreadContext(Callable<T> callable) {
        Map<String, String> mdcContext = MDC.getCopyOfContextMap();
        return configureThreadContext(callable, mdcContext);
    }

    /** It will configure on the provided {@link Callable} the thread context needed */
    public static <T> Callable<T> configureThreadContext(Callable<T> callable, Map<String, String> mdcContext) {
        return () -> {
            MDC.setContextMap(mdcContext);
            return callable.call();
        };
    }

    @SuppressWarnings("java:S1452") // Generic wildcard usage is appropriate here
    public static Future<?> submit(ExecutorService executorService, Runnable task) {
        return executorService.submit(configureThreadContext(task));
    }

    @SuppressWarnings("java:S1452") // Generic wildcard usage is appropriate here
    public static Future<?> submit(ExecutorService executorService, Runnable task, Map<String, String> mdcContext) {
        return executorService.submit(configureThreadContext(task, mdcContext));
    }

    public static <T> Future<T> submit(ExecutorService executorService, Callable<T> task) {
        return executorService.submit(configureThreadContext(task));
    }

    public static <T> Future<T> submit(ExecutorService executorService, Callable<T> task, Map<String, String> mdcContext) {
        return executorService.submit(configureThreadContext(task, mdcContext));
    }
}

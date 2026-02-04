package it.gov.pagopa.payhub.activities.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import java.util.Map;
import java.util.concurrent.*;

class ThreadUtilsTest {

    @BeforeEach
    void init(){
        MDC.put("PROVA", "VALORE");
    }

    @AfterEach
    void clear(){
        MDC.clear();
    }

    @Test
    void whenConfigureThreadContextWithRunnableThenSuccess() throws ExecutionException, InterruptedException {
        // Given
        Map<String, String> contextMap = MDC.getCopyOfContextMap();
        String threadName = Thread.currentThread().getName();
        Runnable runnable = buildRunnableTest(contextMap, threadName);

        // When
        Runnable result = ThreadUtils.configureThreadContext(runnable);

        // Then
        try(ExecutorService executorService = Executors.newSingleThreadExecutor()){
            Future<?> task = executorService.submit(result);

            task.get();
        }
    }

    private static Runnable buildRunnableTest(Map<String, String> contextMap, String threadName) {
        return () -> {
            Assertions.assertEquals(MDC.getCopyOfContextMap(), contextMap);
            Assertions.assertNotEquals(threadName, Thread.currentThread().getName());
        };
    }

    @Test
    void whenConfigureThreadContextWithCallableThenSuccess() throws ExecutionException, InterruptedException {
        // Given
        Map<String, String> contextMap = MDC.getCopyOfContextMap();
        String threadName = Thread.currentThread().getName();
        Callable<String> callable = buildCallableTest(contextMap, threadName);

        // When
        Callable<String> result = ThreadUtils.configureThreadContext(callable);

        // Then
        try(ExecutorService executorService = Executors.newSingleThreadExecutor()){
            Future<String> task = executorService.submit(result);

            Assertions.assertEquals("OK", task.get());
        }
    }

    private static Callable<String> buildCallableTest(Map<String, String> contextMap, String threadName) {
        return () -> {
            buildRunnableTest(contextMap, threadName).run();
            return "OK";
        };
    }

    @Test
    void whenSubmitWithRunnableThenSuccess() throws ExecutionException, InterruptedException {
        // Given
        Map<String, String> contextMap = MDC.getCopyOfContextMap();
        String threadName = Thread.currentThread().getName();
        Runnable runnable = buildRunnableTest(contextMap, threadName);

        try(ExecutorService executorService = Executors.newSingleThreadExecutor()){
            // When
            Future<?> task = ThreadUtils.submit(executorService, runnable);

            // Then
            task.get();
        }
    }

    @Test
    void whenSubmitWithRunnableAndMdcContextThenSuccess() throws ExecutionException, InterruptedException {
        // Given
        Map<String, String> contextMap = MDC.getCopyOfContextMap();
        String threadName = Thread.currentThread().getName();
        Runnable runnable = buildRunnableTest(contextMap, threadName);

        try(ExecutorService executorService = Executors.newSingleThreadExecutor()){
            // When
            Future<?> task = ThreadUtils.submit(executorService, runnable, contextMap);

            // Then
            task.get();
        }
    }

    @Test
    void whenSubmitWithCallableThenSuccess() throws ExecutionException, InterruptedException {
        // Given
        Map<String, String> contextMap = MDC.getCopyOfContextMap();
        String threadName = Thread.currentThread().getName();
        Callable<String> callable = buildCallableTest(contextMap, threadName);

        try(ExecutorService executorService = Executors.newSingleThreadExecutor()){
            // When
            Future<String> task = ThreadUtils.submit(executorService, callable);

            // Then
            Assertions.assertEquals("OK", task.get());
        }
    }

    @Test
    void whenSubmitWithCallableAndMdcContextThenSuccess() throws ExecutionException, InterruptedException {
        // Given
        Map<String, String> contextMap = MDC.getCopyOfContextMap();
        String threadName = Thread.currentThread().getName();
        Callable<String> callable = buildCallableTest(contextMap, threadName);

        try(ExecutorService executorService = Executors.newSingleThreadExecutor()){
            // When
            Future<String> task = ThreadUtils.submit(executorService, callable, contextMap);

            // Then
            Assertions.assertEquals("OK", task.get());
        }
    }
}

package com.xwintop.xJavaFxTool.utils;

import com.xwintop.xJavaFxTool.utils.func.AsyncResult;
import com.xwintop.xJavaFxTool.utils.func.Future;
import com.xwintop.xJavaFxTool.utils.func.HandleFunc;
import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Code that Changed the World
 *
 * @author Pro
 * @date 2022/3/18
 */
@Slf4j
public final class ThreadPoolUtil {

    /**
     * 线程池
     */
    private static final ExecutorService executorService = Executors.newFixedThreadPool(1);

    public static void run(Runnable runnable) {
        executorService.submit(runnable);
    }

    public static <T> void run(HandleFunc<Future<T>> futureHandleFunc, HandleFunc<AsyncResult<T>> resultHandleFunc) {
        executorService.submit(() -> {
            Future<T> future = new Future<>();
            try {
                futureHandleFunc.handle(future);
            } catch (Throwable throwable) {
                log.error("线程池执行出错: ", throwable);
                future.setThrowable(throwable);
            }
            Platform.runLater(() -> resultHandleFunc.handle(future));
        });
    }

}

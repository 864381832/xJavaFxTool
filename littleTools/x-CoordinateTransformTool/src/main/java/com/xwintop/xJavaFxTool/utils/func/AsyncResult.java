package com.xwintop.xJavaFxTool.utils.func;

/**
 * Code that Changed the World
 *
 * @author Pro
 * @date 2022/3/19
 */
public class AsyncResult<T> {

    protected boolean isSuccess = true;

    protected T result;

    protected String message;

    protected Throwable error;

    public boolean isSuccess() {
        return isSuccess;
    }

    public T result() {
        return result;
    }

    public String message() {
        return message;
    }

    public Throwable error() {
        return error;
    }

}

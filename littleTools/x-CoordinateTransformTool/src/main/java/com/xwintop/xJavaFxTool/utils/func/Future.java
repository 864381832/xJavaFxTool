package com.xwintop.xJavaFxTool.utils.func;

/**
 * Code that Changed the World
 *
 * @author Pro
 * @date 2022/3/19
 */
public class Future<T> extends AsyncResult<T> {

    public void setResult(T result) {
        this.result = result;
    }

    public void setSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setThrowable(Throwable throwable) {
        this.isSuccess = false;
        this.error = throwable;
    }

}

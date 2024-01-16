package com.xwintop.xcore;

public class XCoreException extends RuntimeException {

    public XCoreException() {
    }

    public XCoreException(String message) {
        super(message);
    }

    public XCoreException(String message, Throwable cause) {
        super(message, cause);
    }

    public XCoreException(Throwable cause) {
        super(cause);
    }
}

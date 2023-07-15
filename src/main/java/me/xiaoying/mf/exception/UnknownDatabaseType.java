package me.xiaoying.mf.exception;

public class UnknownDatabaseType extends Exception {
    public UnknownDatabaseType() {
        super();
    }

    public UnknownDatabaseType(String message) {
        super(message);
    }

    public UnknownDatabaseType(String message, Throwable cause) {
        super(message, cause);
    }

    public UnknownDatabaseType(Throwable cause) {
        super(cause);
    }

    protected UnknownDatabaseType(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
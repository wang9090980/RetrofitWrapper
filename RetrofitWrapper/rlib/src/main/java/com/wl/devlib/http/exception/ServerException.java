package com.wl.devlib.http.exception;


public class ServerException extends Exception {

    public static final int ERROR_NETWORK = -1;
    public static final int ERROR_OTHER = -2;

    private int errorCode = ERROR_OTHER;

    public int getErrorCode() {
        return errorCode;
    }

    public ServerException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

}

package com.wl.devlib.http.utils;

import android.os.Looper;

/**
 * 检查工具类
 */
public class CheckHelper {

    /**
     * 手机号的校验规则
     */
    public final static String PHONE_REX = "^((13[0-9])|(15[^4,\\\\d])|(18[0-9])|(14[0-9])|(17[0-9])|(19[8-9])|(166))\\d{8}$";

    private CheckHelper() {
        throw new UnsupportedOperationException("Instantiation operation is not supported.");
    }

    public static <T> T checkNotNull(T object, String message) {
        if (object == null) {
            throw new NullPointerException(message);
        }
        return object;
    }

    public static boolean checkMainThread() {
        return Thread.currentThread() == Looper.getMainLooper().getThread();
    }


}

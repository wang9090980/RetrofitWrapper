package com.wl.devlib.http.exception;


import com.wl.devlib.http.model.BaseEntity;

import java.io.IOException;

/**
 * Token校验异常
 *
 */
public class TokenCheckException extends IOException {

    private BaseEntity entity;

    public TokenCheckException(String message, BaseEntity entity) {
        super(message);
        this.entity = entity;
    }

    public BaseEntity getEntity() {
        return entity;
    }

}

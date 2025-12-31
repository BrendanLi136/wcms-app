package com.wcms.common.exception;

import lombok.Getter;

@Getter
public class ServiceException extends RuntimeException {
    private final Integer code;

    public ServiceException(String message) {
        this(500, message);
    }

    public ServiceException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}

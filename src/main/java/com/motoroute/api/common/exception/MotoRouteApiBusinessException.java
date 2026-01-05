package com.motoroute.api.common.exception;

import lombok.Getter;

@Getter
public class MotoRouteApiBusinessException extends RuntimeException {

    private final String messageKey;
    private final Object[] messageArgs;

    public MotoRouteApiBusinessException(String messageKey) {
        super(messageKey);
        this.messageKey = messageKey;
        this.messageArgs = new Object[0];
    }

    public MotoRouteApiBusinessException(String messageKey, Object... messageArgs) {
        super(messageKey);
        this.messageKey = messageKey;
        this.messageArgs = messageArgs;
    }

    public MotoRouteApiBusinessException(String messageKey, Throwable cause) {
        super(messageKey, cause);
        this.messageKey = messageKey;
        this.messageArgs = new Object[0];
    }
}

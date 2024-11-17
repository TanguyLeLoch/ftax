package com.natu.ftax.common.exception;

import java.text.MessageFormat;

public class AuthenticationException extends RuntimeException {

    public AuthenticationException(String message, Object... params) {
        super(MessageFormat.format(message, params));
    }
}
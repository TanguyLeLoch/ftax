package com.natu.ftax.common.exception;

import java.text.MessageFormat;

public class FunctionalException extends RuntimeException {
    public FunctionalException(String message, Object... params) {
        super(MessageFormat.format(message, params));
    }

}
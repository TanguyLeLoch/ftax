package com.natu.ftax.common.exception;

import java.text.MessageFormat;

public class TechnicalException extends RuntimeException {
    public TechnicalException(String message, Object... params) {
        super(MessageFormat.format(message, params));
    }
}
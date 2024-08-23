package com.natu.ftax.common.exception;

import jakarta.validation.constraints.NotNull;

public record ExceptionResponse(@NotNull String message) {
}
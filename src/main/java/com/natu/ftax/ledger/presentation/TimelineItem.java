package com.natu.ftax.ledger.presentation;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class TimelineItem {

    @NotNull
    private final Instant dateTime;
    @Getter
    @NotNull
    private final BigDecimal amount;

    public TimelineItem(Instant dateTime, BigDecimal amount) {
        this.dateTime = dateTime;
        this.amount = amount;
    }

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    public ZonedDateTime getDateTime() {
        return dateTime.atZone(ZoneId.of("UTC"));
    }


}

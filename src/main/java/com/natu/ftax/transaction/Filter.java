package com.natu.ftax.transaction;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;


@Builder
@Getter
public class Filter {

    private final Boolean valid;
    private final LocalDate from;
    private final LocalDate to;
    private final List<String> tokens;


}

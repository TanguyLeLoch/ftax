package com.natu.ftax.transaction.importer.eth.model;

import java.util.List;

public record LogsResponse(
        String status,
        String message,
        List<EventLog> result
) {
}
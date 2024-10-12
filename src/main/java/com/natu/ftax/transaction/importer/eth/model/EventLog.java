package com.natu.ftax.transaction.importer.eth.model;

import java.util.List;

public record EventLog(
        String address,
        List<String> topics,
        String data,
        String blockNumber,
        String timeStamp,
        String gasPrice,
        String gasUsed,
        String logIndex,
        String transactionHash,
        String transactionIndex
) {
}

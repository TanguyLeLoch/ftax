package com.natu.ftax.transaction.simplified;

import com.natu.ftax.transaction.domain.Transaction;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
public class Pnl {
    String tokenId;
    BigDecimal value;
    TransactionSimplified transaction;
}

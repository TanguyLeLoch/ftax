package com.natu.ftax.transaction.simplified;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
public class Pnl {
    String tokenId;
    BigDecimal value;
    TransactionSimplified transaction;
}

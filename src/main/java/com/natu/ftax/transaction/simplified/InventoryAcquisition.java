package com.natu.ftax.transaction.simplified;

import com.natu.ftax.transaction.domain.Transaction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class InventoryAcquisition {
    private String tokenId;
    List<CostQuantity> costQuantities;

    public InventoryAcquisition (String tokenId){
        this.tokenId = tokenId;
        costQuantities = new ArrayList<>();
    }

    public Pnl fifo(TransactionSimplified tx){
        return null;
    }

    public Pnl average(TransactionSimplified tx){
        return null;
    }


}

package com.natu.ftax.ledger.presentation;

import com.natu.ftax.ledger.domain.Balance;
import com.natu.ftax.ledger.domain.LedgerBook;
import com.natu.ftax.ledger.domain.LedgerEntry;

import java.util.ArrayList;
import java.util.List;

public class TimeLine {
    private LedgerBook ledgerBook;

    public TimeLine(LedgerBook ledgerBook) {
        this.ledgerBook = ledgerBook;
    }

    public List<TimelineItem> getTimeLine(String token) {
        List<TimelineItem> timelineItems = new ArrayList<>();

        String previousBalanceId = null;
        for (LedgerEntry ledgerEntry : ledgerBook.getLedgerEntries()) {
            var instant = ledgerEntry.getInstant();
            Balance balance = ledgerEntry.getBalances().get(token);
            if (balance == null || balance.getId().equals(previousBalanceId)) {
                continue;
            }
            previousBalanceId = balance.getId();

            var amount = balance.getAmount();
            TimelineItem timelineItem = new TimelineItem(instant, amount);
            timelineItems.add(timelineItem);
        }
        return timelineItems;
    }
}

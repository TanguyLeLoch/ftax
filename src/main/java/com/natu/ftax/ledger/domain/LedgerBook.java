package com.natu.ftax.ledger.domain;

import com.natu.ftax.transaction.domain.Token;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

public class LedgerBook {
    @Getter
    @NotNull
    private final String id;
    @NotNull
    private final List<LedgerEntry> ledgerEntries;

    private LedgerBook(String id) {
        this.id = id;
        this.ledgerEntries = new ArrayList<>();
    }

    public LedgerBook(String id, List<LedgerEntry> ledgerEntries) {
        this.id = id;
        this.ledgerEntries = ledgerEntries;
    }

    public static LedgerBook create(String id) {
        return new LedgerBook(id);
    }

    public List<LedgerEntry> getLedgerEntries() {
        return Collections.unmodifiableList(ledgerEntries);
    }

    public void add(LedgerEntry entry) {
        ledgerEntries.add(entry);
    }

    @NotNull
    public Set<Token> getTokens() {
        return ledgerEntries.stream()
                .map(LedgerEntry::getBalances)
                .map(Map::keySet)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }
}

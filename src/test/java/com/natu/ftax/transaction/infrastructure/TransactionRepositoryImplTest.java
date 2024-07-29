package com.natu.ftax.transaction.infrastructure;

import com.natu.ftax.common.exception.NotFoundException;
import com.natu.ftax.transaction.TransactionTestUtils;
import com.natu.ftax.transaction.domain.Transaction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TransactionRepositoryImplTest {

    @Mock
    private TransactionRepositoryJpa transactionRepositoryJpa;

    @InjectMocks
    private TransactionRepositoryImpl transactionRepositoryImpl;

    @Test
    void shouldReturnTransactionById() {
        String transactionId = "1";
        Transaction transaction = TransactionTestUtils.submittedTransaction(transactionId);
        TransactionEntity transactionEntity = TransactionEntity.fromDomain(transaction);

        given(transactionRepositoryJpa.findById(transactionId)).willReturn(Optional.of(transactionEntity));

        Transaction result = transactionRepositoryImpl.getTransactionById(transactionId);

        assertNotNull(result);
        assertEquals(transactionId, result.getId());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenTransactionNotFoundById() {
        String transactionId = "nonexistent";

        given(transactionRepositoryJpa.findById(transactionId)).willReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            transactionRepositoryImpl.getTransactionById(transactionId);
        });

        assertTrue(exception.getMessage().contains("Transaction not found with id: " + transactionId));
    }

    @Test
    void shouldReturnAllTransactions() {
        List<TransactionEntity> transactions = Arrays.asList(
                TransactionEntity.fromDomain(TransactionTestUtils.submittedTransaction("1")),
                TransactionEntity.fromDomain(TransactionTestUtils.submittedTransaction("2"))
        );

        given(transactionRepositoryJpa.findAll()).willReturn(transactions);

        List<Transaction> result = transactionRepositoryImpl.getAllTransactions();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void shouldSaveTransaction() {
        Transaction transaction = TransactionTestUtils.submittedTransaction("1");
        TransactionEntity transactionEntity = TransactionEntity.fromDomain(transaction);

        transactionRepositoryImpl.save(transaction);

        assertNotNull(transactionEntity);
        assertEquals(transaction.getId(), transactionEntity.getId());
    }
}
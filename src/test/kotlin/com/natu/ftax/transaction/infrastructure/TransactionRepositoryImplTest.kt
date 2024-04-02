package com.natu.ftax.transaction.infrastructure

import com.natu.ftax.common.exception.NotFoundException
import com.natu.ftax.transaction.TransactionTestUtils.Companion.submittedTransaction
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import java.util.*

@ExtendWith(MockitoExtension::class)
class TransactionRepositoryImplTest {

    @Mock
    lateinit var transactionRepositoryJpa: TransactionRepositoryJpa

    @InjectMocks
    lateinit var transactionRepositoryImpl: TransactionRepositoryImpl

    @Test
    fun `should return transaction by id`() {
        val transactionId = "1"
        val transaction = submittedTransaction(transactionId)
        val transactionEntity = TransactionEntity.fromDomain(transaction)

        given(transactionRepositoryJpa.findById(transactionId)).willReturn(Optional.of(transactionEntity))

        val result = transactionRepositoryImpl.getTransactionById(transactionId)

        assertNotNull(result)
        assertEquals(transactionId, result.id)
    }

    @Test
    fun `should throw NotFoundException when transaction not found by id`() {
        val transactionId = "nonexistent"

        given(transactionRepositoryJpa.findById(transactionId)).willReturn(Optional.empty())

        val exception = assertThrows(NotFoundException::class.java) {
            transactionRepositoryImpl.getTransactionById(transactionId)
        }

        assertTrue(exception.message!!.contains("Transaction not found with id: $transactionId"))
    }

    @Test
    fun `should return all transactions`() {
        val transactions = listOf(
            TransactionEntity.fromDomain(submittedTransaction("1")),
            TransactionEntity.fromDomain(submittedTransaction("2"))
        )

        given(transactionRepositoryJpa.findAll()).willReturn(transactions)

        val result = transactionRepositoryImpl.getAllTransactions()

        assertNotNull(result)
        assertEquals(2, result.size)
    }

    @Test
    fun `should save a transaction`() {
        val transaction = submittedTransaction("1")
        val transactionEntity = TransactionEntity.fromDomain(transaction)

        transactionRepositoryImpl.save(transaction)

        assertNotNull(transactionEntity)
        assertEquals(transaction.id, transactionEntity.id)
    }
}

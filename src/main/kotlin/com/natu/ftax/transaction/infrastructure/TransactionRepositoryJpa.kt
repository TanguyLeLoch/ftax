package com.natu.ftax.transaction.infrastructure

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TransactionRepositoryJpa : JpaRepository<TransactionEntity, String>
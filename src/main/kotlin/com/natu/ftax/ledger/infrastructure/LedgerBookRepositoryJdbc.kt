package com.natu.ftax.ledger.infrastructure

import LedgerBookRepository
import org.springframework.stereotype.Repository


@Repository
class LedgerBookRepositoryImpl(val ledgerBookRepositoryJpa: LedgerBookRepositoryJpa) : LedgerBookRepository {


}
// transaction.service.ts

import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { Transaction, TransactionControllerService } from '../model';
import { tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class TransactionService {
  private transactionsSubject: BehaviorSubject<Transaction[]> = new BehaviorSubject<Transaction[]>([]);
  public transactions$: Observable<Transaction[]> = this.transactionsSubject.asObservable();

  constructor(private transactionControllerService: TransactionControllerService) {
  }

  /**
   * Fetches all transactions from the backend and updates the state.
   */
  fetchTransactions(): Observable<Transaction[]> {
    return this.transactionControllerService.getAll().pipe(
      tap((transactions) => {
        this.transactionsSubject.next(transactions);
      })
    );
  }

  /**
   * Adds a new transaction by sending it to the backend and updating the state.
   * @param transaction The Transaction object to add.
   */
  addTransaction(transaction: Transaction): Observable<Transaction> {
    return this.transactionControllerService.post(transaction).pipe(
      tap((newTransaction) => {
        const currentTransactions = this.transactionsSubject.value;
        this.transactionsSubject.next([newTransaction, ...currentTransactions]);
      })
    );
  }

  /**
   * Retrieves the current list of transactions synchronously.
   * @returns An array of Transaction objects.
   */
  getTransactions(): Transaction[] {
    return this.transactionsSubject.value;
  }


  /**
   * Updates an existing transaction by sending it to the backend and updating the state.
   * @param updatedTransaction The Transaction object with updated data.
   */
  updateTransaction(updatedTransaction: Transaction): Observable<Transaction> {
    return this.transactionControllerService.post(updatedTransaction).pipe(
      tap((transaction) => {
        const currentTransactions = this.transactionsSubject.value;
        const index = currentTransactions.findIndex((t) => t.id === transaction.id);
        if (index !== -1) {
          currentTransactions[index] = transaction;
          this.transactionsSubject.next([...currentTransactions]);
        }
      })
    );
  }

  /**
   * Refreshes a transaction by fetching it from the backend and updating the state.
   * @param externalId The ID of the transaction to refresh.
   * @returns An Observable of the updated Transaction.
   */
  refreshTransaction(externalId: string): Observable<Transaction[]> {
    return this.transactionControllerService.refresh(externalId).pipe(
      tap((updatedTransactions) => {
        const currentTransactions = this.transactionsSubject.value;
        const updatedTransactionIds = updatedTransactions.map(t => t.externalId);

        // Remove transactions with matching externalIds
        const filteredTransactions = currentTransactions.filter(t => !updatedTransactionIds.includes(t.externalId));

        // Combine filtered transactions with updated ones
        const newTransactions = [...updatedTransactions, ...filteredTransactions];

        // Update the BehaviorSubject with the new transaction list
        this.transactionsSubject.next(newTransactions);
      })
    );
  }

  deleteByExternalId(externalId: string) {
    return this.transactionControllerService.deleteByExternalId(externalId).pipe(
      tap(() => {
        const currentTransactions = this.transactionsSubject.value;
        const updatedTransactions = currentTransactions.filter(t => t.externalId !== externalId);
        this.transactionsSubject.next(updatedTransactions);
      })
    );
  }

  deleteById(id: string) {
    return this.transactionControllerService.deleteById(id).pipe(
      tap(() => {
        const currentTransactions = this.transactionsSubject.value;
        const index = currentTransactions.findIndex((t) => t.id === id);
        if (index !== -1) {
          currentTransactions.splice(index, 1);
        }
        this.transactionsSubject.next([...currentTransactions]);
      })
    );
  }
}



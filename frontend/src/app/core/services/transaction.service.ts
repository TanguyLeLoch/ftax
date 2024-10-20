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
    console.log('updateTransaction');
    console.log(updatedTransaction);
    return this.transactionControllerService.post(updatedTransaction).pipe(
      tap((transaction) => {
        console.log('11111');
        console.log(transaction);

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
   * @param id The ID of the transaction to refresh.
   * @returns An Observable of the updated Transaction.
   */
  refreshTransaction(id: string): Observable<Transaction> {
    return this.transactionControllerService.getById(id).pipe(
      tap((updatedTransaction) => {
        const currentTransactions = this.transactionsSubject.value;
        const index = currentTransactions.findIndex((t) => t.id === id);
        if (index !== -1) {
          currentTransactions[index] = updatedTransaction;
          this.transactionsSubject.next([...currentTransactions]);
        }
      })
    );
  }
}



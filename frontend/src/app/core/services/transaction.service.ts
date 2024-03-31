import {Injectable} from '@angular/core';
import {SubmitTransactionRequest, Transaction, TransactionControllerService} from "../model";
import {Observable, tap} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class TransactionService {

  private transactions: Transaction[] = [];

  constructor(private transactionControllerService: TransactionControllerService) {
  }

  createTransactions() {
    return this.transactionControllerService.createDraftTransaction()
  }

  getTransactions() {
    return this.transactionControllerService.getAllTransactions().pipe(
      tap((transactions: Transaction[]) => {
        this.transactions = transactions;
      })
    );
  }

  submitDraftTransaction(request: SubmitTransactionRequest): Observable<Transaction> {
    return this.transactionControllerService.submitDraftTransaction(request).pipe(
      tap(() => this.getTransactions().subscribe())
    );
  }
}


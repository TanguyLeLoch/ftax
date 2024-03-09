import {Injectable} from '@angular/core';
import {Transaction, TransactionControllerService} from "../model";
import {tap} from "rxjs";

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


  // submitDraftTransaction(draftTransaction: DraftTransaction) {
  //   return this.transactionControllerService.submitDraftTransaction(draftTransaction).pipe(
  //     tap(() => this.getTransactions().subscribe())
  //   );
  // }

  getTransactions() {
    return this.transactionControllerService.getAllTransactions().pipe(
      tap((transactions: Transaction[]) => {
        this.transactions = transactions;
      })
    );
  }
}


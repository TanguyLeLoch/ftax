import {Injectable} from '@angular/core';
import {SubmitTransactionRequest, Transaction, TransactionControllerService} from "../model";
import {BehaviorSubject} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class TransactionService {

  private transactions: Transaction[] = [];

  // Initializing with an empty array
  private transactionsSubject = new BehaviorSubject<Transaction[]>(this.transactions);
  public transactions$ = this.transactionsSubject.asObservable();

  constructor(private transactionControllerService: TransactionControllerService) {
  }

  createTransactions() {
    this.transactionControllerService.createDraftTransaction()
      .subscribe((transaction: Transaction) => {
          this.transactions.push(transaction);
          this.transactionsSubject.next(this.transactions);
        }
      )
  }

  getTransactions() {
    this.transactionControllerService.getAllTransactions()
      .subscribe((transactions: Transaction[]) => {
          this.transactions = transactions;
          this.transactionsSubject.next(this.transactions);
        }
      );
  }

  submitDraftTransaction(request: SubmitTransactionRequest) {
    this.transactionControllerService.submitDraftTransaction(request)
      .subscribe(() => {
          this.getTransactions();
        }
      )
  }
}


import {Injectable} from '@angular/core';
import {EditFieldRequest, SubmitTransactionRequest, Transaction, TransactionControllerService} from "../model";
import {BehaviorSubject, catchError, map, Observable, of, tap} from "rxjs";

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
      .subscribe(() => {
          this.getTransactions();
        }
      )
  }

  getTransactions() {
    this.transactionControllerService.getAllTransactions()
      .subscribe((transactions: Transaction[]) => {
          this.transactions = transactions;
          this.orderTx()
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

  submitDraftTransactionById(id: string) {
    this.transactionControllerService.submitDraftTransactionById(id)
      .subscribe(() => {
          this.getTransactions();
        }
      )
  }

  editTransaction(id: string) {
    this.transactionControllerService.editTransaction(id)
      .subscribe(() => {
          this.getTransactions();
        }
      )
  }

  deleteTransaction(id: string) {
    this.transactionControllerService.deleteTransaction(id)
      .subscribe(() => {
          this.getTransactions();
        }
      )
  }

  orderTransactionsByDraftFirst() {
    this.transactions = this.transactions.sort((a, b) => {
      if (a.state === 'draft') {
        return -1;
      } else if (b.state === 'draft') {
        return 1;
      } else {
        return 0;
      }
    });
  }

  orderTransactionsByDate() {
    this.transactions = this.transactions.sort((a, b) => {
      if (!a.dateTime || !b.dateTime) {
        return 0;
      }
      return new Date(b.dateTime!).getTime() - new Date(a.dateTime!).getTime();
    });
  }

  orderTx() {
    this.orderTransactionsByDate();
    this.orderTransactionsByDraftFirst();
  }


  editField(editFieldRequest: EditFieldRequest): Observable<boolean> {
    return this.transactionControllerService.editField(editFieldRequest)
      .pipe(
        tap(() => {
          this.getTransactions();
        }),
        map(() => true),
        catchError(() => of(false))
      );
  }
}


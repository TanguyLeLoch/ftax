import {Component, OnInit} from '@angular/core';
import {TransactionService} from "../../../core/services/transaction.service";
import {DraftTransaction} from "../../../core/model";
import {tap} from "rxjs";

@Component({
  selector: 'app-transaction-list',
  templateUrl: './transaction-list.component.html',
  styleUrls: ['./transaction-list.component.scss']
})
export class TransactionListComponent implements OnInit {
  transactions: DraftTransaction[] = [];

  constructor(private transactionService: TransactionService) {
  }

  ngOnInit(): void {
  }

  addTransaction() {
    this.transactionService.createTransactions().pipe(
      tap(transaction => this.transactions.push(transaction))
    ).subscribe();
  }
}

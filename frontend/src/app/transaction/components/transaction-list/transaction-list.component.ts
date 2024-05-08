import {Component, OnInit} from '@angular/core';
import {TransactionService} from "../../../core/services/transaction.service";
import {Transaction} from "../../../core/model";
import {Subscription} from "rxjs";

@Component({
  selector: 'app-transaction-list',
  templateUrl: './transaction-list.component.html',
  styleUrls: ['./transaction-list.component.scss']
})
export class TransactionListComponent implements OnInit {
  transactions: Transaction[] = [];
  private transactionsSub!: Subscription;

  constructor(private transactionService: TransactionService) {
  }

  ngOnInit(): void {
    this.transactionService.getTransactions()
    this.transactionsSub = this.transactionService.transactions$.subscribe(
      transactions => {
        this.transactions = transactions;
      }
    );
  }

  ngOnDestroy() {
    this.transactionsSub.unsubscribe();
  }

  addTransaction() {
    this.transactionService.createTransactions();
  }

  importFile() {

  }
}

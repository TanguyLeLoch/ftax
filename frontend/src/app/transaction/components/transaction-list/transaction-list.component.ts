import {Component, OnInit} from '@angular/core';
import {TransactionService} from "../../../core/services/transaction.service";

@Component({
  selector: 'app-transaction-list',
  templateUrl: './transaction-list.component.html',
  styleUrls: ['./transaction-list.component.scss']
})
export class TransactionListComponent implements OnInit {
  transactions: any[] = [];

  constructor(private transactionService: TransactionService) {
  }

  ngOnInit(): void {
  }

  addTransaction() {
    this.transactionService.createTransactions().subscribe((res: any) => {
      this.transactions.push(res);
    });
  }
}

import { Component } from '@angular/core';
import {TransactionService} from "../../../core/services/transaction.service";

@Component({
  selector: 'app-transaction-list-header',
  standalone: true,
  imports: [],
  templateUrl: './transaction-list-header.component.html',
  styleUrl: './transaction-list-header.component.scss'
})
export class TransactionListHeaderComponent {

  constructor(private transactionService: TransactionService) {}

  addTransaction() {
    this.transactionService.createTransactions();
  }
}

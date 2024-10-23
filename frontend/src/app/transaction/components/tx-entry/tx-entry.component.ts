// tx-entry.component.ts

import { Component, Input, OnInit } from '@angular/core';
import { Transaction } from '../../../core/model';
import { faArrowDown, faArrowUp, faWarning } from '@fortawesome/free-solid-svg-icons';
import { TokenService } from '../../../core/services/token.service';
import { TransactionService } from '../../../core/services/transaction.service';

@Component({
  selector: 'app-tx-entry',
  templateUrl: './tx-entry.component.html',
  styleUrls: ['./tx-entry.component.scss'],
})
export class TxEntryComponent implements OnInit {
  @Input() transaction!: Transaction;

  isExpanded: boolean = false;
  date!: Date;
  time!: string;
  isValid!: boolean;
  hasBeenExpanded = false;

  actions = [
    {value: 'BUY', text: 'Buy'},
    {value: 'SELL', text: 'Sell'},
  ];


  protected readonly faArrowUp = faArrowUp;
  protected readonly faArrowDown = faArrowDown;
  protected readonly faWarning = faWarning;

  constructor(
    private tokenService: TokenService,
    private transactionService: TransactionService // Inject the TransactionService
  ) {

  }

  ngOnInit(): void {
    const dateStr = this.transaction.localDateTime.slice(0, 10);
    this.date = new Date(dateStr + 'T00:00:00Z');
    this.time = this.transaction.localDateTime.slice(11, 19);
    this.isValid = this.transaction.valid;
    this.hasBeenExpanded = this.isExpanded;
  }


  onFormSubmit(updatedTransaction: Transaction) {
    // Update the transaction state
    this.transactionService.updateTransaction(updatedTransaction).subscribe(
      (tx: Transaction) => {
        this.transaction = tx;
      },
    )
  }


  onRefreshClick() {
    // Assuming 'refreshTransaction' method fetches updated transaction data
    this.transactionService.refreshTransaction(this.transaction.id).subscribe((updatedTransaction) => {
      this.transaction = updatedTransaction;
      this.isValid = updatedTransaction.valid;
    });
  }
}

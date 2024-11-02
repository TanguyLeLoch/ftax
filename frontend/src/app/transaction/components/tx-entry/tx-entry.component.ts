// tx-entry.component.ts

import { Component, Input, OnInit } from '@angular/core';
import { Transaction, TransactionRequest } from '../../../core/model';
import { faArrowDown, faArrowUp, faWarning } from '@fortawesome/free-solid-svg-icons';
import { TokenService } from '../../../core/services/token.service';
import { TransactionService } from '../../../core/services/transaction.service';
import { ToastService } from "../../../core/services/toast.service";
import { Clipboard } from "@angular/cdk/clipboard";


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
    private clipboard: Clipboard,
    private tokenService: TokenService,
    private transactionService: TransactionService,
    private toast: ToastService,
  ) {

  }

  ngOnInit(): void {
    const dateStr = this.transaction.localDateTime.slice(0, 10);
    this.date = new Date(dateStr + 'T00:00:00Z');
    this.time = this.transaction.localDateTime.slice(11, 19);
    this.isValid = this.transaction.valid;
    this.hasBeenExpanded = this.isExpanded;
  }


  onFormSubmit(updatedTransaction: TransactionRequest) {
    // Update the transaction state
    this.transactionService.updateTransaction(updatedTransaction).subscribe(
      (tx: Transaction) => {
        this.transaction = tx;
      },
    )
  }


  deleteTx() {
    this.transactionService.deleteById(this.transaction.id).subscribe(() => {
      this.toast.showSuccess('Transaction deleted');
    });
  }

  copyTxId() {
    this.clipboard.copy(this.transaction.id);
  }
}

import {Component, Input, OnInit} from '@angular/core';
import {DraftTransaction, EditTransactionRequest} from "../../../core/model";
import {TransactionService} from "../../../core/services/transaction.service";
import {format} from 'date-fns';

@Component({
  selector: 'app-transaction-item',
  templateUrl: './transaction-item.component.html',
  styleUrls: ['./transaction-item.component.scss'],
})
export class TransactionItemComponent implements OnInit {

  @Input() transaction!: DraftTransaction;

  transactionTypes: DraftTransaction.TransactionTypeEnum[] = Object.values(DraftTransaction.TransactionTypeEnum);

  constructor(private transactionService: TransactionService) {
  }

  ngOnInit(): void {
    console.log(this.transaction)
  }

  submitDraftTransaction() {
    const date = Date.parse(this.transaction.date!)
    this.transaction.date = format(date, "yyyy-MM-dd'T'HH:mm:ss.SSS")
    const request: EditTransactionRequest = {
      id: this.transaction.id,
      date: this.transaction.date!,
      transactionType: this.transaction.transactionType,
      amount1: this.transaction.amount1,
      amount2: this.transaction.amount2,
      amountFee: this.transaction.amountFee,
      token1: this.transaction.token1!,
      token2: this.transaction.token2!,
      tokenFee: this.transaction.tokenFee!,
      externalId: this.transaction.externalId
    }
    this.transactionService.submitDraftTransaction(request).subscribe()
  }
}

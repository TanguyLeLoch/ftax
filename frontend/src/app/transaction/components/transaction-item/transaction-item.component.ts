import {Component, Input, OnInit} from '@angular/core';
import {DraftTransaction, EditTransactionRequest} from "../../../core/model";
import {TransactionService} from "../../../core/services/transaction.service";

@Component({
  selector: 'app-transaction-item',
  templateUrl: './transaction-item.component.html',
  styleUrls: ['./transaction-item.component.scss'],
})
export class TransactionItemComponent implements OnInit {

  @Input() transaction!: DraftTransaction;
  editMode!: boolean;

  txDate!: string;
  txTime!: string;

  transactionTypes: DraftTransaction.TransactionTypeEnum[] = Object.values(DraftTransaction.TransactionTypeEnum);

  constructor(private transactionService: TransactionService) {
  }

  ngOnInit(): void {

    if (this.transaction.date) {
      this.txDate = this.transaction.date.slice(0, 10)
      this.txTime = this.transaction.date.slice(11, 23)
    }
  }

  submitDraftTransaction() {

    const date = new Date(this.txDate + 'T' + this.txTime)
    console.log(this.txDate, this.txTime)
    console.log(date)
    this.transaction.date = date.toISOString()
    console.log(this.transaction.date)

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

  onChange(value: any) {
    console.log(value)
  }


}

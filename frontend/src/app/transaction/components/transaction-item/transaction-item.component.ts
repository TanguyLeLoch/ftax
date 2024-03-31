import {Component, Input, OnInit} from '@angular/core';
import {SubmitTransactionRequest, Transaction} from "../../../core/model";
import {TransactionService} from "../../../core/services/transaction.service";
import {faCheck, faEdit, faTrash} from '@fortawesome/free-solid-svg-icons';
import TransactionTypeEnum = SubmitTransactionRequest.TransactionTypeEnum;


@Component({
  selector: 'app-transaction-item',
  templateUrl: './transaction-item.component.html',
  styleUrls: ['./transaction-item.component.scss'],
})
export class TransactionItemComponent implements OnInit {

  @Input() transaction!: Transaction;
  editMode!: boolean;

  txDate!: string;
  txTime!: string;
  faCheck = faCheck;
  faEdit = faEdit;
  faTrash = faTrash;
  submitted = false;

  transactionTypes: Transaction.TransactionTypeEnum[] = Object.values(Transaction.TransactionTypeEnum);
  protected readonly TransactionTypeEnum = TransactionTypeEnum;

  constructor(private transactionService: TransactionService) {
  }

  ngOnInit(): void {

    this.editMode = this.transaction.state === Transaction.StateEnum.Draft;
    if (this.transaction.date) {
      this.txDate = this.transaction.date.slice(0, 10)
      this.txTime = this.transaction.date.slice(11, 23)
    }
  }

  submitTransaction() {
    this.submitted = true;
    if (this.isDateInvalid()
      || this.isTimeInvalid()
      || this.isTransactionTypeInvalid()
      || this.isAmount1Invalid()
      || this.isAmount2Invalid()
      || this.isAmountFeeInvalid()
      || this.isToken1Invalid()
      || this.isToken2Invalid()
      || this.isTokenFeeInvalid()) {
      return;
    }
    if (!this.transaction.token1) {
      this.transaction.token1 = 'DUMMY'
    }
    if (!this.transaction.token2) {
      this.transaction.token2 = 'DUMMY'
    }
    if (!this.transaction.tokenFee) {
      this.transaction.tokenFee = 'DUMMY'
    }
    const date = new Date(this.txDate + 'T' + this.txTime)
    console.log(this.txDate, this.txTime)
    console.log(date)
    this.transaction.date = date.toISOString()
    console.log(this.transaction.date)

    const request: SubmitTransactionRequest = {
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

  editTransaction() {
    console.log('edit')

  }

  delete() {
    console.log('delete')
  }

  // In your component class
  isDateInvalid(): boolean {
    if (!this.txDate) return true;
    // check has pattern yyyy-mm-dd
    if (!/^\d{4}-\d{2}-\d{2}$/.test(this.txDate)) return true;
    return new Date(this.txDate) > new Date();
  }

  isTimeInvalid(): boolean {
    if (!this.txTime) return true;
    // check has pattern hh:mm:ss.SSS
    return !/^\d{2}:\d{2}:\d{2}.\d{3}$/.test(this.txTime);
  }

  isTransactionTypeInvalid(): boolean {
    return this.submitted && this.transaction.transactionType === TransactionTypeEnum.None;
  }

  isAmount1Invalid(): boolean {
    return this.submitted && this.transaction.amount1 < 0;
  }

  isAmount2Invalid(): boolean {
    return this.submitted && this.transaction.amount2 < 0;
  }

  isAmountFeeInvalid(): boolean {
    return this.submitted && this.transaction.amountFee < 0;
  }

  isToken1Invalid(): boolean {
    return this.submitted && (!this.transaction.token1 && this.transaction.amount1 !== 0);
  }

  isToken2Invalid(): boolean {
    return this.submitted && (!this.transaction.token2 && this.transaction.amount2 !== 0);
  }

  isTokenFeeInvalid(): boolean {
    return this.submitted && (!this.transaction.tokenFee && this.transaction.amountFee !== 0);
  }
}

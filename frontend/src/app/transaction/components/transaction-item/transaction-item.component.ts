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
      || this.isAmountInInvalid()
      || this.isAmountOutInvalid()
      || this.isAmountFeeInvalid()
      || this.isTokenInInvalid()
      || this.isTokenOutInvalid()
      || this.isTokenFeeInvalid()) {
      return;
    }
    if (!this.transaction.tokenIn) {
      this.transaction.tokenIn = 'DUMMY'
    }
    if (!this.transaction.tokenOut) {
      this.transaction.tokenOut = 'DUMMY'
    }
    if (!this.transaction.tokenFee) {
      this.transaction.tokenFee = 'DUMMY'
    }
    const date = new Date(this.txDate + 'T' + this.txTime)
    this.transaction.date = date.toISOString()

    const request: SubmitTransactionRequest = {
      id: this.transaction.id,
      date: this.transaction.date!,
      transactionType: this.transaction.transactionType,
      amountIn: this.transaction.amountIn,
      amountOut: this.transaction.amountOut,
      amountFee: this.transaction.amountFee,
      tokenIn: this.transaction.tokenIn!,
      tokenOut: this.transaction.tokenOut!,
      tokenFee: this.transaction.tokenFee!,
      externalId: this.transaction.externalId
    }
    this.transactionService.submitDraftTransaction(request)
  }

  editTransaction() {
    this.transactionService.editTransaction(this.transaction.id)

  }

  delete() {
    this.transactionService.deleteTransaction(this.transaction.id)
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

  isAmountInInvalid(): boolean {
    return this.submitted && this.transaction.amountIn < 0;
  }

  isAmountOutInvalid(): boolean {
    return this.submitted && this.transaction.amountOut < 0;
  }

  isAmountFeeInvalid(): boolean {
    return this.submitted && this.transaction.amountFee < 0;
  }

  isTokenInInvalid(): boolean {
    return this.submitted && (!this.transaction.tokenIn && this.transaction.amountIn !== 0);
  }

  isTokenOutInvalid(): boolean {
    return this.submitted && (!this.transaction.tokenOut && this.transaction.amountOut !== 0);
  }

  isTokenFeeInvalid(): boolean {
    return this.submitted && (!this.transaction.tokenFee && this.transaction.amountFee !== 0);
  }
}

import {Component, Input, OnInit} from '@angular/core';
import {EditFieldRequest, SubmitTransactionRequest, Transaction,} from "../../../core/model";
import {TransactionService} from "../../../core/services/transaction.service";
import {faCheck, faEdit, faTrash} from '@fortawesome/free-solid-svg-icons';
import {
  AmountFeeField,
  AmountInField,
  AmountOutField,
  DateAndTime,
  DateTimeFormField,
  ExternalIdField,
  FormField,
  TokenFeeField,
  TokenInField,
  TokenOutField,
  TransactionTypeFormField,
  Value,
  ValueFeeField,
  ValueInField,
  ValueOutField
} from './FormField';
import TransactionTypeEnum = Transaction.TransactionTypeEnum;


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
  dateValid = true;
  transactionTypeValid = true;
  amountInValid = true;
  amountOutValid = true;
  amountFeeValid = true;
  tokenInValid = true;
  tokenOutValid = true;
  tokenFeeValid = true;
  externalIdValid = true;

  dateTimeField!: DateTimeFormField
  transactionTypeField!: TransactionTypeFormField
  valueInField!: ValueInField
  valueOutField!: ValueOutField
  valueFeeField!: ValueFeeField
  amountInField!: AmountInField
  amountOutField!: AmountOutField
  amountFeeField!: AmountFeeField
  tokenInField!: TokenInField
  tokenOutField!: TokenOutField
  tokenFeeField!: TokenFeeField
  externalIdField!: ExternalIdField


  transactionTypes: Transaction.TransactionTypeEnum[] = Object.values(Transaction.TransactionTypeEnum);

  constructor(private transactionService: TransactionService) {
  }

  ngOnInit(): void {

    this.editMode = this.transaction.state === Transaction.StateEnum.Draft;
    let dateAndTime: DateAndTime | undefined = undefined;
    if (this.transaction.dateTime) {
      this.txDate = this.transaction.dateTime.slice(0, 10)
      this.txTime = this.transaction.dateTime.slice(11, 23)
      dateAndTime = new DateAndTime(this.transaction.dateTime.slice(0, 10), this.transaction.dateTime.slice(11, 23))
    }
    const tx = this.transaction

    this.dateTimeField = new DateTimeFormField(tx, dateAndTime, (value) => !!value && value.isValid())
    this.transactionTypeField = new TransactionTypeFormField(tx, this.transaction.transactionType, this.isTransactionTypeValid)
    this.amountInField = new AmountInField(tx, this.transaction.amountIn, this.isAmountValid)
    this.amountOutField = new AmountOutField(tx, this.transaction.amountOut, this.isAmountValid)
    this.amountFeeField = new AmountFeeField(tx, this.transaction.amountFee, this.isAmountValid)
    this.tokenInField = new TokenInField(tx, this.transaction.tokenIn, (value) => this.isTokenValid(value, this.amountInField.value));
    this.tokenOutField = new TokenOutField(tx, this.transaction.tokenOut, (value) => this.isTokenValid(value, this.amountOutField.value));
    this.tokenFeeField = new TokenFeeField(tx, this.transaction.tokenFee, (value) => this.isTokenValid(value, this.amountFeeField.value));
    this.externalIdField = new ExternalIdField(tx, this.transaction.externalId, () => true);

    let valueIn = new Value(this.transaction.tokenIn, this.transaction.amountIn)
    this.valueInField = new ValueInField(tx, valueIn, (value) => !!value && value.isValid())

    let valueOut = new Value(this.transaction.tokenOut, this.transaction.amountOut)
    this.valueOutField = new ValueOutField(tx, valueOut, (value) => !!value && value.isValid())

    let valueFee = new Value(this.transaction.tokenFee, this.transaction.amountFee)
    this.valueFeeField = new ValueFeeField(tx, valueFee, (value) => !!value && value.isValid())

  }


  isAmountValid(value: number | undefined): boolean {
    return value !== undefined && value >= 0;
  }

  isTransactionTypeValid(value: TransactionTypeEnum | undefined) {
    return value != TransactionTypeEnum.None
  }

  isTokenValid(value: string | undefined, amountValue: number | undefined): boolean {
    return !!value || !amountValue;

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
    this.transaction.dateTime = date.toISOString()

    const request: SubmitTransactionRequest = {
      id: this.transaction.id,
      date: this.transaction.dateTime!,
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
    return this.transaction.transactionType === TransactionTypeEnum.None;
  }

  isAmountInInvalid(): boolean {
    return this.transaction.amountIn !== undefined && this.transaction.amountIn < 0;
  }


  isAmountOutInvalid(): boolean {
    return this.transaction.amountOut !== undefined && this.transaction.amountOut < 0;
  }

  isAmountFeeInvalid(): boolean {
    return this.transaction.amountFee !== undefined && this.transaction.amountFee < 0;
  }

  isTokenInInvalid(): boolean {
    return !this.transaction.tokenIn && this.transaction.amountIn !== 0;
  }

  isTokenOutInvalid(): boolean {
    return !this.transaction.tokenOut && this.transaction.amountOut !== 0;
  }

  isTokenFeeInvalid(): boolean {
    return !this.transaction.tokenFee && this.transaction.amountFee !== 0;
  }

  saveField(isInvalidFunction: () => boolean,
            setValidField: (valid: boolean) => void,
            request: EditFieldRequest) {
    if (!this.editMode) {
      return;
    }
    if (isInvalidFunction()) {
      console.log('invalid field')
      setValidField(false);
      return;
    }
    this.transactionService.editField(request).subscribe((success: boolean) => {
      setValidField(success)
    });
  }

  saveDate() {
    const date = new Date(this.txDate + 'T' + this.txTime)
    try {
      this.transaction.dateTime = date.toISOString()
    } catch (e) {
      this.dateValid = false
      return
    }

    this.saveField(() => this.isDateInvalid(),
      (valid: boolean) => this.dateValid = valid,
      {
        id: this.transaction.id,
        date: this.transaction.dateTime
      });
  }

  saveTransactionType() {
    this.transactionTypeField.dirty()
    if (this.transactionTypeField.isValid()) {
      const request: EditFieldRequest = {
        id: this.transaction.id,
        transactionType: this.transactionTypeField.value
      }
      this.transactionService.editField(request).subscribe((success: boolean) => {
        console.log("savedField")
      });
    }
  }

  save(field: FormField<any>) {
    field.dirty();
    if (field.isValid()) {
      const request = field.createEditRequestBody();
      this.transactionService.editField(request).subscribe((success: boolean) => {
        console.log("savedField")
      });
    }
  }

  saveAmountIn() {
    this.saveField(() => this.isAmountInInvalid(),
      (valid: boolean) => this.amountInValid = valid,
      {
        id: this.transaction.id,
        amountIn: this.transaction.amountIn
      });
  }

  saveTokenIn() {
    this.saveField(() => this.isTokenInInvalid(),
      (valid: boolean) => this.tokenInValid = valid,
      {
        id: this.transaction.id,
        tokenIn: this.transaction.tokenIn
      });
  }

  saveAmountOut() {
    this.saveField(() => this.isAmountOutInvalid(),
      (valid: boolean) => this.amountOutValid = valid,
      {
        id: this.transaction.id,
        amountOut: this.transaction.amountOut
      });
  }

  saveTokenOut() {
    this.saveField(() => this.isTokenOutInvalid(),
      (valid: boolean) => this.tokenOutValid = valid,
      {
        id: this.transaction.id,
        tokenOut: this.transaction.tokenOut
      });
  }

  saveAmountFee() {
    this.saveField(() => this.isAmountFeeInvalid(),
      (valid: boolean) => this.amountFeeValid = valid,
      {
        id: this.transaction.id,
        amountFee: this.transaction.amountFee
      });
  }

  saveTokenFee() {
    this.saveField(() => this.isTokenFeeInvalid(),
      (valid: boolean) => this.tokenFeeValid = valid,
      {
        id: this.transaction.id,
        tokenFee: this.transaction.tokenFee
      });
  }

  saveExternalId() {
    this.saveField(() => false,
      (valid: boolean) => this.externalIdValid = valid,
      {
        id: this.transaction.id,
        externalId: this.transaction.externalId
      });
  }

  isDateValid() {
  }

}



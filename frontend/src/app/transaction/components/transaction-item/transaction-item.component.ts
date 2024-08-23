import {Component, Input, OnInit} from '@angular/core';
import {Transaction,} from "../../../core/model";
import {TransactionService} from "../../../core/services/transaction.service";
import {faCheck, faEdit, faTrash} from '@fortawesome/free-solid-svg-icons';
import {
  DateAndTime,
  DateTimeFormField,
  ExternalIdField,
  FormField,
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

  faCheck = faCheck;
  faEdit = faEdit;
  faTrash = faTrash;

  dateTimeField!: DateTimeFormField
  transactionTypeField!: TransactionTypeFormField
  valueInField!: ValueInField
  valueOutField!: ValueOutField
  valueFeeField!: ValueFeeField
  externalIdField!: ExternalIdField
  fields!: FormField<any>[];


  transactionTypes: Transaction.TransactionTypeEnum[] = Object.values(Transaction.TransactionTypeEnum);

  constructor(private transactionService: TransactionService) {
  }

  ngOnInit(): void {
    const tx = this.transaction

    this.editMode = this.transaction.state === Transaction.StateEnum.Draft;
    const dateAndTime = new DateAndTime(this.transaction.dateTime.slice(0, 10), this.transaction.dateTime.slice(11, 23))

    this.dateTimeField = new DateTimeFormField(tx, dateAndTime, (value) => !!value && value.isValid())
    this.transactionTypeField = new TransactionTypeFormField(tx, this.transaction.transactionType, this.isTransactionTypeValid)

    let valueIn = new Value(this.transaction.tokenIn, this.transaction.amountIn)
    this.valueInField = new ValueInField(tx, valueIn, (value) => !!value && value.isValid())

    let valueOut = new Value(this.transaction.tokenOut, this.transaction.amountOut)
    this.valueOutField = new ValueOutField(tx, valueOut, (value) => !!value && value.isValid())

    let valueFee = new Value(this.transaction.tokenFee, this.transaction.amountFee)
    this.valueFeeField = new ValueFeeField(tx, valueFee, (value) => !!value && value.isValid())

    this.externalIdField = new ExternalIdField(tx, this.transaction.externalId, () => true);

    this.fields = [this.dateTimeField, this.transactionTypeField, this.valueInField, this.valueOutField, this.valueFeeField, this.externalIdField]
  }

  isTransactionTypeValid(value: TransactionTypeEnum | undefined) {
    return value != TransactionTypeEnum.None
  }


  submitTransaction() {
    this.fields.forEach(field => field.dirty());
    const valid = this.fields.every(field => field.isValid());
    if (valid) {
      this.transactionService.submitDraftTransactionById(this.transaction.id)
    }
  }

  editTransaction() {
    this.transactionService.editTransaction(this.transaction.id)

  }

  delete() {
    this.transactionService.deleteTransaction(this.transaction.id)
  }

  save(field: FormField<any>) {
    if (!this.editMode) {
      return;
    }
    field.dirty();
    if (field.isValid()) {
      const request = field.createEditRequestBody();
      this.transactionService.editField(request).subscribe((success: boolean) => {
        field.setBackendInvalid(!success);
      });
    }
  }

}



import {EditFieldRequest, Transaction} from "../../../core/model";
import TransactionTypeEnum = Transaction.TransactionTypeEnum;

export abstract class FormField<T> {
  protected readonly tx: Transaction
  protected isDirty: boolean;
  private readonly validFunction: (value: T | undefined) => boolean;
  value: T | undefined;
  protected backendInvalid: boolean;


  constructor(tx: Transaction, value: T | undefined, isValidFunction: (value: T | undefined) => boolean) {
    this.tx = tx;
    this.value = value;
    this.validFunction = isValidFunction
    this.isDirty = false;
    this.backendInvalid = false;
  };

  isValid() {
    return this.validFunction(this.value);
  }

  dirty() {
    this.isDirty = true;
  }


  isInvalid() {
    return this.isDirty && !this.isValid()
  }

  setBackendInvalid(value: boolean) {
    this.backendInvalid = value;
  }

  abstract createEditRequestBody(): EditFieldRequest;
}

export class TransactionTypeFormField extends FormField<TransactionTypeEnum> {
  createEditRequestBody(): EditFieldRequest {
    return {
      id: this.tx.id,
      transactionType: this.value
    }
  };
}

export class DateTimeFormField extends FormField<DateAndTime> {
  createEditRequestBody(): EditFieldRequest {
    const value: DateAndTime = this.value!
    return {
      id: this.tx.id,
      date: value.getDateAsIso()
    }
  }

  isDateInvalid() {
    return this.backendInvalid || this.isDirty && !this.value!.isDateValid()
  }

  isTimeInvalid() {
    return this.backendInvalid || this.isDirty && !this.value!.isTimeValid()
  }
}

export abstract class ValueField extends FormField<Value> {
  isAmountInvalid() {
    return this.backendInvalid || this.isInvalid() && !this.value!.isAmountValid()
  }

  isTokenInvalid() {
    return this.backendInvalid || this.isInvalid() && !this.value!.isTokenValid()
  }
}

export class ValueInField extends ValueField {
  createEditRequestBody(): EditFieldRequest {
    const value = this.value!
    return {
      id: this.tx.id,
      tokenIn: value.token,
      amountIn: value.amount
    }
  }
}

export class ValueOutField extends ValueField {
  createEditRequestBody(): EditFieldRequest {
    const value = this.value!
    return {
      id: this.tx.id,
      tokenOut: value.token,
      amountOut: value.amount
    }
  }
}

export class ValueFeeField extends ValueField {
  createEditRequestBody(): EditFieldRequest {
    const value = this.value!
    return {
      id: this.tx.id,
      tokenFee: value.token,
      amountFee: value.amount
    }
  }
}

export class ExternalIdField extends FormField<string> {
  createEditRequestBody(): EditFieldRequest {
    return {
      id: this.tx.id,
      externalId: this.value
    }
  }
}

export class Value {
  token: string | undefined
  amount: number | undefined

  constructor(token: string | undefined, amount: number | undefined) {
    this.token = token
    this.amount = amount
  }

  isValid(): boolean {
    return !this.token === !this.amount;
  }

  isTokenValid() {
    return this.isValid() || !!this.token
  }

  isAmountValid() {
    return this.isValid() || !!this.amount
  }
}


export class DateAndTime {
  date: string;
  time: string;

  constructor(date: string, time: string) {
    this.date = date;
    this.time = time;
  }

  isValid() {
    return this.isDateValid() && this.isTimeValid()
  }

  isDateValid(): boolean {
    if (!this.date) return false;
    // check has pattern yyyy-mm-dd
    return /^\d{4}-\d{2}-\d{2}$/.test(this.date);
  }

  isTimeValid(): boolean {
    console.log(this.time)
    console.log(/^\d{2}:\d{2}:\d{2}.\d{3}$/.test(this.time))
    if (!this.time) return false
    // check has pattern hh:mm:ss.SSS
    return /^\d{2}:\d{2}:\d{2}.\d{3}$/.test(this.time);
  }

  getDateAsIso(): string {
    const date = new Date(this.date + 'T' + this.time + 'Z')
    return date.toISOString();
  }
}

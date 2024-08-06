import {EditFieldRequest, Transaction} from "../../../core/model";
import TransactionTypeEnum = Transaction.TransactionTypeEnum;

export abstract class FormField<T> {
  protected readonly tx: Transaction
  private isDirty: boolean;
  private validFunction: (value: T | undefined) => boolean;
  value: T | undefined;


  constructor(tx: Transaction, value: T | undefined, isValidFunction: (value: T | undefined) => boolean) {
    this.tx = tx;
    this.value = value;
    this.validFunction = isValidFunction
    this.isDirty = false;
  }

  setValue(value: T) {
    this.value = value
  }

  getValue() {
    return this.value;
  }

  isValid() {
    return this.validFunction(this.value);
  }

  dirty() {
    this.isDirty = true;
  }


  isInvalid() {
    return this.isDirty && !this.isValid()
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
}

export class TokenInField extends FormField<string> {
  createEditRequestBody(): EditFieldRequest {
    const value = this.value!
    return {
      id: this.tx.id,
      tokenIn: value,
      amountIn: this.tx.amountIn
    }
  }
}

export abstract class ValueField extends FormField<Value> {
  isAmountInvalid() {
    return this.isInvalid() || !this.value!.isAmountValid()
  }

  isTokenInvalid() {
    return this.isInvalid() || !this.value!.isTokenValid()
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

export class TokenOutField extends FormField<string> {
  createEditRequestBody(): EditFieldRequest {
    const value = this.value!
    return {
      id: this.tx.id,
      tokenOut: value,
      amountOut: this.tx.amountOut
    }
  }
}

export class TokenFeeField extends FormField<string> {
  createEditRequestBody(): EditFieldRequest {
    const value = this.value!
    return {
      id: this.tx.id,
      tokenFee: value,
      amountFee: this.tx.amountFee
    }
  }
}


export class AmountInField extends FormField<number> {
  createEditRequestBody(): EditFieldRequest {
    const value = this.value!
    return {
      id: this.tx.id,
      tokenIn: this.tx.tokenIn,
      amountIn: value
    }
  }
}

export class AmountOutField extends FormField<number> {
  createEditRequestBody(): EditFieldRequest {
    const value = this.value!
    return {
      id: this.tx.id,
      tokenOut: this.tx.tokenOut,
      amountOut: value
    }
  }
}

export class AmountFeeField extends FormField<number> {
  createEditRequestBody(): EditFieldRequest {
    const value = this.value!
    return {
      id: this.tx.id,
      tokenFee: this.tx.tokenFee,
      amountFee: value
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
    return !!this.amount
  }
}


export class DateAndTime {
  private date: string;
  private time: string;

  constructor(date: string, time: string) {
    this.date = date;
    this.time = time;
  }

  isValid() {
    return this.isDateValid() && this.isTimeValid()
  }

  private isDateValid(): boolean {
    if (!this.date) return false;
    // check has pattern yyyy-mm-dd
    return /^\d{4}-\d{2}-\d{2}$/.test(this.date);


  }

  private isTimeValid(): boolean {
    if (!this.time) return false
    // check has pattern hh:mm:ss.SSS
    return /^\d{2}:\d{2}:\d{2}.\d{3}$/.test(this.date);
  }

  getDateAsIso(): string {
    const date = new Date(this.date + 'T' + this.time)
    return date.toISOString();
    // try {
    //   this.transaction.dateTime = date.toISOString()
    // } catch (e) {
    //   this.dateValid = false
    //   return
    // }
  }
}

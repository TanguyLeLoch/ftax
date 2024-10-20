// tx-form.component.ts

import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  FormControl,
  FormGroup,
  ValidationErrors,
  ValidatorFn,
  Validators,
} from '@angular/forms';
import { map, Observable, startWith } from 'rxjs';
import { Token, Transaction } from '../../../core/model';
import { TokenService } from '../../../core/services/token.service';
import { ToastService } from '../../../core/services/toast.service';

import { faPlus } from '@fortawesome/free-solid-svg-icons';
import { MatDialog } from '@angular/material/dialog';
import { TokenDialogComponent } from '../token-dialog/token-dialog.component';
import { TransactionService } from "../../../core/services/transaction.service";

@Component({
  selector: 'app-tx-form',
  templateUrl: './tx-form.component.html',
  styleUrls: ['./tx-form.component.scss'],
})
export class TxFormComponent implements OnInit {
  @Input() transaction!: Transaction;
  @Output() formSubmit = new EventEmitter<Transaction>();

  txForm!: FormGroup;
  tokenControl!: FormControl<string | Token | null>;
  filteredOptions!: Observable<Token[]>;
  addTokenOpen = false;
  isExpanded: boolean = false;
  isValid!: boolean;
  date!: Date;
  time!: string;
  actions = [
    { value: 'BUY', text: 'Buy' },
    { value: 'SELL', text: 'Sell' },
  ];
  formatter = new Intl.NumberFormat('en-US', {
    notation: 'compact',
    compactDisplay: 'short',
  });

  protected readonly faPlus = faPlus;

  constructor(
    private fb: FormBuilder,
    private tokenService: TokenService,
    private toast: ToastService,
    private dialog: MatDialog,
    private transactionService: TransactionService // Inject the TransactionStateService
  ) {}

  ngOnInit(): void {
    this.setupTokenAutocomplete();
    this.initializeForms();
    this.isValid = this.transaction.valid;
    this.isExpanded = !this.transaction.valid;
  }

  initializeForms() {
    const dateStr = this.transaction.localDateTime.slice(0, 10);
    this.date = new Date(dateStr + 'T00:00:00Z');
    this.time = this.transaction.localDateTime.slice(11, 19);

    this.txForm = this.fb.group({
      date: [this.date, Validators.required],
      time: [this.time, Validators.required],
      type: [this.transaction.type, Validators.required],
      amount: [this.transaction.amount, [Validators.required, notNegativeValidator()]],
      token: this.tokenControl,
      price: [this.transaction.price, [Validators.required, notNegativeValidator()]],
    });
  }

  setupTokenAutocomplete() {
    this.tokenControl = new FormControl<string | Token | null>(
      this.transaction.token ? this.transaction.token : '',
      [Validators.required, noStringValidator()]
    );

    this.tokenService.tokens$.subscribe((tokens) => {
      this.replaceStringByToken(tokens);
      this.filteredOptions = this.tokenControl.valueChanges.pipe(
        startWith(this.tokenControl.value),
        map((value) => {
          const name = typeof value === 'string' ? value : value?.name;
          return name ? this._filter(name as string) : tokens.slice();
        })
      );
    });
  }

  private replaceStringByToken(tokens: Token[]) {
    if (tokens.length === 0) {
      return;
    }
    const originalToken = this.tokenControl?.value;
    if (typeof originalToken !== 'string') return;

    const found = tokens.find((t) => t.id === originalToken);
    if (found) {
      this.tokenControl.setValue(found);
    }
  }

  displayFn(token: Token): string {
    return token && token.name ? token.name : '';
  }

  getToken(): Token[] {
    return this.tokenService.getTokens();
  }

  private _filter(name: string): Token[] {
    const filterValue = name.toLowerCase();
    return this.getToken().filter((option) => option.name.toLowerCase().includes(filterValue));
  }

  onSubmit(): void {
    // Format date and time
    console.log('onSubmit');
    const date: Date = this.txForm.get('date')!.value;
    const dateStr = date.toISOString().slice(0, 10);
    this.transaction.localDateTime = dateStr + ' ' + this.txForm.get('time')!.value;
    this.transaction.type = this.txForm.get('type')!.value;
    this.transaction.amount = this.txForm.get('amount')!.value;
    this.transaction.token = this.txForm.get('token')!.value.id;
    this.transaction.price = this.txForm.get('price')!.value;

    // Update the transaction state using the service
    console.log('addTransaction');
    this.transactionService.updateTransaction(this.transaction);

    // Emit the transaction
    this.formSubmit.emit(this.transaction);

    // Optionally, handle validation or display messages
    this.isValid = this.transaction.valid;
    if (!this.transaction.valid) {
      this.toast.showToast('error', this.transaction.error!);
    }
    this.isExpanded = false;
  }

  addToken() {
    const tokenInputValue = this.txForm.get('token')!.value;
    const threeFirstChars = tokenInputValue.substring(0, 3).toUpperCase();
    const dialogRef = this.dialog.open(TokenDialogComponent, {
      width: '600px',
      data: {
        name: tokenInputValue,
        ticker: threeFirstChars,
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        // Handle the result from the dialog (the new token)
        this.handleNewToken(result);
      }
    });
  }

  handleNewToken(token: Token) {
    this.tokenService.createToken(token).subscribe((newToken) => {
      this.addTokenOpen = false;
      this.txForm.get('token')!.setValue(newToken);
    });
  }
}

// Validators
function noStringValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    if (typeof control.value === 'string' && control.value.length > 0) {
      return {noString: true};
    }
    return null;
  };
}

export function notNegativeValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const value = control.value;

    // Check if the value is not negative (>= 0)
    if (value >= 0) {
      return null; // Valid, no error
    } else {
      return {notNegative: true}; // Invalid, return error
    }
  };
}

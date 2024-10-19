import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  FormControl,
  FormGroup,
  ValidationErrors,
  ValidatorFn,
  Validators
} from '@angular/forms';
import { map, Observable, startWith } from 'rxjs';
import { Token, Transaction, TransactionControllerService } from "../../../core/model";
import { TokenService } from "../../../core/services/token.service";
import { ToastService } from "../../../core/services/toast.service";

import { faAdd, faCheck, faEdit, faPlus, faTrash, faWarning } from "@fortawesome/free-solid-svg-icons";


@Component({
  selector: 'app-tx-form',
  templateUrl: './tx-form.component.html',
  styleUrls: ['./tx-form.component.scss'],
})
export class TxFormComponent implements OnInit {
  @Input() transaction!: Transaction;
  @Output() formSubmit = new EventEmitter<Transaction>();

  txForm!: FormGroup;
  tokenForm!: FormGroup;
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



  constructor(
    private service: TransactionControllerService,
    private fb: FormBuilder,
    private tokenService: TokenService,
    private toast: ToastService
  ) {}

  ngOnInit(): void {
    this.initializeForms();
    this.setupTokenAutocomplete();
    this.isValid = this.transaction.valid;
    this.isExpanded = !this.transaction.valid;
  }

  initializeForms() {
    this.txForm = this.fb.group({
      date: [this.date, Validators.required,],
      time: [this.time, Validators.required,],
      type: [this.transaction.type, Validators.required,],
      amount: [this.transaction.amount, [Validators.required, notNegativeValidator()]],
      token: this.tokenControl,
      price: [this.transaction.price, [Validators.required, notNegativeValidator()]],
    });
    this.tokenForm = this.fb.group({
      name: ['', Validators.required],
      ticker: ['', Validators.required],
    });
  }

  setupTokenAutocomplete() {
    this.tokenControl = new FormControl<string | Token | null>(
      this.transaction.token ? this.transaction.token : '',
      [Validators.required, noStringValidator()]
    );

    this.tokenService.tokens$.subscribe(
      tokens => {
        this.replaceStringByToken(tokens);
        this.filteredOptions = this.tokenControl.valueChanges.pipe(
          startWith(this.tokenControl.value),
          map(value => {
            const name = typeof value === 'string' ? value : value?.name;
            return name ? this._filter(name as string) : tokens.slice();
          }),
        );
      }
    );

  }
  private replaceStringByToken(tokens: Token[]) {
    if (tokens.length === 0) {
      return;
    }
    const originalToken = this.tokenControl?.value;
    if (typeof originalToken !== 'string') return;

    const found = tokens.find(t => t.id === originalToken);
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
    return this.getToken().filter(option => option.name.toLowerCase().includes(filterValue));
  }

  onSubmit(): void {
      // yyyy-MM-dd HH:mm:ss.SSS without timezone
      const date: Date = this.txForm.get('date')!.value;
    const dateStr = date.toISOString().slice(0, 10)
    this.transaction.localDateTime = dateStr + ' ' + this.time;
    this.transaction.type = this.txForm.get('type')!.value;
    this.transaction.amount = this.txForm.get('amount')!.value;
    this.transaction.token = this.txForm.get('token')!.value.id


    this.transaction.price = this.txForm.get('price')!.value;
    this.service.post(this.transaction).subscribe(tx => {
      this.transaction = tx
      this.isValid = this.transaction.valid;
      if (!this.transaction.valid) {
        this.toast.showToast('error', tx.error!);
      }
      console.log('is valid ? ', this.transaction.valid)
      this.isExpanded = false;
    });
    this.formSubmit.emit(this.transaction);
  }

  addToken() {
    this.addTokenOpen = true;
    const threeFirstChars = this.txForm.get('token')!.value.substring(0, 3).toUpperCase();

    this.tokenForm.setValue({
      name: this.txForm.get('token')!.value,
      ticker: threeFirstChars
    });
  }

  onSubmitToken() {
    const token: Token = {
      name: this.tokenForm.get('name')!.value,
      ticker: this.tokenForm.get('ticker')!.value,
    };
    this.tokenService.createToken(token).subscribe(token => {
        this.addTokenOpen = false;
        this.txForm.get('token')!.setValue(token);
      }
    )
  }

  protected readonly faTrash = faTrash;
  protected readonly faEdit = faEdit;
  protected readonly faCheck = faCheck;
  protected readonly faWarning = faWarning;
  protected readonly faAdd = faAdd;
  protected readonly faPlus = faPlus;
}


function noStringValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    if (typeof control.value === 'string' && control.value.length > 0) {
      return {'noString': true};
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

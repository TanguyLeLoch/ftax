import {Component, Input, OnInit} from '@angular/core';
import {Token, TransactionSimplified, TransactionSimplifiedControllerService} from "../../../../core/model";
import {
  AbstractControl,
  FormBuilder,
  FormControl,
  FormGroup,
  ValidationErrors,
  ValidatorFn,
  Validators
} from "@angular/forms";
import {faAdd, faCheck, faEdit, faPlus, faTrash, faWarning} from "@fortawesome/free-solid-svg-icons";
import {map, Observable, startWith} from "rxjs";
import {TokenService} from "../../../../core/services/token.service";

@Component({
  selector: 'app-tx-simplified',
  templateUrl: './tx-simplified.component.html',
  styleUrl: './tx-simplified.component.scss',
})
export class TxSimplifiedComponent implements OnInit {
  @Input() transaction!: TransactionSimplified;

  txForm!: FormGroup;
  date!: Date;
  time!: string;

  isValid!: boolean;
  isCollapsed!: boolean;

  tokenControl!: FormControl<string | Token | null>;
  filteredOptions!: Observable<Token[]>;
  addTokenOpen = false;

  tokenForm!: FormGroup;
  actions = [
    {value: 'BUY', text: 'bought'},
    {value: 'SELL', text: 'sold'}
  ]


  open(): void {
    this.isCollapsed = false
  }


  constructor(private service: TransactionSimplifiedControllerService, private fb: FormBuilder,
              private tokenService: TokenService) {
  }

  ngOnInit(): void {
    this.tokenService.fetchAllTokens();

    this.tokenControl = new FormControl<string | Token | null>(
      this.transaction.token ? this.transaction.token : '',
      [Validators.required,noStringValidator()]
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

    const dateStr = this.transaction.localDateTime.slice(0, 10);
    this.date = new Date(dateStr + 'T00:00:00Z')
    this.time = this.transaction.localDateTime.slice(11, 19);
    this.isValid = this.transaction.valid;
    this.isCollapsed = this.transaction.valid

    this.txForm = this.fb.group({
      date: [this.date, Validators.required,],
      time: [this.time, Validators.required,],
      type: [this.transaction.type, Validators.required,],
      amount: [this.transaction.amount, [Validators.required, notNegativeValidator()]],
      token: this.tokenControl,
      dollarValue: [this.transaction.dollarValue, [Validators.required, notNegativeValidator()]],
    });
    this.tokenForm = this.fb.group({
      name: ['', Validators.required],
      ticker: ['', Validators.required],
    });
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

  getToken(): Token[] {
    return this.tokenService.getTokens();
  }

  onSubmit(): void {
    // yyyy-MM-dd HH:mm:ss.SSS without timezone
    const date: Date = this.txForm.get('date')!.value;
    const dateStr = date.toISOString().slice(0, 10)
    this.transaction.localDateTime = dateStr + ' ' + this.time;
    this.transaction.type = this.txForm.get('type')!.value;
    this.transaction.amount = this.txForm.get('amount')!.value;
    this.transaction.token = this.txForm.get('token')!.value.id


    this.transaction.dollarValue = this.txForm.get('dollarValue')!.value;
    this.service.post(this.transaction).subscribe(tx => {
      this.transaction = tx
      this.isValid = this.transaction.valid;
      this.isCollapsed = this.transaction.valid;
    });


  }


  displayFn(token: Token): string {
    return token && token.name ? token.name : '';
  }

  private _filter(name: string): Token[] {
    const filterValue = name.toLowerCase();
    return this.getToken().filter(option => option.name.toLowerCase().includes(filterValue));
  }

  protected readonly faTrash = faTrash;
  protected readonly faEdit = faEdit;
  protected readonly faCheck = faCheck;
  protected readonly faWarning = faWarning;
  protected readonly faAdd = faAdd;
  protected readonly faPlus = faPlus;

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
}

function noStringValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    if (typeof control.value === 'string' && control.value.length > 0) {
      return {'invalidString': true};
    }
    return null;
  };
}

export function notNegativeValidator(): ValidatorFn {
  console.log('not negativ validator')
  return (control: AbstractControl): ValidationErrors | null => {
    const value = control.value;

    // Check if the value is not negative (>= 0)
    console.log('is it called ? ')
    if (value >= 0) {
      return null; // Valid, no error
    } else {
      return { notNegative: true }; // Invalid, return error
    }
  };
}

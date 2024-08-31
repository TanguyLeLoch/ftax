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
import {map, Observable, startWith, Subscription} from "rxjs";
import {TokenService} from "../../../../core/services/token.service";

@Component({
  selector: 'app-tx-simplified',
  templateUrl: './tx-simplified.component.html',
  styleUrl: './tx-simplified.component.scss',
})
export class TxSimplifiedComponent implements OnInit {
  @Input() transaction!: TransactionSimplified;

  txForm!: FormGroup;
  date!: string;
  time!: string;

  isValid!: boolean;
  isCollapsed!: boolean;

  tokenControl!: FormControl<string | Token | null>;
  options!: Token[]
  filteredOptions!: Observable<Token[]>;
  tokens!: Token[];
  private tokensSub!: Subscription;

  open(): void {
    this.isCollapsed = false
  }


  constructor(private service: TransactionSimplifiedControllerService, private fb: FormBuilder,
              private tokenService: TokenService) {
  }

  ngOnInit(): void {
    let tokens = this.tokenService.getTokens();
    this.tokenService.fetchAllTokens();

    this.tokenControl = new FormControl<string | Token | null>(
      this.transaction.token ? this.transaction.token : '',
      [noStringValidator()]
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
    this.options = tokens


    this.date = this.transaction.localDateTime.slice(0, 10)
    this.time = this.transaction.localDateTime.slice(11, 23);
    this.isValid = this.transaction.valid;
    this.isCollapsed = this.transaction.valid

    this.txForm = this.fb.group({
      date: [this.date, Validators.required,],
      time: [this.time, Validators.required,],
      type: [this.transaction.type, Validators.required,],
      amount: [this.transaction.amount, Validators.required,],
      token: this.tokenControl,
      dollarValue: [this.transaction.dollarValue, Validators.required,],
    });
    // token: [this.transaction.token, Validators.required],
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
    this.transaction.localDateTime = this.date + ' ' + this.time;
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

  invalidAndTouched(field: string): boolean {
    return this.txForm.get(field)!.invalid && this.txForm.get(field)!.touched;
  }

  invalidAndNotTouched(field: string): boolean {
    return this.txForm.get(field)!.invalid && !this.txForm.get(field)!.touched;
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
    console.log('add token')
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

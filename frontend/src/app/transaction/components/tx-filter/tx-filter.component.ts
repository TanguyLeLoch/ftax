import { ChangeDetectionStrategy, Component, OnInit, ViewEncapsulation } from '@angular/core';
import { FilterService } from "../../../core/services/filter.service";
import { AbstractControl, FormBuilder, FormControl, FormGroup, ValidatorFn } from "@angular/forms";
import { Token } from "../../../core/model";
import { TokenService } from "../../../core/services/token.service";
import { startWith, tap } from "rxjs";
import { MatDialogRef } from "@angular/material/dialog";

@Component({
  selector: 'app-tx-filter',
  templateUrl: './tx-filter.component.html',
  styleUrl: './tx-filter.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
  encapsulation: ViewEncapsulation.None
})
export class TxFilterComponent implements OnInit {

  filterForm!: FormGroup;
  tokens: Token[] = [];
  availableTokens!: Token[];
  tokenControl!: FormControl<Token | string | null>;

  constructor(
    public dialogRef: MatDialogRef<TxFilterComponent>,
    private filterService: FilterService,
    private tokenService: TokenService,
    private fb: FormBuilder) {
  }

  ngOnInit(): void {
    this.tokens = this.filterService.tokenFilter;
    this.availableTokens = this.tokenService.getTokens();
    this.tokenControl = new FormControl<Token | string | null>('');
    this.tokenControl.valueChanges.pipe(
      startWith(this.tokenControl.value),
      tap((value) => {
        const name = typeof value === 'string' ? value : value?.name;
        this.availableTokens = name ? this._filter(name as string) : this.filterEmpty();
      })
    ).subscribe()

    this.filterForm = this.fb.group({
        start: new FormControl<Date | null>(this.filterService.startDateFilter),
        end: new FormControl<Date | null>(this.filterService.endDateFilter),
        validity: new FormControl<string[]>(this.filterService.validityFilter),
        token: this.tokenControl,
      },
      {validators: this.dateRangeValidator});
  }

  private filterEmpty() {
    return this.getToken().filter(t => this.tokens.find(token => token.id === t.id) === undefined);
  }

  onSubmit() {
    this.filterService.updateFilter(
      this.filterForm.get('validity')?.value,
      this.filterForm.get('start')?.value,
      this.filterForm.get('end')?.value,
      this.tokens
    );

    this.closeDialog();
  }

  closeDialog() {
    this.dialogRef.close();
  }

  clear(formAttribute: string) {
    this.filterForm.get(formAttribute)?.setValue(null);
  }

  displayFn(token: Token): string {
    return token && token.name ? token.name : '';
  }

  private getToken(): Token[] {
    return this.tokenService.getTokens();
  }

  private _filter(name: string): Token[] {
    const filterValue = name.toLowerCase();
    return this.getToken()
      .filter((option) => option.name.toLowerCase().includes(filterValue))
      .filter(option => this.tokens.find(t => t.id === option.id) === undefined);
  }

  addToFilter(option: Token) {
    this.tokens.push(option);
    this.filterForm.get('token')!.setValue(null);
  }

  removeToken(token: Token) {
    this.tokens = this.tokens.filter(t => t.id !== token.id);
    this.filterForm.get('token')!.updateValueAndValidity();
  }

  dateRangeValidator: ValidatorFn = (control: AbstractControl): { [key: string]: any } | null => {
    const start = control.get('start');
    const end = control.get('end');

    if (start?.value && end?.value && new Date(start.value) > new Date(end.value)) {
      return {'dateRange': true};
    }
    return null;
  };
}

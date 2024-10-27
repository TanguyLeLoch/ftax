import { Injectable } from '@angular/core';
import { BehaviorSubject } from "rxjs";
import { Token } from "../model";

@Injectable({
  providedIn: 'root'
})
export class FilterService {

  validityFilter: string[] = []
  startDateFilter: Date | null = null
  endDateFilter: Date | null = null
  tokenFilter: Token[] = []

  filterUpdated = new BehaviorSubject<Filter[]>([]);
  filterUpdated$ = this.filterUpdated.asObservable();

  constructor() { }


  updateFilter(validity: string[], startDate: Date | null, endDate: Date | null, token: Token[]) {
    this.validityFilter = validity;
    this.startDateFilter = startDate;
    this.endDateFilter = endDate;
    this.tokenFilter = token;

    this.filterUpdated.next(this.getFilter());
  }

  getFilter(): Filter[] {
    const filters: Filter[] = [];

    // Add validity filters
    this.validityFilter.forEach(validity => {
      filters.push(new Filter(
        validity,
        validity === 'valid' ? 'Valid transactions' : 'Invalid transactions',
        () => {
          this.validityFilter = this.validityFilter.filter(v => v !== validity);
          this.filterUpdated.next(this.getFilter());
        }
      ));
    });

    // Add date filter if either date is set
    if (this.startDateFilter) {
      filters.push(new Filter(
        this.startDateFilter,
        `From : ${this.startDateFilter.toLocaleDateString('en-US')}`,
        () => {
          this.startDateFilter = null;
          this.filterUpdated.next(this.getFilter());
        }
      ));
    }
    if (this.endDateFilter) {
      filters.push(new Filter(
        this.endDateFilter,
        `To : ${this.endDateFilter.toLocaleDateString('en-US')}`,
        () => {
          this.endDateFilter = null;
          this.filterUpdated.next(this.getFilter());
        }
      ));
    }

    // Add token filters
    this.tokenFilter.forEach(token => {
      filters.push(new Filter(
        token,
        `${token.name}(${token.ticker})`,  // Assuming Token has name or id property
        () => {
          this.tokenFilter = this.tokenFilter.filter(t => t !== token);
          this.filterUpdated.next(this.getFilter());
        }
      ));
    });

    return filters;
  }

  // clearAllFilters() {
  //   this.validityFilter = [];
  //   this.startDateFilter = null;
  //   this.endDateFilter = null;
  //   this.tokenFilter = [];
  //   this.filterUpdated.next([]);
  // }

}

export class Filter {
  readonly _value: any;
  private readonly _displayValue: string;
  removeFunction: () => void;

  constructor(value: any, displayValue: string, removeFunction: () => void) {
    this._value = value;
    this._displayValue = displayValue;
    this.removeFunction = removeFunction;
  }

  get displayValue() {
    return this._displayValue;
  }
}

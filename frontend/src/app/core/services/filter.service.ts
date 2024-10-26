import { Injectable } from '@angular/core';
import { BehaviorSubject } from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class FilterService {

  // private dateFromFilter = new BehaviorSubject<Date>(null);
  private selectedFilters = new BehaviorSubject<string[]>([]);
  selectedFilters$ = this.selectedFilters.asObservable();

  updateFilters(filters: string[]) {
    this.selectedFilters.next(filters);
  }
  constructor() { }
}

import { Component, OnInit } from '@angular/core';
import { FilterService } from "../../../core/services/filter.service";
import { FormBuilder, FormGroup } from "@angular/forms";

@Component({
  selector: 'app-tx-filter',
  templateUrl: './tx-filter.component.html',
  styleUrl: './tx-filter.component.scss'
})
export class TxFilterComponent implements OnInit {

  filterForm!: FormGroup;

  constructor(private filterService: FilterService,
              private fb: FormBuilder) {

  }

  ngOnInit(): void {
    this.filterForm = this.fb.group({
      dateFrom: [''],
      dateTo: ['']
    });
  }


  onSubmit() {

  }
}

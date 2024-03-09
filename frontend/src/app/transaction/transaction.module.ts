import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {TransactionListComponent} from './components/transaction-list/transaction-list.component';
import {CoreModule} from "../core/core.module";
import {TransactionItemComponent} from './components/transaction-item/transaction-item.component';
import {MatSelect} from "@angular/material/select";
import {MatDatepickerInput} from "@angular/material/datepicker";


@NgModule({
  declarations: [
    TransactionListComponent,
    TransactionItemComponent
  ],
  exports: [
    TransactionListComponent
  ],
  imports: [
    CommonModule,
    CoreModule,
    MatSelect,
    MatDatepickerInput,
  ]
})
export class TransactionModule {
}

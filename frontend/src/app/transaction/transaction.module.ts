import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {TransactionListComponent} from './components/transaction-list/transaction-list.component';
import {CoreModule} from "../core/core.module";
import {TransactionItemComponent} from './components/transaction-item/transaction-item.component';
import {MatSelect} from "@angular/material/select";
import {MatDatepickerInput} from "@angular/material/datepicker";
import {FormsModule} from "@angular/forms";


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
    FormsModule,
  ]
})
export class TransactionModule {
}

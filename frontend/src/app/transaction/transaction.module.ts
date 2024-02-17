import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {TransactionListComponent} from './components/transaction-list/transaction-list.component';


@NgModule({
  declarations: [
    TransactionListComponent
  ],
  exports: [
    TransactionListComponent
  ],
  imports: [
    CommonModule
  ]
})
export class TransactionModule {
}

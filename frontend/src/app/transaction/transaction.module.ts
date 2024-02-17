import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {TransactionListComponent} from './components/transaction-list/transaction-list.component';
import {CoreModule} from "../core/core.module";
import { TransactionItemComponent } from './components/transaction-item/transaction-item.component';


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
    CoreModule
  ]
})
export class TransactionModule {
}

import { NgModule } from '@angular/core';
import { AsyncPipe, CommonModule } from '@angular/common';
import { TransactionListComponent } from './components/transaction-list/transaction-list.component';
import { CoreModule } from "../core/core.module";
import { TransactionItemComponent } from './components/transaction-item/transaction-item.component';
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { FaIconComponent, FontAwesomeModule } from "@fortawesome/angular-fontawesome";
import { TransactionListHeaderComponent } from "./components/transaction-list-header/transaction-list-header.component";
import { TxSimplifiedComponent } from "./components/simplified/tx-simplified/tx-simplified.component";
import { TxListComponent } from "./components/simplified/tx-list/tx-list.component";
import { MaterialModule } from "../material/material.module";


@NgModule({
  declarations: [
    TransactionListComponent,
    TransactionItemComponent,
    TxSimplifiedComponent,
    TxListComponent
  ],
  exports: [
    TransactionListComponent,
    TxListComponent
  ],
  imports: [
    CommonModule,
    CoreModule,
    FormsModule,
    FaIconComponent,
    FontAwesomeModule,
    TransactionListHeaderComponent,
    ReactiveFormsModule,
    AsyncPipe,
    MaterialModule
  ]
})
export class TransactionModule {
}

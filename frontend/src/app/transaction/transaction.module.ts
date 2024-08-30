import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {TransactionListComponent} from './components/transaction-list/transaction-list.component';
import {CoreModule} from "../core/core.module";
import {TransactionItemComponent} from './components/transaction-item/transaction-item.component';
import {MatSelect} from "@angular/material/select";
import {MatDatepickerInput} from "@angular/material/datepicker";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {FaIconComponent, FontAwesomeModule} from "@fortawesome/angular-fontawesome";
import {TransactionListHeaderComponent} from "./components/transaction-list-header/transaction-list-header.component";
import {TxSimplifiedComponent} from "./components/simplified/tx-simplified/tx-simplified.component";
import {TxListComponent} from "./components/simplified/tx-list/tx-list.component";
import {MatIcon} from "@angular/material/icon";
import {MatIconButton} from "@angular/material/button";


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
    MatSelect,
    MatDatepickerInput,
    FormsModule,
    FaIconComponent,
    FontAwesomeModule,
    TransactionListHeaderComponent,
    ReactiveFormsModule,
    MatIcon,
    MatIconButton
  ]
})
export class TransactionModule {
}

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
import {StandardButtonComponent} from "../core/components/standard-button/standard-button.component";
import {ModalComponent} from "../core/components/modal/modal.component";


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
        FaIconComponent,
        FontAwesomeModule,
        TransactionListHeaderComponent,
        ReactiveFormsModule,
        StandardButtonComponent,
        ModalComponent
    ]
})
export class TransactionModule {
}

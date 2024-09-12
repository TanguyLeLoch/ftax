import { NgModule } from '@angular/core';
import { AsyncPipe, CommonModule } from '@angular/common';
import { CoreModule } from "../core/core.module";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { FaIconComponent, FontAwesomeModule } from "@fortawesome/angular-fontawesome";
import { TxEntryComponent } from "./components/simplified/tx-entry/tx-entry.component";
import { TxListComponent } from "./components/simplified/tx-list/tx-list.component";
import { MaterialModule } from "../material/material.module";


@NgModule({
  declarations: [
    TxEntryComponent,
    TxListComponent
  ],
  exports: [
    TxListComponent
  ],
  imports: [
    CommonModule,
    CoreModule,
    FormsModule,
    FaIconComponent,
    FontAwesomeModule,
    ReactiveFormsModule,
    AsyncPipe,
    MaterialModule
  ]
})
export class TransactionModule {
}

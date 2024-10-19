import { NgModule } from '@angular/core';
import { AsyncPipe, CommonModule } from '@angular/common';
import { CoreModule } from "../core/core.module";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { FaIconComponent, FontAwesomeModule } from "@fortawesome/angular-fontawesome";
import { TxEntryComponent } from "./components/tx-entry/tx-entry.component";
import { TxListComponent } from "./components/tx-list/tx-list.component";
import { MaterialModule } from "../material/material.module";
import { TxImportComponent } from "./components/tx-import/tx-import.component";
import { TxFormComponent } from "./components/tx-form/tx-form.component";
import { TokenDialogComponent } from "./components/token-dialog/token-dialog.component";


@NgModule({
  declarations: [
    TxEntryComponent,
    TxListComponent,
    TxImportComponent,
    TxFormComponent,
    TokenDialogComponent
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

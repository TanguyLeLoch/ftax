import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppComponent} from './app.component';
import {NavbarComponent} from './components/navbar/navbar.component';
import {TransactionModule} from "./transaction/transaction.module";
import {provideAnimationsAsync} from '@angular/platform-browser/animations/async';
import {FontAwesomeModule} from '@fortawesome/angular-fontawesome';
import {RouterModule, Routes} from "@angular/router";
import {TransactionListComponent} from "./transaction/components/transaction-list/transaction-list.component";
import {LedgerBookComponent} from "./ledger/components/ledger-book/ledger-book.component";
import {LedgerModule} from "./ledger/ledger.module";
import {CoreModule} from "./core/core.module";
import {provideCharts, withDefaultRegisterables} from 'ng2-charts';
import {TxListComponent} from "./transaction/components/simplified/tx-list/tx-list.component";
import {MatNativeDateModule} from "@angular/material/core";
import {MatDatepickerModule} from "@angular/material/datepicker";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatButtonModule} from "@angular/material/button";
import {MatSelectModule} from "@angular/material/select";
import {MatInputModule} from "@angular/material/input";
import {MatIconModule} from "@angular/material/icon";
import {basePathProvider} from "../config/basePathProvider";


const routes: Routes = [
  {path: 'simplified', component: TxListComponent},
  { path: 'transactions', component: TransactionListComponent },
  { path: 'ledger', component: LedgerBookComponent },
  {path: '', redirectTo: '/simplified', pathMatch: 'full'} // default route
];

@NgModule({
  declarations: [
    AppComponent,
    NavbarComponent
  ],
  imports: [
    BrowserModule,
    TransactionModule,
    LedgerModule,
    FontAwesomeModule,
    RouterModule.forRoot(routes),
    CoreModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatFormFieldModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatIconModule,

  ],
  providers: [
    provideAnimationsAsync(),
    provideCharts(withDefaultRegisterables()),
    basePathProvider
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}

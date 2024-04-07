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


const routes: Routes = [
  { path: 'transactions', component: TransactionListComponent },
  { path: 'ledger', component: LedgerBookComponent },
  { path: '', redirectTo: '/transactions', pathMatch: 'full' } // default route
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
    RouterModule.forRoot(routes)

  ],
  providers: [
    provideAnimationsAsync()
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}

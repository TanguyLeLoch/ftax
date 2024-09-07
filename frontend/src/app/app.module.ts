import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppComponent } from './app.component';
import { TransactionModule } from "./transaction/transaction.module";
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { RouterModule, Routes } from "@angular/router";
import { TransactionListComponent } from "./transaction/components/transaction-list/transaction-list.component";
import { LedgerBookComponent } from "./ledger/components/ledger-book/ledger-book.component";
import { LedgerModule } from "./ledger/ledger.module";
import { CoreModule } from "./core/core.module";
import { provideCharts, withDefaultRegisterables } from 'ng2-charts';
import { TxListComponent } from "./transaction/components/simplified/tx-list/tx-list.component";
import { basePathProvider } from "../config/basePathProvider";
import { LoginComponent } from "./core/components/login/login.component";
import { AuthComponent } from "./core/components/auth/auth.component";
import { AuthGuard } from "./auth.guard";


const routes: Routes = [
  {path: 'login', component: LoginComponent},
  {path: 'auth', component: AuthComponent},
  {path: 'simplified', component: TxListComponent, canActivate: [AuthGuard]},
  {path: 'transactions', component: TransactionListComponent},
  {path: 'ledger', component: LedgerBookComponent},
  {path: '', redirectTo: '/simplified', pathMatch: 'full'} // default route
];

@NgModule({
  declarations: [
    AppComponent,

  ],
  imports: [
    BrowserModule,
    TransactionModule,
    LedgerModule,
    FontAwesomeModule,
    RouterModule.forRoot(routes),
    CoreModule,


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

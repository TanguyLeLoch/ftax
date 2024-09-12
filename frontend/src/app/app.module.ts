import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppComponent } from './app.component';
import { TransactionModule } from "./transaction/transaction.module";
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { RouterModule, Routes } from "@angular/router";
import { CoreModule } from "./core/core.module";
import { provideCharts, withDefaultRegisterables } from 'ng2-charts';
import { TxListComponent } from "./transaction/components/tx-list/tx-list.component";
import { basePathProvider } from "../config/basePathProvider";
import { LoginComponent } from "./core/components/login/login.component";
import { AuthComponent } from "./core/components/auth/auth.component";
import { AuthGuard } from "./auth.guard";


const routes: Routes = [
  {path: 'login', component: LoginComponent},
  {path: 'auth', component: AuthComponent},
  {path: 'transactions', component: TxListComponent, canActivate: [AuthGuard]},
  {path: '', redirectTo: '/transactions', pathMatch: 'full'} // default route
];

@NgModule({
  declarations: [
    AppComponent,

  ],
  imports: [
    BrowserModule,
    TransactionModule,
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

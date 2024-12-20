import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HTTP_INTERCEPTORS, provideHttpClient, withInterceptorsFromDi } from "@angular/common/http";
import { provideNativeDateAdapter } from "@angular/material/core";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { ToastComponent } from "./components/toast/toast.component";
import { FaIconComponent } from "@fortawesome/angular-fontawesome";
import { SidenavComponent } from "./components/sidenav/sidenav.component";
import { RouterOutlet } from "@angular/router";
import { LoginComponent } from "./components/login/login.component";
import { MaterialModule } from "../material/material.module";
import { NavbarComponent } from "../components/navbar/navbar.component";
import { AuthComponent } from "./components/auth/auth.component";
import { HttpErrorInterceptor } from "./interceptors/HttpErrorInterceptor";
import { AuthInterceptor } from "./interceptors/AuthInterceptor";
import { ProfileComponent } from "./components/profile/profile.component";
import { FileUploadComponent } from "./components/file-upload/file-upload.component";


@NgModule({
  declarations: [
    ToastComponent,
    SidenavComponent,
    LoginComponent,
    NavbarComponent,
    AuthComponent,
    ProfileComponent,
    FileUploadComponent
  ],
  exports: [
    NavbarComponent,
    ToastComponent,
    SidenavComponent,
    LoginComponent,
    AuthComponent,
    ProfileComponent,
    FileUploadComponent
  ], imports: [CommonModule,
    FormsModule,
    FaIconComponent,
    ReactiveFormsModule,
    FormsModule,
    RouterOutlet,
    MaterialModule], providers: [provideNativeDateAdapter(),
    {
      provide: HTTP_INTERCEPTORS,
      useClass: HttpErrorInterceptor,
      multi: true
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    }, provideHttpClient(withInterceptorsFromDi())]
})
export class CoreModule {
}

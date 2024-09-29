import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HTTP_INTERCEPTORS, provideHttpClient, withInterceptorsFromDi } from "@angular/common/http";
import { provideNativeDateAdapter } from "@angular/material/core";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { ToastComponent } from "./components/toast/toast.component";
import { FileUploadComponent } from "./components/file-upload/file-upload.component";
import { FaIconComponent } from "@fortawesome/angular-fontawesome";
import { ModalComponent } from "./components/modal/modal.component";
import { SidenavComponent } from "./components/sidenav/sidenav.component";
import { RouterOutlet } from "@angular/router";
import { LoginComponent } from "./components/login/login.component";
import { MaterialModule } from "../material/material.module";
import { NavbarComponent } from "../components/navbar/navbar.component";
import { AuthComponent } from "./components/auth/auth.component";
import { HttpErrorInterceptor } from "./interceptors/HttpErrorInterceptor";
import { AuthInterceptor } from "./interceptors/AuthInterceptor";
import { ProfileComponent } from "./components/profile/profile.component";


@NgModule({ declarations: [
        ToastComponent,
        FileUploadComponent,
        ModalComponent,
        SidenavComponent,
        LoginComponent,
        NavbarComponent,
        AuthComponent,
        ProfileComponent
    ],
    exports: [
        NavbarComponent,
        ToastComponent,
        FileUploadComponent,
        ModalComponent,
        SidenavComponent,
        LoginComponent,
        AuthComponent,
        ProfileComponent
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
        }, provideHttpClient(withInterceptorsFromDi())] })
export class CoreModule {
}

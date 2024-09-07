import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from "@angular/common/http";
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


@NgModule({
  declarations: [
    ToastComponent,
    FileUploadComponent,
    ModalComponent,
    SidenavComponent,
    LoginComponent,
    NavbarComponent
  ],
  imports: [
    CommonModule,
    HttpClientModule,
    FormsModule,
    FaIconComponent,
    ReactiveFormsModule,
    FormsModule,
    RouterOutlet,
    MaterialModule,
  ],
  exports: [
    NavbarComponent,
    ToastComponent,
    FileUploadComponent,
    ModalComponent,
    SidenavComponent,
    LoginComponent,
  ],
  providers: [provideNativeDateAdapter()],
})
export class CoreModule {
}

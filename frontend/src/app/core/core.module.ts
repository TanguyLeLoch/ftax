import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {HttpClientModule} from "@angular/common/http";
import {MatSelectModule} from "@angular/material/select";
import {MatDatepickerModule} from "@angular/material/datepicker";
import {provideNativeDateAdapter} from "@angular/material/core";
import {MatInputModule} from "@angular/material/input";
import {MatFormFieldModule} from "@angular/material/form-field";
import {FormsModule} from "@angular/forms";
import {ToastComponent} from "./components/toast/toast.component";
import {FileUploadComponent} from "./components/file-upload/file-upload.component";
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {DropdownComponent} from "./components/dropdown/dropdown.component";


@NgModule({
  declarations: [ToastComponent, FileUploadComponent, DropdownComponent],
  imports: [
    CommonModule,
    HttpClientModule,
    MatSelectModule,
    MatDatepickerModule,
    MatFormFieldModule,
    MatInputModule,
    FormsModule,
    FaIconComponent,

  ],
  exports: [
    MatSelectModule,
    MatDatepickerModule,
    MatFormFieldModule,
    MatInputModule,
    ToastComponent,
    FileUploadComponent,
    DropdownComponent
  ],
  providers: [provideNativeDateAdapter()],
})
export class CoreModule {
}

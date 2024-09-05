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
import {ModalComponent} from "./components/modal/modal.component";
import {MatExpansionModule} from "@angular/material/expansion";
import { SidenavComponent } from "./components/sidenav/sidenav.component";
import { MatDrawerContainer, MatSidenavModule } from "@angular/material/sidenav";
import { MatButtonModule } from "@angular/material/button";
import { MatCheckboxModule } from "@angular/material/checkbox";
import { MatIcon } from "@angular/material/icon";
import { RouterOutlet } from "@angular/router";
import { MatDivider } from "@angular/material/divider";


@NgModule({
  declarations: [
    ToastComponent,
    FileUploadComponent,
    ModalComponent,
    SidenavComponent],
    imports: [
        CommonModule,
        HttpClientModule,
        MatSelectModule,
        MatDatepickerModule,
        MatFormFieldModule,
        MatInputModule,
        FormsModule,
        FaIconComponent,
        MatDrawerContainer,
        MatSidenavModule,
        MatCheckboxModule,
        FormsModule,
        MatButtonModule,
        MatIcon,
        RouterOutlet,
        MatDivider

    ],
  exports: [
    MatSelectModule,
    MatDatepickerModule,
    MatFormFieldModule,
    MatInputModule,
    ToastComponent,
    FileUploadComponent,
    ModalComponent,
    MatExpansionModule,
    SidenavComponent
  ],
  providers: [provideNativeDateAdapter()],
})
export class CoreModule {
}

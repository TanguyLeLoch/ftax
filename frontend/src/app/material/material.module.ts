import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatSelectModule } from "@angular/material/select";
import { MatDatepickerModule } from "@angular/material/datepicker";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { MatDrawer, MatDrawerContainer, MatSidenavContainer, MatSidenavModule } from "@angular/material/sidenav";
import { MatCheckboxModule } from "@angular/material/checkbox";
import { MatButtonModule } from "@angular/material/button";
import { MatIcon, MatIconModule } from "@angular/material/icon";
import { MatDivider } from "@angular/material/divider";
import { MatSlideToggle } from "@angular/material/slide-toggle";
import { MatCard, MatCardContent, MatCardHeader, MatCardModule } from "@angular/material/card";
import { MatNativeDateModule, provideNativeDateAdapter } from "@angular/material/core";
import { MatToolbar } from "@angular/material/toolbar";
import { MatExpansionModule } from "@angular/material/expansion";
import { MatAutocompleteModule } from "@angular/material/autocomplete";


@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    MatAutocompleteModule,
    MatSelectModule,
    MatDatepickerModule,
    MatFormFieldModule,
    MatInputModule,
    MatDrawerContainer,
    MatSidenavModule,
    MatCheckboxModule,
    MatButtonModule,
    MatIcon,
    MatDivider,
    MatSlideToggle,
    MatCardHeader,
    MatCard,
    MatCardContent,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatFormFieldModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatIconModule,
    MatToolbar,
    MatDrawerContainer,
    MatDrawer,
    MatSidenavContainer,
    MatCardModule,
    MatSelectModule,
    MatDatepickerModule,
    MatFormFieldModule,
    MatInputModule,
    MatExpansionModule,
  ],
  exports: [
    MatAutocompleteModule,
    MatSelectModule,
    MatDatepickerModule,
    MatFormFieldModule,
    MatInputModule,
    MatDrawerContainer,
    MatSidenavModule,
    MatCheckboxModule,
    MatButtonModule,
    MatIcon,
    MatDivider,
    MatSlideToggle,
    MatCardHeader,
    MatCard,
    MatCardContent,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatFormFieldModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatIconModule,
    MatToolbar,
    MatDrawerContainer,
    MatDrawer,
    MatSidenavContainer,
    MatCardModule,
    MatSelectModule,
    MatDatepickerModule,
    MatFormFieldModule,
    MatInputModule,
    MatExpansionModule,
  ],
  providers: [
    provideNativeDateAdapter(),
  ]

})
export class MaterialModule {
}

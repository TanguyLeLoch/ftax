import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatAutocompleteModule } from "@angular/material/autocomplete";
import { MatButtonModule } from "@angular/material/button";
import { MatButtonToggleModule } from "@angular/material/button-toggle";
import { MatCardModule } from "@angular/material/card";
import { MatCheckboxModule } from "@angular/material/checkbox";
import { MatChipsModule } from "@angular/material/chips";
import { MatNativeDateModule, provideNativeDateAdapter } from "@angular/material/core";
import { MatDatepickerModule } from "@angular/material/datepicker";
import { MatDialogModule } from "@angular/material/dialog";
import { MatDividerModule } from "@angular/material/divider";
import { MatExpansionModule } from "@angular/material/expansion";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatGridListModule } from "@angular/material/grid-list";
import { MatIconModule } from "@angular/material/icon";
import { MatInputModule } from "@angular/material/input";
import { MatListModule } from "@angular/material/list";
import { MatProgressBarModule } from "@angular/material/progress-bar";
import { MatProgressSpinnerModule } from "@angular/material/progress-spinner";
import { MatSelectModule } from "@angular/material/select";
import { MatSidenavModule } from "@angular/material/sidenav";
import { MatSlideToggleModule } from "@angular/material/slide-toggle";
import { MatToolbarModule } from "@angular/material/toolbar";
import { MatTooltipModule } from "@angular/material/tooltip";

const materialModules = [
  MatAutocompleteModule,
  MatButtonModule,
  MatButtonToggleModule,
  MatCardModule,
  MatCheckboxModule,
  MatChipsModule,
  MatDatepickerModule,
  MatDialogModule,
  MatDividerModule,
  MatExpansionModule,
  MatFormFieldModule,
  MatGridListModule,
  MatIconModule,
  MatInputModule,
  MatListModule,
  MatNativeDateModule,
  MatProgressBarModule,
  MatProgressSpinnerModule,
  MatSelectModule,
  MatSidenavModule,
  MatSlideToggleModule,
  MatToolbarModule,
  MatTooltipModule,
];

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    ...materialModules,
  ],
  exports: materialModules,
  providers: [
    provideNativeDateAdapter(),
  ]
})
export class MaterialModule {
}

//
// @NgModule({
//   declarations: [],
//   imports: [
//     CommonModule,
//     MatAutocompleteModule,
//     MatSelectModule,
//     MatDatepickerModule,
//     MatFormFieldModule,
//     MatInputModule,
//     MatDrawerContainer,
//     MatSidenavModule,
//     MatCheckboxModule,
//     MatButtonModule,
//     MatIcon,
//     MatDivider,
//     MatSlideToggle,
//     MatCardHeader,
//     MatCard,
//     MatCardContent,
//     MatInputModule,
//     MatSelectModule,
//     MatFormFieldModule,
//     MatDatepickerModule,
//     MatNativeDateModule,
//     MatIconModule,
//     MatToolbar,
//     MatDrawerContainer,
//     MatDrawer,
//     MatSidenavContainer,
//     MatCardModule,
//     MatSelectModule,
//     MatDatepickerModule,
//     MatFormFieldModule,
//     MatInputModule,
//     MatExpansionModule,
//     MatProgressSpinner,
//     MatNavList,
//     MatTooltip,
//     MatDialogActions,
//     MatDialogContent,
//     MatGridList,
//     MatGridTile,
//     MatDialogContainer,
//     MatProgressSpinnerModule,
//     MatProgressBarModule,
//     MatChipsModule,
//     MatButtonToggleModule,
//   ],
//   exports: [
//     MatAutocompleteModule,
//     MatSelectModule,
//     MatDatepickerModule,
//     MatFormFieldModule,
//     MatInputModule,
//     MatDrawerContainer,
//     MatSidenavModule,
//     MatCheckboxModule,
//     MatButtonModule,
//     MatIcon,
//     MatDivider,
//     MatSlideToggle,
//     MatCardHeader,
//     MatCard,
//     MatCardContent,
//     MatInputModule,
//     MatSelectModule,
//     MatButtonModule,
//     MatFormFieldModule,
//     MatDatepickerModule,
//     MatNativeDateModule,
//     MatIconModule,
//     MatToolbar,
//     MatDrawerContainer,
//     MatDrawer,
//     MatSidenavContainer,
//     MatCardModule,
//     MatSelectModule,
//     MatDatepickerModule,
//     MatFormFieldModule,
//     MatInputModule,
//     MatExpansionModule,
//     MatProgressSpinner,
//     MatNavList,
//     MatTooltip,
//     MatDialogActions,
//     MatDialogContent,
//     MatGridList,
//     MatGridTile,
//     MatDialogContainer,
//     MatProgressSpinnerModule,
//     MatProgressBarModule,
//     MatChipsModule,
//     MatButtonToggleModule
//   ],
//   providers: [
//     provideNativeDateAdapter(),
//   ]
//
// })
// export class MaterialModule {
// }

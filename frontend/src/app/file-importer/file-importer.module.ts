import {NgModule} from '@angular/core';
import {CommonModule, NgOptimizedImage} from '@angular/common';
import {CoreModule} from "../core/core.module";
import {PlatformListComponent} from "./components/platform-list/platform-list.component";
import {ImportFileComponent} from "./components/import-file/import-file.component";


@NgModule({
  declarations: [
    ImportFileComponent,
    PlatformListComponent,
  ],
  exports: [PlatformListComponent
  ],
  imports: [
    CommonModule,
    CoreModule,
    NgOptimizedImage,
  ]
})
export class FileImporterModule {
}

import {NgModule} from '@angular/core';
import {CommonModule, NgOptimizedImage} from '@angular/common';
import {CoreModule} from "../core/core.module";
import {PlatformListComponent} from "./components/platform-list/platform-list.component";


@NgModule({
  declarations: [PlatformListComponent],
  exports: [PlatformListComponent],
  imports: [
    CommonModule,
    CoreModule,
    NgOptimizedImage
  ]
})
export class FileImporterModule {
}

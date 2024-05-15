import {Component, Input} from '@angular/core';
import {Platform} from "../platform-list/platform-list.component";

@Component({
  selector: 'app-import-file',
  templateUrl: './import-file.component.html',
  styleUrl: './import-file.component.scss'
})
export class ImportFileComponent {

  @Input() platform!: Platform


}

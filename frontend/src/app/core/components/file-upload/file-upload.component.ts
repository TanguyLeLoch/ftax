import {Component, EventEmitter, Output} from '@angular/core';
import {faCloudUpload, faTrash} from '@fortawesome/free-solid-svg-icons';
import {ToastService} from "../../services/toast.service";


@Component({
  selector: 'app-file-upload',
  templateUrl: './file-upload.component.html',
  styleUrls: ['./file-upload.component.scss']
})
export class FileUploadComponent {
  protected readonly faCloudUpload = faCloudUpload;
  protected readonly faTrash = faTrash;
  @Output() closeModal: EventEmitter<void> = new EventEmitter<void>();
  @Output() upload: EventEmitter<File[]> = new EventEmitter<File[]>();

  protected files: File[] = [];

  constructor(private toastService: ToastService) {}

  onFileSelected(event: any): void {

    for (const file of event.target.files) {
      if (this.files.find(f => f.name === file.name) !== undefined) {
        this.toastService.showToast("warning",
        "File already exists with the same name, be sure it's not a duplicate");
      }
      this.files.push(file);
    }
  }


  onCloseModal() {
    console.log('close modal')
    this.closeModal.emit();
  }



  removeFile(file: File) {
    this.files = this.files.filter(f => f !== file);
  }

  getFileName(file: File) {
    if (file.name.length > 35) {
      return '...' + file.name.slice(file.name.length - 32);
    }
    return file.name;
  }

  onUpload() {
    console.log('uploading files');
    this.upload.emit(this.files);
  }
}

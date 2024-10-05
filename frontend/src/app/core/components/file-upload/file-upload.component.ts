import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ToastService } from "../../services/toast.service";

@Component({
  selector: 'app-file-upload',
  templateUrl: './file-upload.component.html',
  styleUrl: './file-upload.component.scss'
})
export class FileUploadComponent {
  @Input() acceptedFileTypes: string = '.csv,.xlsx,.xls';
  @Output() fileSelected = new EventEmitter<File>();

  isDragOver = false;
  selectedFile: File | null = null;

  constructor(private toast: ToastService) { }

  onDragOver(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();
    this.isDragOver = true;
  }

  onDragLeave(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();
    this.isDragOver = false;
  }

  onDrop(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();
    this.isDragOver = false;

    const files = event.dataTransfer?.files;
    if (files && files.length > 0) {
      this.handleFileInput(files[0]);
    }
  }

  onFileSelected(event: Event) {
    const element = event.target as HTMLInputElement;
    const files = element.files;
    if (files && files.length > 0) {
      this.handleFileInput(files[0]);
    }
  }

  handleFileInput(file: File) {
    const validExtensions = this.acceptedFileTypes.split(',').map(ext => ext.trim().toLowerCase());
    const fileExtension = '.' + file.name.split('.').pop()?.toLowerCase();

    if (validExtensions.includes(fileExtension)) {
      this.selectedFile = file;
      this.fileSelected.emit(file);
    } else {
      this.toast.showToast('error', 'Invalid file type. Please select a CSV or Excel file.');
    }
  }

  removeFile() {
    this.selectedFile = null;
    this.fileSelected.emit(undefined);
  }
}

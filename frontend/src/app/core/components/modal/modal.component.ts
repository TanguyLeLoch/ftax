import {Component, EventEmitter, HostListener, Input, Output} from '@angular/core';

@Component({
  selector: 'app-modal',
  templateUrl: './modal.component.html',
  styleUrl: './modal.component.scss'
})
export class ModalComponent {
  @Input() isOpen = false;
  @Input() title = '';
  @Output() close = new EventEmitter<void>();

  closeModal(): void {
    this.isOpen = false;
    this.close.emit();
  }

  @HostListener('document:keydown.escape', ['$event'])
  onEscKeyPress(event: KeyboardEvent): void {
    this.closeModal();
  }
}

import {Component, Input} from '@angular/core';
import {NgIf} from "@angular/common";

@Component({
  selector: 'app-modal',
  standalone: true,
  imports: [
    NgIf
  ],
  templateUrl: './modal.component.html',
  styleUrl: './modal.component.scss'
})
export class ModalComponent {
  isVisible: boolean = false;
  @Input() width = '300px';  // Default width with unit
  @Input() height = '150px'; // Default height with unit


  open() {
    this.isVisible = true;
  }

  close() {
    this.isVisible = false;
  }
}

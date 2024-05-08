import {Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
  selector: 'app-standard-button',
  standalone: true,
  imports: [],
  templateUrl: './standard-button.component.html',
  styleUrl: './standard-button.component.scss'
})
export class StandardButtonComponent {
  @Input() type: string = 'button'; // Default type of button
  @Input() disabled: boolean = false; // Button disabled state
  @Output() clicked = new EventEmitter<Event>(); // Event emitter for click events

  onClick(event: Event): void {
    this.clicked.emit(event);
  }
}

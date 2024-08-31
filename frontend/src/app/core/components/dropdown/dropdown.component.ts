import {Component, ElementRef, forwardRef, HostListener, Input} from '@angular/core';
import {ControlValueAccessor, NG_VALUE_ACCESSOR} from "@angular/forms";
import {faChevronDown} from "@fortawesome/free-solid-svg-icons";

@Component({
  selector: 'app-dropdown',
  templateUrl: './dropdown.component.html',
  styleUrls: ['./dropdown.component.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => DropdownComponent),
      multi: true
    }
  ]
})
export class DropdownComponent implements ControlValueAccessor {
  @Input() options: { value: string, label: string }[] = [];
  @Input() placeholder: string = 'Select an option';

  isOpen = false;
  selectedOption: string | null = null;
  onChange: any = () => {};
  onTouched: any = () => {};

  constructor(private eRef: ElementRef) {}

  @HostListener('document:click', ['$event'])
  clickout(event: Event) {
    if (!this.eRef.nativeElement.contains(event.target)) {
      this.isOpen = false;
    }
  }

  toggleDropdown() {
    this.isOpen = !this.isOpen;
    this.onTouched();
  }

  selectOption(value: string) {
    this.selectedOption = this.options.find(opt => opt.value === value)?.label || null;
    this.isOpen = false;
    this.onChange(value);
    this.onTouched();
  }

  writeValue(value: string): void {
    this.selectedOption = this.options.find(opt => opt.value === value)?.label || null;
  }

  registerOnChange(fn: any): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }

  setDisabledState?(isDisabled: boolean): void {
    // Implement if needed
  }

  protected readonly faChevronDown = faChevronDown;
}

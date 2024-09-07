import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from "@angular/forms";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  isLoginMode = true;
  form: FormGroup;

  constructor(private fb: FormBuilder) {
    this.form = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      name: ['']
    });
  }

  toggleMode() {
    this.isLoginMode = !this.isLoginMode;
    if (this.isLoginMode) {
      this.form.get('name')?.clearValidators();
      this.form.get('name')?.updateValueAndValidity();
    } else {
      this.form.get('name')?.setValidators(Validators.required);
      this.form.get('name')?.updateValueAndValidity();
    }
  }

  onSubmit() {
    if (this.form.valid) {
      console.log(this.form.value);
      // Here you would typically call your authentication service
    }
  }
}

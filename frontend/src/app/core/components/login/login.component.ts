import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { AuthService } from "../../services/auth.service";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  isLoginMode = true;
  form: FormGroup;
  isSubmitted = false;

  constructor(private fb: FormBuilder, private authService: AuthService) {
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
      this.authService.sendMagicLink(this.form.value.email, this.form.value.name).subscribe(
        () => {
          this.isSubmitted = true;
        }
      );
    }
  }
}

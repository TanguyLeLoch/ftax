import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { AuthService } from "../../services/auth.service";
import { catchError, of } from "rxjs";
import { ToastService } from "../../services/toast.service";
import { HttpErrorResponse } from "@angular/common/http";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  isLoginMode = true;
  form: FormGroup;
  isSubmitted = false;

  constructor(private fb: FormBuilder, private authService: AuthService, private toastService: ToastService) {
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
      this.authService.sendMagicLink(this.form.value.email, this.form.value.name)
        .pipe(
          catchError((error: HttpErrorResponse) => {
            console.log(error);
            if (error.status === 404) {
              this.isLoginMode = false;
              this.toastService.showToast('error', 'Your email is not registered, please register first.');
            } else {
              this.toastService.showToast('error', 'An unexpected error occurred. Please try again.');
            }
            return of(false);
          })
        )
        .subscribe(
          (success) => {
            if (success) {
              this.isSubmitted = true;
              this.toastService.showToast('success', 'Magic link sent successfully!');
            }
          }
        );
    }
  }
}

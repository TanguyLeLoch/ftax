import {Component} from '@angular/core';
import {AuthService} from "../../services/auth.service";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  email: string = '';
  message: string | null = null;

  constructor(private authService: AuthService) {
  }

  sendMagicLink(): void {
    this.authService.sendMagicLink(this.email).subscribe({
      next: () => {
        this.message = 'Magic link sent! Check your email.';
      },
      error: () => {
        this.message = 'Failed to send magic link.';
      }
    });
  }
}

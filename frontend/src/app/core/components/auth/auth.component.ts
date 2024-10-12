import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {AuthControllerService, AuthResponse} from "../../model";
import {catchError, of} from "rxjs";
import {ToastService} from "../../services/toast.service";
import {CookieService} from "ngx-cookie-service";

@Component({
  selector: 'app-auth',
  templateUrl: './auth.component.html',
  styleUrl: './auth.component.scss'
})
export class AuthComponent implements OnInit {

  constructor(private route: ActivatedRoute,
              private authController: AuthControllerService,
              private router: Router,
              private toastService: ToastService,
              private cookieService: CookieService
  ) {
  }

  ngOnInit(): void {
    // Get query parameters
    this.route.queryParams.subscribe(params => {
      const email = params['email'] || null;
      const hash = params['hash'] || null;

      if (!email || !hash) {
        this.router.navigate(['/login']);
        return;
      }

      this.authController.verifyHash(email, hash).pipe(
        catchError((error) => {
          this.toastService.showToast('error', 'Error occurred during hash verification');
          this.router.navigate(['/login']);
          // Redirect to login on error
          return of("error"); // Return a safe value or an empty observable to prevent further actions
        })
      ).subscribe(result => {
        if (result === 'error') {
          return;
        }
        const response = result as AuthResponse
        this.cookieService.set('hash', response.hash);
        this.cookieService.set('email', email);
        this.router.navigate(['/']);
      });
    });
  }
}

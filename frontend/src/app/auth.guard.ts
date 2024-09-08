import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot } from '@angular/router';
import { Observable } from 'rxjs';
import { CookieService } from "ngx-cookie-service";

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  constructor(private cookieService: CookieService, private router: Router) {
  }

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean> | Promise<boolean> | boolean {

    const cookie = this.cookieService.get('hash');
    const isAuthenticated = !!cookie;
    console.log('isAuthenticated', isAuthenticated);

    if (!isAuthenticated) {
      // Redirect to login page if the user is not authenticated
      this.router.navigate(['/login']);
      return false;
    }
    return true; // Allow route access if authenticated
  }
}

import { Injectable } from '@angular/core';
import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CookieService } from "ngx-cookie-service";
import { environment } from "../../../environments/environment";


@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(private cookieService: CookieService) {

  }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const email = this.cookieService.get('email');
    const hash = this.cookieService.get('hash');

    if (!email || !hash) {
      return next.handle(request);
    }

    if (environment.production) {
      return next.handle(request);
    }

    // Create the basic auth header
    const basicAuthHeader = 'Basic ' + btoa(email + ':' + hash);

    // Clone the request and add the new header
    const modifiedRequest = request.clone({
      setHeaders: {
        'Authorization': basicAuthHeader
      }
    });

    // Pass the cloned request instead of the original request to the next handle
    return next.handle(modifiedRequest);
  }
}

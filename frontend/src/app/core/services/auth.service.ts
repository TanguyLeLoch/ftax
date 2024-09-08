import { Injectable } from '@angular/core';
import { AuthControllerService, Client } from "../model";
import { map, Observable, tap } from "rxjs";
import { CookieService } from "ngx-cookie-service";

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private authController: AuthControllerService, private cookieService: CookieService) {
  }


  sendMagicLink(email: string, name: string | undefined): Observable<boolean> {
    const client: Client = {
      email,
      username: name
    }

    return this.authController.createHashAndSendMagicLink(client).pipe(
      tap(response => {
        this.cookieService.set("email", email, {expires: undefined});
        this.cookieService.set("hash", response.hash, {expires: undefined});
      }),
      map(() => true)
    );
  }
}

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


  sendMagicLink(email: string, name: string | undefined): Observable<void> {
    const client: Client = {
      email,
      username: name
    }

    return this.authController.createHashAndSendMagicLink(client).pipe(
      tap(response => {
        console.log(response);
        this.cookieService.set("token", response.hash, {expires: new Date(Date.now() + (3 * 365 * 24 * 60 * 60 * 1000))});
      }),
      map(() => void 0)
    );
  }
}

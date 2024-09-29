import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { CookieService } from "ngx-cookie-service";
import { switchMap, tap } from 'rxjs/operators';
import { Client, ClientControllerService, Profile } from "../model";

@Injectable({
  providedIn: 'root'
})
export class ProfileService {
  private clientSubject = new BehaviorSubject<Client | null>(null);
  client$ = this.clientSubject.asObservable();

  constructor(
    private clientService: ClientControllerService,
    private cookieService: CookieService
  ) {
    this.loadClient();
  }

  private loadClient(): void {
    const email = this.cookieService.get('email');
    if (email) {
      this.clientService.getClient(email).pipe(
        tap(client => this.clientSubject.next(client))
      ).subscribe();
    }
  }

  updateCalculationMethod(value: Profile.CalculationMethodEnum): void {
    const client = this.clientSubject.value;
    if (client) {
      client.profile!.calculationMethod = value;
      this.clientService.updateClient(client.email, client.username!, value).pipe(
        switchMap(() => this.clientService.getClient(client.email))
      ).subscribe(updatedClient => this.clientSubject.next(updatedClient));
    }
  }

  updateUsername(newUsername: string): void {
    const client = this.clientSubject.value;
    if (client) {
      client.username = newUsername;
      this.clientService.updateClient(client.email, newUsername, client.profile!.calculationMethod).pipe(
        switchMap(() => this.clientService.getClient(client.email))
      ).subscribe(updatedClient => this.clientSubject.next(updatedClient));
    }
  }

  getCalculationMethod(): Profile.CalculationMethodEnum | null {
    return this.clientSubject.value?.profile?.calculationMethod || null;
  }
}

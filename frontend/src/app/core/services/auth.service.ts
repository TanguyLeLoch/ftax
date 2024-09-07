import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {map} from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private tokenKey = 'auth-token';  // Key for storing token in local storage

  constructor(private http: HttpClient) {
  }

  login(username: string, password: string): Observable<any> {
    return this.http.post<{ token: string }>('/api/auth/login', {username, password})
      .pipe(map(response => {
        if (response.token) {
          localStorage.setItem(this.tokenKey, response.token);
        }
        return response;
      }));
  }


  sendMagicLink(email: string): Observable<string> {
    throw new Error('Not implemented');
  }

}

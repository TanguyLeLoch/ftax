import { Injectable } from '@angular/core';
import { Token, TokenControllerService } from "../model";
import { BehaviorSubject, Observable, tap } from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class TokenService {
  private tokens: BehaviorSubject<Token[]> = new BehaviorSubject<Token[]>([]);
  public tokens$ = this.tokens.asObservable();
  private allTokens: Token[] = [];

  constructor(private controller: TokenControllerService) {
  }

  fetchAllTokens(): Observable<Token[]> {
    return this.controller.getAllTokens().pipe(
      tap((tokens: Token[]) => {
        this.allTokens = tokens;
        this.tokens.next(tokens);
      })
    );
  }

  getTokens(): Token[] {
    return this.allTokens;
  }

  getToken(id: string): Token | undefined {
    return this.allTokens.find(token => token.id === id);
  }

  createToken(token: Token): Observable<Token> {
    return this.controller.post1(token)
      .pipe(
        tap(token => {
          this.allTokens.push(token);
          this.tokens.next(this.allTokens);
        })
      );
  }
}

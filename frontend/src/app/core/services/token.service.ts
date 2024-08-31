import {Injectable} from '@angular/core';
import {Token, TokenControllerService} from "../model";
import {BehaviorSubject} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class TokenService {
  private tokens: BehaviorSubject<Token[]> = new BehaviorSubject<Token[]>([]);
  public tokens$ = this.tokens.asObservable();
  private allTokens: Token[] = [];

  constructor(private controller: TokenControllerService) {
  }

  fetchAllTokens(): void {
    this.controller.getAllTokens()
      .subscribe((tokens: Token[]) => {
        this.allTokens = tokens;
        this.tokens.next(tokens);
      });
  }

  getTokens(): Token[] {
    return this.allTokens;
  }

}

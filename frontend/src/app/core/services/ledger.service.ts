import {Injectable} from '@angular/core';
import {LedgerBook, LedgerBookControllerService} from "../model";
import {BehaviorSubject, tap} from "rxjs";

@Injectable({
    providedIn: 'root'
})
export class LedgerService {

  private ledgerBook: LedgerBook | null = null;
  public tokens = new Set<string>();

  private ledgerBookSubject = new BehaviorSubject<LedgerBook | null>(this.ledgerBook);
  public ledgerBook$ = this.ledgerBookSubject.asObservable();


  constructor(private ledgerBookControllerService: LedgerBookControllerService) {
  }

  generateLedgerBook() {
    this.ledgerBookControllerService.generateLedgerBook().pipe(
      tap((ledgerBook: LedgerBook) => {
        this.ledgerBook = ledgerBook;
        this.ledgerBookSubject.next(ledgerBook);
        this.getTokens(ledgerBook.id);
      }),
    ).subscribe();
  }

  getTokens(bookId: string): void {
    this.ledgerBookControllerService.getTokens(bookId).pipe(
      tap((tokens: Set<string>) => {
        this.tokens = tokens;
      })).subscribe();
  }

}

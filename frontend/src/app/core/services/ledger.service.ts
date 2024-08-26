import {Injectable} from '@angular/core';
import {LedgerBook, LedgerBookControllerService, TimelineItem} from "../model";
import {BehaviorSubject, Observable, tap} from "rxjs";

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
        this.tokens = ledgerBook.tokens;

      }),
    ).subscribe();
  }

  getTimelineForToken(bookId: string, selectedToken: string): Observable<TimelineItem[]> {
    return this.ledgerBookControllerService.getTimelineForToken(bookId, selectedToken);
  }
}

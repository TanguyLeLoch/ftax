import {ChangeDetectorRef, Component, OnDestroy, OnInit} from '@angular/core';
import {LedgerService} from "../../../core/services/ledger.service";
import {LedgerBook, TimelineItem} from "../../../core/model";
import {Subscription, tap} from "rxjs";


@Component({
  selector: 'app-ledger-book',
  templateUrl: './ledger-book.component.html',
  styleUrl: './ledger-book.component.scss'
})
export class LedgerBookComponent implements OnInit, OnDestroy {

  ledgerBook: LedgerBook | null = null;
  ledgerBookSub!: Subscription;


  date: Date | null = null;

  timeline: Map<string, TimelineItem[]>
  tokens: string[] = [];


  constructor(private ledgerService: LedgerService, private cdr: ChangeDetectorRef) {
    this.timeline = new Map();
  }

  ngOnInit(): void {
    this.ledgerBookSub = this.ledgerService.ledgerBook$.subscribe(
      ledgerBook => {
        this.ledgerBook = ledgerBook;
      }
    );
  }

  getTokens() {
    return this.ledgerService.tokens
  }

  ngOnDestroy(): void {
    this.ledgerBookSub.unsubscribe();
  }

  generateLedgerBook() {
    this.ledgerService.generateLedgerBook();
  }

  onTokenChange(event: Event) {
    const selectedToken = (event.target as HTMLSelectElement).value;
    if (this.tokens.includes(selectedToken)) {
      return;
    }
    this.ledgerService.getTimelineForToken(this.ledgerBook!.id, selectedToken).pipe(
      tap((timelineItems: TimelineItem[]) => {
        this.timeline.set(selectedToken, timelineItems);
        this.tokens.push(selectedToken);
      })
    ).subscribe();
  }
}


import {Component, OnDestroy, OnInit} from '@angular/core';
import {LedgerService} from "../../../core/services/ledger.service";
import {LedgerBook, TimelineItem} from "../../../core/model";
import {Subscription, tap} from "rxjs";
import {FormsModule} from "@angular/forms";
import {NgForOf, NgIf} from "@angular/common";


@Component({
  selector: 'app-ledger-book',
  standalone: true,
  imports: [
    FormsModule,
    NgIf,
    NgForOf
  ],
  templateUrl: './ledger-book.component.html',
  styleUrl: './ledger-book.component.scss'
})
export class LedgerBookComponent implements OnInit, OnDestroy {

  ledgerBook: LedgerBook | null = null;
  ledgerBookSub!: Subscription;


  date: Date | null = null;

  timeline: Map<string, TimelineItem[]>


  constructor(private ledgerService: LedgerService) {
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
    this.ledgerService.getTimelineForToken(this.ledgerBook!.id, selectedToken).pipe(
      tap((timelineItems: TimelineItem[]) => {
        this.timeline.set(selectedToken, timelineItems);
      })
    ).subscribe();


  }
}


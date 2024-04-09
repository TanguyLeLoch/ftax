import {Injectable} from '@angular/core';
import {LedgerBook, LedgerBookControllerService, Transaction, TransactionControllerService} from "../model";
import {BehaviorSubject} from "rxjs";

@Injectable({
    providedIn: 'root'
})
export class LedgerService {

    private ledgerBook: LedgerBook | null = null;

    private ledgerBookSubject = new BehaviorSubject<LedgerBook | null>(this.ledgerBook);
    public ledgerBook$ = this.ledgerBookSubject.asObservable();

    constructor(private ledgerBookControllerService: LedgerBookControllerService) {
    }

    generateLedgerBook() {
        this.ledgerBookControllerService.generateLedgerBook().subscribe(
            (ledgerBook: LedgerBook) => {
                this.ledgerBook = ledgerBook;
                this.ledgerBookSubject.next(ledgerBook);
            }
        );
    }
}

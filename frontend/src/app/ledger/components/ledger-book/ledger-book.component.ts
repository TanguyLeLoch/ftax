import {Component, OnInit} from '@angular/core';
import {LedgerService} from "../../../core/services/ledger.service";
import {Balance, LedgerBook} from "../../../core/model";
import {Subscription} from "rxjs";
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
export class LedgerBookComponent implements OnInit{

    ledgerBook: LedgerBook | null = null;
    ledgerBookSub!: Subscription;

    date: Date | null = null;
    balances: Balance[] = [];


    constructor(private ledgerService: LedgerService) {

    }

    ngOnInit(): void {
        this.ledgerBookSub = this.ledgerService.ledgerBook$.subscribe(
            ledgerBook => {
                this.ledgerBook = ledgerBook;
            }
        );
    }

    generateLedgerBook() {
        this.ledgerService.generateLedgerBook();
    }

    onDateChange() {
        console.log("Date changed to: ", this.date);
        this.date = new Date(this.date!);
        for (let entry of this.ledgerBook!.ledgerEntries) {

            const entryDate = new Date(entry.date);

            if (this.date! <= entryDate) {
                this.balances = Object.values(entry.balances);
            }
        }
        console.log("Balances: ", this.balances);

    }
}


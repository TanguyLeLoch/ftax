import {Component} from '@angular/core';
import {LedgerService} from "../../../core/services/ledger.service";
import {LedgerBookControllerService} from "../../../core/model";
import {publish} from "rxjs";

@Component({
  selector: 'app-ledger-book',
  standalone: true,
  imports: [],
  templateUrl: './ledger-book.component.html',
  styleUrl: './ledger-book.component.scss'
})
export class LedgerBookComponent {

  ledgerBook: any;

  constructor(private ledgerService: LedgerService) {

  }
}


import {Component, Input, OnInit} from '@angular/core';
import {DraftTransaction} from "../../../core/model";

@Component({
  selector: 'app-transaction-item',
  templateUrl: './transaction-item.component.html',
  styleUrls: ['./transaction-item.component.scss'],
})
export class TransactionItemComponent implements OnInit {

  @Input() transaction!: DraftTransaction;

  transactionTypes: DraftTransaction.TransactionTypeEnum[] = Object.values(DraftTransaction.TransactionTypeEnum);

  constructor() {
  }

  ngOnInit(): void {
    console.log(this.transaction)
  }
}

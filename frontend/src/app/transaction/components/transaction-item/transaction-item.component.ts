import {Component, Input, OnInit} from '@angular/core';
import {DraftTransaction} from "../../../core/model";

@Component({
  selector: 'app-transaction-item',
  templateUrl: './transaction-item.component.html',
  styleUrls: ['./transaction-item.component.scss']
})
export class TransactionItemComponent implements OnInit {

  @Input() transaction!: DraftTransaction;
  protected readonly JSON = JSON;

  constructor() {
  }

  ngOnInit(): void {
  }
}

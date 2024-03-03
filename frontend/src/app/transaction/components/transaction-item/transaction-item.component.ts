import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'app-transaction-item',
  templateUrl: './transaction-item.component.html',
  styleUrls: ['./transaction-item.component.scss']
})
export class TransactionItemComponent implements OnInit {

  @Input() transaction: Transaction;
  protected readonly JSON = JSON;

  constructor() {
  }

  ngOnInit(): void {
  }
}

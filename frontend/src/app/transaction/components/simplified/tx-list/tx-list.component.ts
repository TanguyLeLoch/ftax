import {Component} from '@angular/core';
import {TransactionSimplified, TransactionSimplifiedControllerService} from "../../../../core/model";

@Component({
  selector: 'app-tx-list',
  templateUrl: './tx-list.component.html',
  styleUrl: './tx-list.component.scss'
})
export class TxListComponent {
  txs: TransactionSimplified[] = [];

  constructor(private service: TransactionSimplifiedControllerService) {
    this.service.getAll().subscribe(txs => this.txs = txs);
  }

  newTx() {
    console.log(this.txs);
    const txSimplified = {} as TransactionSimplified;
    this.service.post(txSimplified).subscribe(tx => this.txs.push(tx));
  }
}

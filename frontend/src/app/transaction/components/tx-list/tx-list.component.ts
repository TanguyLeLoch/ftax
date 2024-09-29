import {Component, OnInit} from '@angular/core';
import {Transaction, TransactionControllerService} from "../../../core/model";


@Component({
  selector: 'app-tx-list',
  templateUrl: './tx-list.component.html',
  styleUrl: './tx-list.component.scss'
})
export class TxListComponent implements OnInit {
  txs: Transaction[] = [];


  constructor(private service: TransactionControllerService) {
    this.service.getAll().subscribe(txs => {
      txs.sort((a, b) => b.localDateTime.localeCompare(a.localDateTime));
      this.txs = txs
    });
  }

  ngOnInit(): void {
    this.fetchTxs();
  }


  private fetchTxs() {
    this.service.getAll().subscribe(txs => {
      txs.sort((a, b) => b.localDateTime.localeCompare(a.localDateTime));
      this.txs = txs
    });
  }

  newTx() {
    console.log(this.txs);
    const tx = {} as Transaction;
    this.service.post(tx).subscribe(tx => {
      this.txs = [tx, ...this.txs];
    });
  }

  importTx() {
  }


}

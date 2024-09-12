import { Component, OnInit } from '@angular/core';
import { Transaction, TransactionControllerService } from "../../../../core/model";


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
    const txSimplified = {} as Transaction;
    this.service.post(txSimplified).subscribe(tx => {
      this.txs = [tx, ...this.txs];
    });
  }

  computePnL() {
    this.service.computePnl("fifo").subscribe(
      txs => {
        if (txs) {
          this.fetchTxs();
        }
      }

      // pnls => {
      //   console.log(pnls)
      //   this.pnls = pnls;
      //   pnls.forEach(e => {
      //     const txPnl = this.txPnls.find(it => it.tx.id === e.transactionId);
      //     if (txPnl) {
      //       txPnl.pnl = e
      //     }
      //   });
      // }
    )
  }


}

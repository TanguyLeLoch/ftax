import {Component} from '@angular/core';
import {Pnl, TransactionSimplified, TransactionSimplifiedControllerService} from "../../../../core/model";

interface txNPnL {
  tx: TransactionSimplified,
  pnl: undefined | Pnl
}

@Component({
  selector: 'app-tx-list',
  templateUrl: './tx-list.component.html',
  styleUrl: './tx-list.component.scss'
})
export class TxListComponent {
  txs: TransactionSimplified[] = [];
  pnls: Pnl[] = [];

  txPnls: txNPnL[] = []


  constructor(private service: TransactionSimplifiedControllerService) {
    this.service.getAll().subscribe(txs => {
      txs.sort((a, b) => b.localDateTime.localeCompare(a.localDateTime));
      this.txs = txs
      this.txPnls = txs.map(tx => {
        return {tx, pnl: undefined}
      })
    });
  }

  newTx() {
    console.log(this.txs);
    const txSimplified = {} as TransactionSimplified;
    this.service.post(txSimplified).subscribe(tx => {
      this.txs = [tx, ...this.txs];
      this.txPnls = [{
        tx: tx,
        pnl: undefined
      }, ...this.txPnls]
    });
  }

  computePnL() {
    this.service.computePnl("fifo").subscribe(
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

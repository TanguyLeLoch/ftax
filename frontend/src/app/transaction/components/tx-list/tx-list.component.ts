import { Component, OnInit } from '@angular/core';
import { Transaction, TransactionControllerService } from "../../../core/model";
import { MatDialog } from "@angular/material/dialog";
import { TxImportComponent } from "../tx-import/tx-import.component";
import { TokenService } from "../../../core/services/token.service";


@Component({
  selector: 'app-tx-list',
  templateUrl: './tx-list.component.html',
  styleUrl: './tx-list.component.scss'
})
export class TxListComponent implements OnInit {
  txs: Transaction[] = [];
  isLoading = true;
  startTime = new Date().getTime();


  constructor(private service: TransactionControllerService,
              private dialog: MatDialog,
              private tokenService: TokenService,
  ) {

  }

  ngOnInit(): void {
    this.tokenService.fetchAllTokens().subscribe(
        () => {
          this.fetchTxs();
        }
    )
  }


  private fetchTxs() {
    console.log('fetchTxs', new Date().getTime() - this.startTime);
    this.service.getAll().subscribe(txs => {
      txs.sort((a, b) => b.localDateTime.localeCompare(a.localDateTime));
      this.txs = txs
      this.isLoading = false;
      console.log('fetchTxs', new Date().getTime() - this.startTime);
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
    let dialogRef = this.dialog.open(TxImportComponent, {
      height: '400px',
      width: '600px',
    });
  }


}

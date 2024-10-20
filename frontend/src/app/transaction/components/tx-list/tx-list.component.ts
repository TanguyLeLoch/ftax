// tx-list.component.ts

import { Component, OnDestroy, OnInit } from '@angular/core';
import { Transaction } from '../../../core/model';
import { MatDialog } from '@angular/material/dialog';
import { TxImportComponent } from '../tx-import/tx-import.component';
import { TokenService } from '../../../core/services/token.service';
import { TransactionService } from '../../../core/services/transaction.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-tx-list',
  templateUrl: './tx-list.component.html',
  styleUrls: ['./tx-list.component.scss'], // Note the correction from 'styleUrl' to 'styleUrls'
})
export class TxListComponent implements OnInit, OnDestroy {
  txs: Transaction[] = [];
  isLoading = true;
  startTime = new Date().getTime();
  private subscriptions = new Subscription();

  constructor(
    private transactionService: TransactionService, // Use TransactionService
    private dialog: MatDialog,
    private tokenService: TokenService
  ) {
  }

  ngOnInit(): void {
    // Fetch tokens and then transactions
    this.tokenService.fetchAllTokens().subscribe(() => {
      this.fetchTxs();
    });

    // Subscribe to the transactions observable
    const txSubscription = this.transactionService.transactions$.subscribe((txs) => {
      this.txs = txs.sort((a, b) => b.localDateTime.localeCompare(a.localDateTime));
    });
    this.subscriptions.add(txSubscription);
  }

  ngOnDestroy(): void {
    // Unsubscribe to prevent memory leaks
    this.subscriptions.unsubscribe();
  }

  private fetchTxs() {
    console.log('fetchTxs', new Date().getTime() - this.startTime);
    this.transactionService.fetchTransactions().subscribe(() => {
      this.isLoading = false;
      console.log('fetchTxs', new Date().getTime() - this.startTime);
    });
  }

  newTx() {
    console.log(this.txs);
    const tx = {} as Transaction;
    this.transactionService.addTransaction(tx).subscribe(
      (newTx) => {
        // The transactions$ observable will emit the new transaction
        // No need to manually update this.txs here
      },
      (error) => {
        console.error('Error adding new transaction:', error);
        // Handle error (e.g., show a notification)
      }
    );
  }

  importTx() {
    const dialogRef = this.dialog.open(TxImportComponent, {
      height: '400px',
      width: '600px',
    });

    // Optionally, handle dialog close event
    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        // Handle the result from the import dialog if needed
      }
    });
  }
}

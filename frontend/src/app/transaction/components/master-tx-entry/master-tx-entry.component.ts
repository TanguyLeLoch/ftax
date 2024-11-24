import { Component, Input, OnInit } from '@angular/core';
import { MasterTransaction } from 'src/app/core/frontModel/MasterTransaction';
import { faArrowDown, faArrowUp, faWarning } from '@fortawesome/free-solid-svg-icons';
import { Transaction } from "../../../core/model";
import { TokenService } from "../../../core/services/token.service";
import { TransactionService } from "../../../core/services/transaction.service";


export interface TxInfo {
  tokenId: string;
  amount: number;
  price?: number;
}

@Component({
  selector: 'app-master-tx-entry',
  templateUrl: './master-tx-entry.component.html',
  styleUrls: ['./master-tx-entry.component.scss'],
})
export class MasterTxEntryComponent implements OnInit {
  @Input() masterTransaction!: MasterTransaction;

  isExpanded: boolean = false;
  date!: Date;
  time!: string;
  hasBeenExpanded = false;
  protected formatter;
  sellTxs: Transaction[] = [];
  buyTxs: Transaction[] = [];
  pnl: number = 0

  summary: Map<string, number> = new Map<string, number>();
  outSummary: TxInfo[] = [];
  inSummary: TxInfo[] = [];
  feeSummary: TxInfo[] = [];


  constructor(private tokenService: TokenService,
              private transactionService: TransactionService) {
    this.formatter = new Intl.NumberFormat('en-US', {
      notation: 'compact',
      compactDisplay: 'short',
    });
  }

  ngOnInit(): void {
    this.sellTxs = this.masterTransaction.transactions.filter(tx => tx.type === 'SELL');
    this.buyTxs = this.masterTransaction.transactions.filter(tx => tx.type === 'BUY');
    this.pnl = this.masterTransaction.transactions
      .reduce((total, tx) => total + (tx.pnl?.value || 0), 0);

    for (let tx of this.masterTransaction.transactions) {
      const tokenId = tx.tokenId;
      if (!tokenId) continue;

      const amount = tx.amount || 0; // Default to 0 if amount is undefined
      const currentAmount = this.summary.get(tokenId) || 0;

      if (tx.type === 'SELL') {
        this.summary.set(tokenId, currentAmount - amount);
      } else if (tx.type === 'BUY') {
        this.summary.set(tokenId, currentAmount + amount);
      } else if (tx.type === 'FEE') {
        this.feeSummary.push({tokenId, amount, price: 0});
      }
    }
    console.log(this.summary)
    // fill in and out summary
    this.summary.forEach((amount, tokenId) => {
      const price = this.masterTransaction.transactions.find(t => t.tokenId === tokenId && t.type !== 'FEE')?.price

      if (amount > 0) {
        this.inSummary.push({tokenId, amount, price})
      } else {
        this.outSummary.push({tokenId, amount: Math.abs(amount), price});
      }
    });
  }

  onExpandedChange(expanded: boolean) {
    if (expanded) {
      this.hasBeenExpanded = true;
    }
  }

  protected readonly faArrowUp = faArrowUp;
  protected readonly faArrowDown = faArrowDown;
  protected readonly faWarning = faWarning;

  getType() {
    const types = new Set<string>();
    this.masterTransaction.transactions.forEach(tx => {
      types.add(tx.type);
    });

    // If there's only one type in the set
    if (types.size === 1) {
      if (types.has('FEE')) return 'Fee';
      if (types.has('SELL')) return 'Out';
      if (types.has('BUY')) return 'In';
    }
    if (types.size === 2) {
      if (types.has('SELL') && types.has('FEE')) return 'Out';
      if (types.has('BUY') && types.has('FEE')) return 'In';
    }

    return 'Swap';
  }

  isValid() {
    return this.masterTransaction.transactions.every(tx => tx.valid);
  }

  getDateTimeAtLocalFormat() {
    const date = new Date(this.masterTransaction.transactions[0].localDateTime);
    return date.toLocaleDateString() + ' ' + date.toLocaleTimeString();
  }

  onRefreshClick() {
    this.transactionService.refreshTransaction(this.masterTransaction.externalId).subscribe();
  }

}

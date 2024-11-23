import { Component, Input, OnInit } from '@angular/core';
import { MasterTransaction } from 'src/app/core/frontModel/MasterTransaction';
import { faArrowDown, faArrowUp, faWarning } from '@fortawesome/free-solid-svg-icons';
import { Transaction } from "../../../core/model";
import { TokenService } from "../../../core/services/token.service";
import { TransactionService } from "../../../core/services/transaction.service";

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

  summary: Map<string, number> = new Map<string, number>();


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

    for (let tx of this.masterTransaction.transactions) {
      const tokenId = tx.tokenId;
      if (!tokenId) continue;

      const amount = tx.amount || 0; // Default to 0 if amount is undefined
      const currentAmount = this.summary.get(tokenId) || 0;

      if (tx.type === 'SELL') {
        this.summary.set(tokenId, currentAmount - amount);
      } else if (tx.type === 'BUY') {
        this.summary.set(tokenId, currentAmount + amount);
      }
    }
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
    if (types.size === 1) {
      return types.values().next().value;
    } else {
      return 'SWAP';
    }
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

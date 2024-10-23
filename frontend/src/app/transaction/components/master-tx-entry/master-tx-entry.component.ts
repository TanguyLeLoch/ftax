import { Component, Input, OnInit } from '@angular/core';
import { MasterTransaction } from 'src/app/core/frontModel/MasterTransaction';
import { faArrowDown, faArrowUp, faWarning } from '@fortawesome/free-solid-svg-icons';
import { Token, Transaction } from "../../../core/model";
import { TokenService } from "../../../core/services/token.service";

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

  constructor(private tokenService: TokenService) {
    this.formatter = new Intl.NumberFormat('en-US', {
      notation: 'compact',
      compactDisplay: 'short',
    });
  }

  ngOnInit(): void {
    this.sellTxs = this.masterTransaction.transactions.filter(tx => tx.type === 'SELL');
    this.buyTxs = this.masterTransaction.transactions.filter(tx => tx.type === 'BUY');
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

  getToken(id: string | undefined): Token | undefined {
    if (!id) {
      return undefined;
    }
    return this.tokenService.getToken(id);
  }
}

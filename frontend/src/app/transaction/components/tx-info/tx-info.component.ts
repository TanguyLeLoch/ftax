// tx-info.component.ts
import { Component, Input, OnInit } from '@angular/core';
import { Token } from "../../../core/model";
import { TokenService } from "../../../core/services/token.service";
import { environment } from "../../../../environments/environment";
import { TxInfo } from '../master-tx-entry/master-tx-entry.component';

export type TxSize = 'small' | 'normal' | 'large';

@Component({
  selector: 'app-tx-info',
  templateUrl: './tx-info.component.html',
  styleUrls: ['./tx-info.component.scss']
})
export class TransactionInfoComponent implements OnInit {
  @Input() txInfo!: TxInfo;
  @Input() size: TxSize = 'normal'; // default size

  amount!: number;
  price: number | undefined;
  tokenId!: string;

  formatter;
  private basePath = environment.basePath;

  constructor(private tokenService: TokenService) {
    this.formatter = new Intl.NumberFormat('en-US', {
      notation: 'compact',
      compactDisplay: 'short',
    });
  }

  ngOnInit(): void {
    this.amount = this.txInfo.amount;
    this.price = this.txInfo.price;
    this.tokenId = this.txInfo.tokenId;
  }

  getToken(id: string | undefined): Token | undefined {
    if (!id) {
      return undefined;
    }
    return this.tokenService.getToken(id);
  }

  getTokenUrl() {
    const token = this.getToken(this.tokenId);
    let url: string;
    if (token && token.logoUrl) {
      url = this.basePath + '/img/' + token.logoUrl;
    } else {
      url = 'assets/unknown-logo.png';
    }
    return url;
  }
}
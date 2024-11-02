import { Component, Input } from '@angular/core';
import { Token, Transaction } from "../../../core/model";
import { TokenService } from "../../../core/services/token.service";
import { environment } from "../../../../environments/environment";


@Component({
  selector: 'app-tx-info',
  templateUrl: './tx-info.component.html',
  styleUrls: ['./tx-info.component.scss']
})
export class TransactionInfoComponent {
  @Input() tx!: Transaction; // Input property to pass transaction data to the component
  formatter ;

  private basePath = environment.basePath;


  constructor( private tokenService: TokenService ) {
    this.formatter = new Intl.NumberFormat('en-US', {
      notation: 'compact',
      compactDisplay: 'short',
    });
  }
  getToken(id: string | undefined): Token | undefined {
    if (!id) {
      return undefined;
    }
    return this.tokenService.getToken(id);
  }

  getTokenUrl() {
    const token = this.getToken(this.tx.tokenId);
    let url: string;
    if (token && token.logoUrl) {
      url = this.basePath + '/img/' + token.logoUrl;
    } else {
      url = 'assets/unknown-logo.png';
    }
    return url;
  }
}

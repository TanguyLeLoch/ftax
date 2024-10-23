import { Component, Input } from '@angular/core';
import { MasterTransaction } from 'src/app/core/frontModel/MasterTransaction';

@Component({
  selector: 'app-master-tx-entry',
  templateUrl: './master-tx-entry.component.html',
  styleUrls: ['./master-tx-entry.component.scss'],
})
export class MasterTxEntryComponent {
  @Input() masterTransaction!: MasterTransaction;
}

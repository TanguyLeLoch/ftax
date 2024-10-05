import { Component } from '@angular/core';
import { FormBuilder, FormGroup } from "@angular/forms";
import { TransactionControllerService } from "../../../core/model";

@Component({
  selector: 'app-tx-import',
  templateUrl: './tx-import.component.html',
  styleUrls: ['./tx-import.component.scss']
})
export class TxImportComponent {

  importForm: FormGroup;
  showCards: boolean = true;

  constructor(private fb: FormBuilder, private transactionService: TransactionControllerService) {
    this.importForm = this.fb.group({
      importType: [''],
      csvFile: [null],
      ethAddress: [''],
      baseChainAddress: ['']
    });
  }

  onFileSelected(event: any): void {
    const file: File = event.target.files[0];
    if (file) {
      this.importForm.patchValue({ csvFile: file });
    }
  }

  importData(): void {
    const { importType, csvFile, ethAddress, baseChainAddress } = this.importForm.value;
    if (importType === 'mexc' && csvFile) {
      this.transactionService.importTransactions('MEXC', csvFile).subscribe((response) => {
        console.log('MEXC import successful:', response);
      });
    } else if (importType === 'Ethereum' && ethAddress) {
      console.log('Ethereum address:', ethAddress);
    } else if (importType === 'Base chain' && baseChainAddress) {
      console.log('Base chain address:', baseChainAddress);
    }
  }

  resetSelection(): void {
    this.importForm.reset();
    this.showCards = true;
  }

  closeDialog() {

  }

  selectImportType(type: string) {
    this.importForm.patchValue({ importType: type });
    this.showCards = false;
  }
}

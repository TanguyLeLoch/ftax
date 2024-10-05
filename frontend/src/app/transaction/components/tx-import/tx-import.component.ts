import { Component } from '@angular/core';
import { FormBuilder, FormGroup } from "@angular/forms";
import { TransactionControllerService } from "../../../core/model";
import { MatDialogRef } from "@angular/material/dialog";

@Component({
  selector: 'app-tx-import',
  templateUrl: './tx-import.component.html',
  styleUrls: ['./tx-import.component.scss']
})
export class TxImportComponent {


  importForm: FormGroup;
  showCards: boolean = true;

  constructor(
    public dialogRef: MatDialogRef<TxImportComponent>,
    private fb: FormBuilder, private transactionService: TransactionControllerService) {
    this.importForm = this.fb.group({
      importType: [''],
      file: [null],
      ethAddress: [''],
      baseChainAddress: ['']
    });
  }

  onFileSelected(file: File | undefined) {
    this.importForm.patchValue({file: file});
    console.log(file)
  }

  importData(): void {
    const {importType, file, ethAddress, baseChainAddress} = this.importForm.value;
    if (importType === 'mexc' && file) {
      console.log('MEXC import file:', file);
      this.transactionService.importTransactions('Mexc', file).subscribe((response) => {
        console.log('MEXC import successful:', response);
      });
    } else if (importType === 'Ethereum' && ethAddress) {
      console.log('Ethereum address:', ethAddress);
    } else if (importType === 'Base chain' && baseChainAddress) {
      console.log('Base chain address:', baseChainAddress);
    }
    this.closeDialog();
  }

  resetSelection(): void {
    this.importForm.reset();
    this.showCards = true;
  }

  closeDialog() {
    this.dialogRef.close();
  }

  selectImportType(type: string) {
    this.importForm.patchValue({ importType: type });
    this.showCards = false;
  }
}

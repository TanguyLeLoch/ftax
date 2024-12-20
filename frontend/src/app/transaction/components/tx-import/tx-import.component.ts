import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";
import {TransactionControllerService} from "../../../core/model";
import {MatDialogRef} from "@angular/material/dialog";

@Component({
  selector: 'app-tx-import',
  templateUrl: './tx-import.component.html',
  styleUrls: ['./tx-import.component.scss']
})
export class TxImportComponent implements OnInit {

  importForm: FormGroup;
  showCards: boolean = true;

  constructor(
    public dialogRef: MatDialogRef<TxImportComponent>,
    private fb: FormBuilder, private transactionService: TransactionControllerService) {
    this.importForm = this.fb.group({
      importType: [''],
      file: [null],
      address: [''],
      start: new FormControl<Date | null>(null),
      end: new FormControl<Date | null>(null),
    });
  }

  ngOnInit() {
    this.importForm.get('importType')?.valueChanges.subscribe(value => {
      this.updateValidators(value);
    });
  }

  updateValidators(importType: string) {
    const fileControl = this.importForm.get('file');
    const ethAddressControl = this.importForm.get('ethAddress');
    const baseChainAddressControl = this.importForm.get('baseChainAddress');

    if (importType === 'mexc') {
      fileControl?.setValidators([Validators.required]);
    } else {
      fileControl?.clearValidators();
    }

    if (importType === 'Ethereum') {
      ethAddressControl?.setValidators([Validators.required]);
    } else {
      ethAddressControl?.clearValidators();
    }

    if (importType === 'Base chain') {
      baseChainAddressControl?.setValidators([Validators.required]);
    } else {
      baseChainAddressControl?.clearValidators();
    }

    fileControl?.updateValueAndValidity();
    ethAddressControl?.updateValueAndValidity();
    baseChainAddressControl?.updateValueAndValidity();
  }

  onFileSelected(file: File | null) {
    this.importForm.patchValue({file: file});
    this.importForm.get('file')?.updateValueAndValidity();
  }


  importData(): void {
    const {importType, file, address, start, end} = this.importForm.value;
    if (importType === 'mexc' && file) {
      console.log('MEXC import file:', file);
      this.transactionService.importTransactions('Mexc', file).subscribe((response) => {
        console.log('MEXC import successful:', response);
      });
    } else if (importType === 'Ethereum' && address) {

      this.transactionService.importOnchainTransactions('Ethereum', address, start, end).subscribe((response) => {
        console.log('Ethereum address:', address);
      });
    } else if (importType === 'Base chain' && address) {
      console.log('Base chain address:', address);
    }
    // this.closeDialog(); uncomment to close dialog
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

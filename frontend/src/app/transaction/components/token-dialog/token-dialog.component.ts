import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Token } from "../../../core/model";

@Component({
  selector: 'app-token-dialog',
  templateUrl: './token-dialog.component.html',
  styleUrls: ['./token-dialog.component.scss']
})
export class TokenDialogComponent implements OnInit {

  tokenForm!: FormGroup;

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<TokenDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: Token
  ) {}

  ngOnInit(): void {
    this.tokenForm = this.fb.group({
      name: [this.data.name, Validators.required],
      ticker: [this.data.ticker, Validators.required]
    });
  }

  onSubmitToken() {
    if (this.tokenForm.valid) {
      this.dialogRef.close(this.tokenForm.value);
    }
  }

  closeDialog() {
    this.dialogRef.close();
  }
}

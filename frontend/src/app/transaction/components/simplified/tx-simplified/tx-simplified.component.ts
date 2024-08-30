import {Component, Input, OnInit} from '@angular/core';
import {TransactionSimplified, TransactionSimplifiedControllerService} from "../../../../core/model";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {faCheck, faEdit, faTrash, faWarning} from "@fortawesome/free-solid-svg-icons";

@Component({
  selector: 'app-tx-simplified',
  templateUrl: './tx-simplified.component.html',
  styleUrl: './tx-simplified.component.scss'
})
export class TxSimplifiedComponent implements OnInit {
  @Input() transaction!: TransactionSimplified;

  txForm!: FormGroup;
  date!: string;
  time!: string;

  isValid = false;
  isCollapsed: boolean = true;

  open(): void {
    this.isCollapsed = false
  }


  constructor(private service: TransactionSimplifiedControllerService, private fb: FormBuilder) {


  }

  ngOnInit(): void {

    this.date = this.transaction.localDateTime.slice(0, 10)
    this.time = this.transaction.localDateTime.slice(11, 23);
    this.isValid = this.transaction.valid;

    this.txForm = this.fb.group({
      date: [this.date, Validators.required,],
      time: [this.time, Validators.required,],
      type: [this.transaction.type, Validators.required,],
      amount: [this.transaction.amount, Validators.required,],
      token: [this.transaction.token, Validators.required,],
      dollarValue: [this.transaction.dollarValue, Validators.required,],
    });

  }

  onSubmit(): void {
    // yyyy-MM-dd HH:mm:ss.SSS without timezone
    this.transaction.localDateTime = this.date + ' ' + this.time;
    this.transaction.type = this.txForm.get('type')!.value;
    this.transaction.amount = this.txForm.get('amount')!.value;
    this.transaction.token = this.txForm.get('token')!.value;
    this.transaction.dollarValue = this.txForm.get('dollarValue')!.value;
    this.service.post(this.transaction).subscribe(tx => {
      this.transaction = tx
      this.isCollapsed = true;
    });
  }

  protected readonly faTrash = faTrash;
  protected readonly faEdit = faEdit;
  protected readonly faCheck = faCheck;
  protected readonly faWarning = faWarning;
}

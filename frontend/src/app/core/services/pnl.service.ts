import { Injectable } from '@angular/core';
import { TransactionControllerService } from "../model";
import { ProfileService } from "./profile.service";

@Injectable({
  providedIn: 'root'
})
export class PnlService {



  constructor(
    private transactionService: TransactionControllerService,
    private profileService: ProfileService
  ) {}

  computePnl() {
    const calculationMethod = this.profileService.getCalculationMethod();
    return this.transactionService.computePnl(calculationMethod!);
  }
}

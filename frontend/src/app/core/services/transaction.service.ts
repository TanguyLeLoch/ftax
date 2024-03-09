import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {TransactionControllerService} from "../model";

@Injectable({
  providedIn: 'root'
})
export class TransactionService {

  constructor(private http: HttpClient, private transactionControllerService: TransactionControllerService) {

  }


  createTransactions() {

    return this.transactionControllerService.createDraftTransaction();
  }


}

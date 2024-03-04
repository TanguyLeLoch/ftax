import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {TransactionControllerService} from "../model";

@Injectable({
  providedIn: 'root'
})
export class TransactionService {
  // private transactionControllerApi: TransactionControllerApi;

  constructor(private http: HttpClient, private transactionControllerService: TransactionControllerService) {

  }

  // transactionControllerApi: TransactionControllerApi) {
  //   const configuration = new Configuration();
  //   configuration.basePath = 'http://localhost:8081';
  //   this.transactionControllerApi = new TransactionControllerApi(configuration);
  // }

  createTransactions() {
    // return this.http.post('http://localhost:8081/transaction/draft', {});
    return this.transactionControllerService.createDraftTransaction();
  }

}

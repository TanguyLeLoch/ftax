import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class TransactionService {

  constructor(private http: HttpClient) {
  }

  createTransactions() {
    return this.http.post('http://localhost:8081/transaction/draft', {});
  }

}

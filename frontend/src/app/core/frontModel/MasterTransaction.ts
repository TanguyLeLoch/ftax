import { Transaction } from "../model";

export class MasterTransaction {
  public externalId: string;
  public transactions: Transaction[];

  constructor(transactions: Transaction[]) {
    this.transactions = transactions;
    this.externalId = transactions[0].externalId!;
  }

}

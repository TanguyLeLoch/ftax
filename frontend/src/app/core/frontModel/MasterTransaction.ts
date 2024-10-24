import { Transaction } from "../model";

export class MasterTransaction {
  public externalId: string;
  public platform: string;
  public transactions: Transaction[];

  constructor(transactions: Transaction[]) {
    this.transactions = transactions;
    this.externalId = transactions[0].externalId!;
    this.platform = transactions[0].platform;
  }

}

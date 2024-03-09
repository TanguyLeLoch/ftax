/**
 * OpenAPI definition
 * No description provided (generated by Openapi Generator https://github.com/openapitools/openapi-generator)
 *
 * The version of the OpenAPI document: v0
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


export interface Transaction { 
    id: string;
    transactionType: Transaction.TransactionTypeEnum;
    date: string;
    token1: string;
    token2: string;
    tokenFee: string;
    amount1: number;
    amount2: number;
    amountFee: number;
    externalId?: string;
}
export namespace Transaction {
    export type TransactionTypeEnum = 'TRANSFER' | 'SWAP' | 'NONE';
    export const TransactionTypeEnum = {
        Transfer: 'TRANSFER' as TransactionTypeEnum,
        Swap: 'SWAP' as TransactionTypeEnum,
        None: 'NONE' as TransactionTypeEnum
    };
}



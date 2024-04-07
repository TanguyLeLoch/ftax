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
    state: Transaction.StateEnum;
    transactionType: Transaction.TransactionTypeEnum;
    date?: string;
    tokenIn?: string;
    tokenOut?: string;
    tokenFee?: string;
    amountIn: number;
    amountOut: number;
    amountFee: number;
    externalId?: string;
}
export namespace Transaction {
    export type StateEnum = 'DRAFT' | 'SUBMITTED';
    export const StateEnum = {
        Draft: 'DRAFT' as StateEnum,
        Submitted: 'SUBMITTED' as StateEnum
    };
    export type TransactionTypeEnum = 'TRANSFER' | 'SWAP' | 'NONE';
    export const TransactionTypeEnum = {
        Transfer: 'TRANSFER' as TransactionTypeEnum,
        Swap: 'SWAP' as TransactionTypeEnum,
        None: 'NONE' as TransactionTypeEnum
    };
}



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


/**
 * Transaction object
 */
export interface Transaction { 
    id: string;
    /**
     * The state of the transaction
     */
    state: Transaction.StateEnum;
    amountIn?: number;
    amountOut?: number;
    amountFee?: number;
    externalId?: string;
    tokenIn?: string;
    tokenOut?: string;
    tokenFee?: string;
    dateTime?: string;
    /**
     * The type of the transaction
     */
    transactionType: Transaction.TransactionTypeEnum;
}
export namespace Transaction {
    export type StateEnum = 'draft' | 'submitted';
    export const StateEnum = {
        Draft: 'draft' as StateEnum,
        Submitted: 'submitted' as StateEnum
    };
    export type TransactionTypeEnum = 'transfer' | 'swap' | 'none';
    export const TransactionTypeEnum = {
        Transfer: 'transfer' as TransactionTypeEnum,
        Swap: 'swap' as TransactionTypeEnum,
        None: 'none' as TransactionTypeEnum
    };
}


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


export interface TransactionRequest { 
    id?: string;
    localDateTime?: string;
    type?: TransactionRequest.TypeEnum;
    amount?: number;
    tokenId?: string;
    price?: number;
    externalId?: string;
    platform?: string;
    address?: string;
}
export namespace TransactionRequest {
    export type TypeEnum = 'BUY' | 'SELL' | 'FEE';
    export const TypeEnum = {
        Buy: 'BUY' as TypeEnum,
        Sell: 'SELL' as TypeEnum,
        Fee: 'FEE' as TypeEnum
    };
}



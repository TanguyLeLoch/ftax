export * from './authController.service';
import { AuthControllerService } from './authController.service';
export * from './tokenController.service';
import { TokenControllerService } from './tokenController.service';
export * from './transactionSimplifiedController.service';
import { TransactionSimplifiedControllerService } from './transactionSimplifiedController.service';
export const APIS = [AuthControllerService, TokenControllerService, TransactionSimplifiedControllerService];

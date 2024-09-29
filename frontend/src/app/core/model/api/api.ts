export * from './authController.service';
import { AuthControllerService } from './authController.service';
export * from './clientController.service';
import { ClientControllerService } from './clientController.service';
export * from './tokenController.service';
import { TokenControllerService } from './tokenController.service';
export * from './transactionController.service';
import { TransactionControllerService } from './transactionController.service';
export const APIS = [AuthControllerService, ClientControllerService, TokenControllerService, TransactionControllerService];

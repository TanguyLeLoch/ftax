import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

export interface ToastMessage {
  type: 'success' | 'error' | 'info' | 'warning';
  message: string;
}

@Injectable({
  providedIn: 'root'
})
export class ToastService {
  private toastSubject = new Subject<ToastMessage>();
  toastState = this.toastSubject.asObservable();

  showToast(type: 'success' | 'error' | 'info' | 'warning', message: string) {
    this.toastSubject.next({ type, message });
  }

  showSuccess(message: string) {
    this.showToast('success', message);
  }
}

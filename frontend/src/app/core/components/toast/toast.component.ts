import {Component, OnInit} from '@angular/core';
import {ToastMessage, ToastService} from "../../services/toast.service";


@Component({
  selector: 'app-toast',
  templateUrl: './toast.component.html',
  styleUrl: './toast.component.scss'
})
export class ToastComponent implements OnInit {
  toastMessages: ToastMessage[] = [];

  constructor(private toastService: ToastService) {}

  ngOnInit(): void {
    this.toastService.toastState.subscribe((toast: ToastMessage) => {
      this.toastMessages.push(toast);
      setTimeout(() => this.removeToast(toast), 3000);
    });
  }

  removeToast(toast: ToastMessage) {
    this.toastMessages = this.toastMessages.filter(t => t !== toast);
  }
}

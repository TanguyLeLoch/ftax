import {Component, OnInit} from '@angular/core';
import {NavigationEnd, Router} from "@angular/router";
import {PnlService} from "../../services/pnl.service";

@Component({
  selector: 'app-sidenav',
  templateUrl: './sidenav.component.html',
  styleUrl: './sidenav.component.scss'
})
export class SidenavComponent implements OnInit {
  events: string[] = [];
  opened: boolean = true;
  isLoginPage = false;

  constructor(private router: Router, private pnlService: PnlService) {
  }

  ngOnInit(): void {
    this.router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        // Check if the current route is the login route
        this.isLoginPage = event.url === '/login';
      }
    });
  }

  openProfile() {
    this.router.navigate(['/profile']);
  }

  computePnL() {
    this.pnlService.computePnl().subscribe()
  }

  goToTransactions() {
    this.router.navigate(['/transactions']);
  }
}

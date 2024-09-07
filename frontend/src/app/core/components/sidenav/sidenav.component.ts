import {Component} from '@angular/core';

@Component({
  selector: 'app-sidenav',
  templateUrl: './sidenav.component.html',
  styleUrl: './sidenav.component.scss'
})
export class SidenavComponent {
  events: string[] = [];
  opened: boolean = true;

  openProfile() {
    console.log('openProfile');
  }
}

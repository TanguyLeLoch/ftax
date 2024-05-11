import {Component} from '@angular/core';

@Component({
  selector: 'app-platform-list',
  templateUrl: './platform-list.component.html',
  styleUrls: ['./platform-list.component.scss']
})
export class PlatformListComponent {
  platforms = [
    {
      name: 'Binance',
      logo: "/assets/Binance-logo.png",
    },
  ];

}

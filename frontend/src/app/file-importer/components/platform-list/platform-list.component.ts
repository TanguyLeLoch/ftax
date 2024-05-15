import {Component, OnInit} from '@angular/core';

@Component({
  selector: 'app-platform-list',
  templateUrl: './platform-list.component.html',
  styleUrls: ['./platform-list.component.scss']
})
export class PlatformListComponent implements OnInit {
  platforms: Platform[] = [
    {
      name: 'Binance',
      logo: "/assets/Binance-logo.png",
    },
  ];

  selectedPlatform: Platform | null = null

  openImportComponent(platform: Platform) {
    this.selectedPlatform = platform;
  }

  ngOnInit(): void {
    this.selectedPlatform = null;
  }

}

export interface Platform {
  name: string;
  logo: string;
}

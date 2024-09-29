import { Component, OnInit } from '@angular/core';
import { ProfileService } from '../../services/profile.service';
import { Profile } from "../../model";

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.scss'
})
export class ProfileComponent implements OnInit {

  profileName: string = '';
  tooltipText: string = 'FIFO: First In, First Out computes PnL based on the order of transactions.' +
    ' Average: Computes PnL based on the average cost of all transactions.';
  calculationMethod!: Profile.CalculationMethodEnum;

  constructor(private profileService: ProfileService) {}

  ngOnInit(): void {
    this.profileService.client$.subscribe(client => {
      if (client) {
        this.profileName = client.username!;
        this.calculationMethod = client.profile!.calculationMethod;
      }
    });
  }

  onMethodChange(value: Profile.CalculationMethodEnum): void {
    this.profileService.updateCalculationMethod(value);
  }

  onUsernameBlur(): void {
    this.profileService.updateUsername(this.profileName);
  }
}

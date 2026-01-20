import { Component } from '@angular/core';
import { Observable } from 'rxjs';
import { AdPostUtilService } from '../ad-post-util.service';
import { AuthStateService } from '../../services/auth-state.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-ad-list',
  templateUrl: './ad-list.component.html',
  styleUrl: './ad-list.component.css',
})
export class AdListComponent {
  title: string = 'am New';
  constructor(private router: Router,
    private adPostUtilService: AdPostUtilService,
    private authStateService: AuthStateService,
  ) {}

  adList: any;
  selectedItem: any;
  userName: string = '';
  isadClicked: boolean = false;
  ngOnInit() {
    this.authStateService.userName$.subscribe((userName) => {
      this.userName = userName;
    });
    this.adPostUtilService.getAdList().subscribe((data: any) => {
      this.adList = data;
    });
  }
  viewAd(newValue: number) {
    // this.adPostUtilService.triggerClick(newValue);
    // this.isadClicked = true;
    this.router.navigate(['/ad-details'], { 
  queryParams: { adId: newValue },
  
});
  }
}

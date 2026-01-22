import { Component } from '@angular/core';
import { CustomerService } from '../../services/customer-service';
import { AuthStateService } from '../../services/auth-state.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-my-ads.component',
  standalone: false,
  templateUrl: './my-ads.component.html',
  styleUrl: './my-ads.component.css'
})
export class MyAdsComponent {

  myAdsList: any;
  currentPage = 1;
  totalPages = 10;
  totalItems = 100;
  itemsPerPage = 10;
  userName: string = '';



  constructor(private customerService: CustomerService, 
    private authStateService: AuthStateService, 
    private router: Router  ) { }

  ngAfterViewInit() {
    this.authStateService.userName$.subscribe((userName) => {
      this.userName = userName;
    });
    this.getMyAds(this.currentPage);
  }

  getMyAds(page: number): void {  
     this.customerService.getMyAdList(page, this.itemsPerPage).subscribe((data: any) => {
      this.myAdsList = data.adPosts;
      this.currentPage = data.currentPage + 1;
      this.totalPages = data.totalPages;
    });
  }

  viewAd(newValue: number) {
    // this.adPostUtilService.triggerClick(newValue);
    // this.isadClicked = true;
    if (!this.authStateService.getLoggedInStatus()) {
      this.router.navigate(['/loginV3']);
    }
    this.router.navigate(['/ad-details'], {
      queryParams: { adId: newValue },
    });
  }
  

}

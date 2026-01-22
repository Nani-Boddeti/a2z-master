import { Component } from '@angular/core';
import { Observable } from 'rxjs';
import { AdPostUtilService } from '../ad-post-util.service';
import { AuthStateService } from '../../services/auth-state.service';
import { Router } from '@angular/router';
import { AdSearch } from '../../services/ad-search';
import { LocationService } from '../../services/location-service';

@Component({
  selector: 'app-ad-list',
  templateUrl: './ad-list.component.html',
  styleUrl: './ad-list.component.css',
})
export class AdListComponent {
  title: string = 'am New';
  constructor(
    private router: Router,
    private adPostUtilService: AdPostUtilService,
    private authStateService: AuthStateService,
    private adSearch: AdSearch,
    private locationService: LocationService,
  ) {}
  latitude: number = 0;
  longitude: number = 0;

  adList: any;
  selectedItem: any;
  userName: string = '';
  isadClicked: boolean = false;
  currentPage = 1;
  totalPages = 10;
  totalItems = 100;
  itemsPerPage = 10;
  selectedCategory: string = 'ALL';
  categories: any[] = [];
  onPageChange(event: Event) {
    //this.currentPage = event;
    // Fetch new data based on page
    //this.loadPageData(page);
    console.log('Page changed:', event);
  }

  ngOnInit() {
    this.authStateService.userName$.subscribe((userName) => {
      this.userName = userName;
    });
    this.loadCategories();
    this.adSearch
      .getAdList(this.currentPage, this.itemsPerPage)
      .subscribe((data: any) => {
         data.adPosts.forEach((ad: any) => {
        if(this.userName && ad.customer.userName === this.userName) {
          ad.isSelfAd = true;
        } else {
          ad.isSelfAd = false;    
      }});
        this.adList = data.adPosts;
        this.currentPage = data.currentPage + 1;
        this.totalPages = data.totalPages;
      });
    this.locationService.getLocationFromBrowser().subscribe({
      next: (coords) => {
        this.latitude = coords.latitude;
        this.longitude = coords.longitude;
        console.log(`Lat: ${coords.latitude}, Lng: ${coords.longitude}`);
      },
      error: (error) => {
        console.error('Location error:', error);
      },
    });
  }
  loadCategories() {
    this.adSearch.getListedCategories().subscribe((data: any) => {
      this.categories = data;
    });
  }

  loadItems(page: number) {
    this.adSearch.getAdList(page, this.itemsPerPage).subscribe((data: any) => {
      data.adPosts.forEach((ad: any) => {
        if(this.userName && ad.customer.userName === this.userName) {
          ad.isSelfAd = true;
        } else {
          ad.isSelfAd = false;    
      }});
      this.adList = data.adPosts;
      this.currentPage = data.currentPage + 1;
      this.totalPages = data.totalPages;
    });
  }
  
  onCategoryChange(categoryCode: string) {
    this.selectedCategory = categoryCode;
    this.currentPage = 1; // Reset to page 1
    this.adSearch
      .searchAdListWithCategoryCode(
        categoryCode,
        this.currentPage,
        this.itemsPerPage,
      )
      .subscribe((data: any) => {
        this.adList = data.adPosts;
        this.currentPage = data.currentPage;
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

  changePageSize(newSize: number) {
    this.itemsPerPage = newSize;
    this.loadItems(1); // Reload items with new page size, starting from page 1
  }
}

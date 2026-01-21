import { Component, Input } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable, Subject, takeUntil } from 'rxjs';
import { AdPostUtilService } from '../ad-post-util.service';
import { AuthStateService } from '../../services/auth-state.service';
import { AdSearch } from '../../services/ad-search';

@Component({
  selector: 'app-search-results',
  standalone: false,
  templateUrl: './search-results.html',
  styleUrl: './search-results.css'
})
export class SearchResults {
constructor(
    private router: Router,
    private adPostUtilService: AdPostUtilService,
    private authStateService: AuthStateService,
    private adSearch: AdSearch,
    private route: ActivatedRoute
  ) {}

  searchQuery: string = '';
  private destroy$ = new Subject<void>();

  adList: any;
  selectedItem: any;
  userName: string = '';
  isadClicked: boolean = false;
  currentPage = 1;
    totalPages = 10;
    totalItems = 100;
    itemsPerPage = 1;

    onPageChange(event: Event) {
        //this.currentPage = event;
        // Fetch new data based on page
        //this.loadPageData(page);
        console.log('Page changed:', event);
    }

    loadPageData(page: number) {
        // API call logic here
    }
  ngOnInit() {
    this.authStateService.userName$.subscribe((userName) => {
      this.userName = userName;
    });

    this.route.queryParams
      .pipe(takeUntil(this.destroy$))
      .subscribe(params => {
        this.searchQuery = params['q'] || '';
        if (this.searchQuery) {
          this.adSearch.searchAdList(this.searchQuery,this.currentPage, this.itemsPerPage).subscribe((data: any) => {

      this.adList = data.adPosts;
      this.currentPage = data.currentPage;
      this.totalPages = data.totalPages;

    });
        }
      });
      
    
  }

  loadItems(page: number) {
    this.adSearch.getAdList(page, this.itemsPerPage).subscribe((data: any) => {

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

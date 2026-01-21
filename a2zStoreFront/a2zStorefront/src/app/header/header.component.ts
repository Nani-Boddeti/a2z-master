import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { RegistrationService } from '../login-register/registration.service';
import { AuthStateService } from '../services/auth-state.service';
import { filter } from 'rxjs/operators';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { AdService } from '../services/ad.service';
import { OrderServiceService } from '../services/order-service.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})
export class HeaderComponent implements OnInit, OnDestroy {
  userMenuOpen = false;
  isLoggedIn = false;
  userName: string = '';
  private destroy$ = new Subject<void>();
 isScrolled = false;
  currentRoute = '';
  constructor(
    private router: Router,
    private registrationService: RegistrationService,
    private authStateService: AuthStateService,
    private adService: AdService,
    private orderService: OrderServiceService
  ) {}

  ngOnInit(): void {
    window.addEventListener('scroll', () => {
      this.isScrolled = window.scrollY > 10;
    });
    // Subscribe to login status from auth service
    this.authStateService.loggedIn$
      .pipe(takeUntil(this.destroy$))
      .subscribe(loggedIn => {
        this.isLoggedIn = loggedIn;
        console.log('Header: isLoggedIn updated to', this.isLoggedIn);
      });

    // Subscribe to user name
    this.authStateService.userName$
      .pipe(takeUntil(this.destroy$))
      .subscribe(userName => {
        this.userName = userName;
      });

    // Re-check login status on navigation
    this.router.events
      .pipe(
        filter(event => event instanceof NavigationEnd),
        takeUntil(this.destroy$)
      )
      .subscribe(() => {
        this.authStateService.checkAndUpdateLoginStatus();
      });

    // Initial check
    this.authStateService.checkAndUpdateLoginStatus();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  toggleUserMenu(): void {
    this.userMenuOpen = !this.userMenuOpen;
  }

  closeMenu(): void {
    this.userMenuOpen = false;
  }

  navigateTo(route: string): void {
    this.router.navigate([route]);
    this.closeMenu();
  }

  goToProfile(): void {
    this.navigateTo('/profile');
  }

  goToSubmitAd(): void {
    this.navigateTo('/ad-posts');
  }

  goToApprovalRequests(): void {
    this.navigateTo('/approvals');
  }

  goToOrders(): void {
    this.navigateTo('/my-orders');
  }
  goToLogin(): void {
    this.navigateTo('/loginV3');
  }
onSearch(event: any) {
    const query = event.target?.value || '';
    // Navigate to search results
    this.router.navigate(['/search'], { queryParams: { q: query } });
  }

  logout(): void {
    this.registrationService.logout().subscribe({
      next: () => {
        sessionStorage.removeItem('access_token');
        localStorage.removeItem('access_token');
        sessionStorage.removeItem('userInfo');
        this.authStateService.setLoggedIn(false);
        this.closeMenu();
        this.router.navigate(['/']);
      },
      error: (error) => {
        console.error('Logout failed:', error);
        sessionStorage.removeItem('access_token');
        localStorage.removeItem('access_token');
        this.authStateService.setLoggedIn(false);
        this.router.navigate(['/']);
      }
    });
  }
}

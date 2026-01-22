import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HttpErrorResponse
} from '@angular/common/http';
import { Observable, throwError, BehaviorSubject, EMPTY } from 'rxjs';
import { catchError, filter, take, switchMap, tap } from 'rxjs/operators';
import { AuthStateService } from './services/auth-state.service';
import { OauthTokenService } from './services/oauth-token.service';
import { Router } from '@angular/router';
import { RequestMatcherService } from './services/request-matcher.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(
    private router: Router,
    private authStateService: AuthStateService,
    private oauthTokenService: OauthTokenService,
    private requestMatcherService: RequestMatcherService
  ) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    // Do not intercept token refresh requests

    if (this.requestMatcherService.matchRequest(request.url)) {
      return next.handle(request);
    }
    // Check if token is expired and refresh if needed
    if (this.authStateService.isTokenExpired()) {
  return this.oauthTokenService.refreshAccessToken().pipe(
    switchMap(() => {
      return this.addTokenAndForwardRequest(request, next);
    }),
    // ✅ Use tap() instead of catchError()
    tap({
      error: (error) => {
        // Side effects ONLY - no return needed
        this.authStateService.setLoggedIn(false);
        this.authStateService.removeTokens();
        this.authStateService.removeUserInfo();
        this.router.navigate(['/']);
      }
    })
  );
}
  //  this.addTokenAndForwardRequest(request, next).subscribe({
  //   error: (error : HttpErrorResponse) => {
  //     if(error.status === 401){
  //       this.authStateService.setLoggedIn(false);
  //     this.authStateService.removeTokens();
  //     this.authStateService.removeUserInfo();
  //     this.router.navigate(['/loginV3']);
  //     }
      
  //   }
  // });
  return this.addTokenAndForwardRequest(request, next).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401) {
        this.authStateService.setLoggedIn(false);
        this.authStateService.removeTokens();
        this.authStateService.removeUserInfo();
        this.router.navigate(['/loginV3']);
      }
      throw error;  // Re-throw to maintain chain
    })
  );
  }

  private addTokenAndForwardRequest(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    const token = sessionStorage.getItem('access_token') || localStorage.getItem('access_token');

    if (token) {
      request = request.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });
    }

    return next.handle(request);
  }
}

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
        catchError((error) => {
          // Handle token refresh failure
          this.authStateService.setLoggedIn(false);
          this.authStateService.removeTokens();
          this.authStateService.removeUserInfo();
          this.router.navigate(['/']);
          return throwError(() => error);
        })
      );
    }
    
    return this.addTokenAndForwardRequest(request, next).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401) {
          this.authStateService.setLoggedIn(false);
          this.authStateService.removeTokens();
          this.authStateService.removeUserInfo();
          this.router.navigate(['/loginV3']);
        }
        return throwError(() => error);
      })
    );
  }

  private addTokenAndForwardRequest(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    const token = sessionStorage.getItem('access_token') || localStorage.getItem('access_token');

    console.log('🔐 Interceptor - Request URL:', request.url);
    console.log('🔐 Interceptor - Token available:', !!token);
    console.log('🔐 Interceptor - Token expired:', this.authStateService.isTokenExpired());

    if (token) {
      console.log('🔐 Interceptor - Adding Authorization header with token');
      request = request.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });
    } else {
      console.warn('⚠️ Interceptor - No token found for request:', request.url);
    }

    return next.handle(request);
  }
}

import { Injectable } from '@angular/core';
import { Observable, BehaviorSubject, throwError, EMPTY } from 'rxjs';
import { filter, take, switchMap, tap, catchError } from 'rxjs/operators';
import { AuthStateService } from './auth-state.service';
import { RegistrationService } from '../login-register/registration.service';
import { ActivatedRoute, Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class OauthTokenService {
    public isHome: boolean = true;
  public isLoggedIn: boolean = false;
  private isRefreshing = false;
  private authCode: string = '';
  private refreshTokenSubject: BehaviorSubject<any> = new BehaviorSubject<any>(null);
  // PKCE variables
  private codeVerifier: string = '';
  private codeChallenge: string = '';
  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private authStateService: AuthStateService,
    private registrationService: RegistrationService
  ) {}

  
  refreshAccessToken(): Observable<any> {
    if (this.isRefreshing) {
      // Wait for the refresh to complete
      return this.refreshTokenSubject.pipe(
        filter(token => token != null),
        take(1),
        switchMap(token => {
          return new Observable(observer => {
            observer.next(token);
            observer.complete();
          });
        })
      );
    }

    this.isRefreshing = true;
    this.refreshTokenSubject.next(null);

    const refreshToken = this.authStateService.getRefreshToken();

     if (!refreshToken) {
    this.isRefreshing = false;  // Reset flag
    this.router.navigate(['/loginV3']);
    return EMPTY;  // Return empty observable
  }

    return this.performTokenRefresh(refreshToken);
  }

  private performTokenRefresh(refreshToken: string): Observable<any> {
    return new Observable(observer => {
      const xhr = new XMLHttpRequest();
      
      xhr.open('POST', '/oauth2/token', true);
      xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');

      const params = new URLSearchParams();
      params.append('grant_type', 'refresh_token');
      params.append('refresh_token', refreshToken);
      params.append('client_id', 'oidc-client');
      params.append('client_secret', 'secret');
      
      xhr.onload = () => {
        if (xhr.status === 200) {
          try {
            const response = JSON.parse(xhr.responseText);
            const newAccessToken = response.access_token;
            const newRefreshToken = response.refresh_token || refreshToken;

            // Update tokens
            this.authStateService.storeTokens(newAccessToken, newRefreshToken);
            this.authStateService.checkAndUpdateLoginStatus();

            this.isRefreshing = false;
            this.refreshTokenSubject.next(newAccessToken);
            observer.next(newAccessToken);
            observer.complete();
          } catch (e) {
            this.isRefreshing = false;
            observer.error(e);
          }
        } else {
          this.isRefreshing = false;
          this.authStateService.setLoggedIn(false);
          observer.error('Token refresh failed');
        }
      };

      xhr.onerror = () => {
        this.isRefreshing = false;
        this.authStateService.setLoggedIn(false);
        observer.error('Token refresh request failed');
      };

      xhr.send(params.toString());
    });
  }

  private exchangeAuthcodeForTokenObservable(authcode: string): Observable<any> {
    return this.exchangeCodeForToken(authcode).pipe(
      switchMap((response: any) => {
        const newAccessToken = response.access_token;
        this.isRefreshing = false;
        this.refreshTokenSubject.next(newAccessToken);
        this.authStateService.setLoggedIn(true);
        return new Observable(observer => {
          observer.next(newAccessToken);
          observer.complete();
        });
      }),
      catchError((error: any) => {
        this.isRefreshing = false;
        this.authStateService.setLoggedIn(false);
        return throwError(() => new Error('Token exchange failed', { cause: error }));
      })
    );
  }

  /**
   * Generate PKCE code_verifier and code_challenge asynchronously
   * code_verifier: random string (43-128 characters)
   * code_challenge: BASE64URL(SHA256(code_verifier))
   */
   async generatePKCEAsync(): Promise<void> {
    // Generate random code_verifier (128 characters)
    const characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-._~';
    let codeVerifier = '';
    for (let i = 0; i < 128; i++) {
      codeVerifier += characters.charAt(Math.floor(Math.random() * characters.length));
    }
    this.codeVerifier = codeVerifier;
    
    // Generate code_challenge as BASE64URL(SHA256(code_verifier))
    const encoder = new TextEncoder();
    const data = encoder.encode(codeVerifier);
    const hashBuffer = await crypto.subtle.digest('SHA-256', data);
    this.codeChallenge = this.base64UrlEncode(hashBuffer);
    
    // Store code_verifier in sessionStorage for later use when exchanging code
    sessionStorage.setItem('code_verifier', this.codeVerifier);
    
    // Redirect to OAuth authorization endpoint
    this.redirectToAuthorizationEndpoint();
  }

  /**
   * Redirect to OAuth2 authorization endpoint
   */
  private redirectToAuthorizationEndpoint(): void {
    const authorizationUrl = `/oauth2/authorize?response_type=code&client_id=oidc-client&code_challenge=${this.codeChallenge}&code_challenge_method=S256&scope=app.read`;
    window.location.href = authorizationUrl;
  }

  /**
   * Convert ArrayBuffer to BASE64URL string
   */
  private base64UrlEncode(buffer: ArrayBuffer): string {
    const bytes = new Uint8Array(buffer);
    let binary = '';
    for (let i = 0; i < bytes.byteLength; i++) {
      binary += String.fromCharCode(bytes[i]);
    }
    return btoa(binary)
      .replace(/\+/g, '-')
      .replace(/\//g, '_')
      .replace(/=+$/, '');
  }

  exchangeCodeForToken(authCode: string): Observable<any> {
    // Retrieve code_verifier from sessionStorage
    const codeVerifier = sessionStorage.getItem('code_verifier');
    
    if (!codeVerifier) {
      console.error('Code verifier not found in session storage');
      return throwError(() => 'Code verifier not found in session storage');
    }
    
    // Prepare the token exchange request
    const tokenRequest = {
      code: authCode,
      code_verifier: codeVerifier,
      client_id: 'oidc-client',
      grant_type: 'authorization_code',
      redirect_uri: window.location.origin + '/',
      client_secret: 'secret',
    };
    
    console.log('Exchanging authorization code for access token');
    
    // Call backend endpoint to exchange code for token
    return this.registrationService.exchangeCodeForToken(tokenRequest).pipe(
      tap({
        next: (response) => {
          console.log('Token exchange successful:', response);
          // Store access token for subsequent requests
          if (response.access_token) {
            this.authStateService.storeTokens(response.access_token, response.refresh_token);
            // Get user name from response or use default
            const decoded = this.authStateService.decodeToken(response.access_token);
            const userName = decoded?.sub || 'User';
            sessionStorage.setItem('userInfo', JSON.stringify({ userName }));
            
            // Notify auth service about successful login
            this.authStateService.setLoggedIn(true, userName);
          }
  
          // Clear stored code_verifier
          sessionStorage.removeItem('code_verifier');
          window.history.replaceState({}, document.title, window.location.pathname);
        },
        error: (error) => {
          console.error('Token exchange failed:', error);
        }
      })
    );
  }
}

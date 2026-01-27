import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthStateService {
  private loggedInSubject = new BehaviorSubject<boolean>(this.isTokenValid());
  public loggedIn$ = this.loggedInSubject.asObservable();

  private userNameSubject = new BehaviorSubject<string>(this.getUserName());
  public userName$ = this.userNameSubject.asObservable();

  constructor() {}

  /**
   * Decode JWT token and extract claims
   * @param token JWT token
   * @returns Decoded token object or null
   */
  public decodeToken(token: string): any {
    try {
      const base64Url = token.split('.')[1];
      const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
      const jsonPayload = decodeURIComponent(atob(base64).split('').map((c) => {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
      }).join(''));
      return JSON.parse(jsonPayload);
    } catch (error) {
      console.error('Error decoding token:', error);
      return null;
    }
  }

  /**
   * Check if access token is expired
   * @returns true if token is expired or doesn't exist
   */
  isTokenExpired(): boolean {
    const token = sessionStorage.getItem('access_token') || localStorage.getItem('access_token');
    if (!token) {
      return true;
    }

    const decoded = this.decodeToken(token);
    if (!decoded || !decoded.exp) {
      return true;
    }

    // Check if token expires in less than 5 minutes (300 seconds)
    const currentTime = Math.floor(Date.now() / 1000);
    const expiryTime = decoded.exp;
    return (expiryTime - currentTime) < 300;
  }

  /**
   * Get refresh token from storage
   * @returns refresh token or null
   */
  getRefreshToken(): string | null {
    return sessionStorage.getItem('refresh_token') || localStorage.getItem('refresh_token');
  }

  /**
   * Store tokens in storage
   * @param accessToken access token
   * @param refreshToken refresh token
   */
  storeTokens(accessToken: string, refreshToken: string): void {
    sessionStorage.setItem('access_token', accessToken);
    sessionStorage.setItem('refresh_token', refreshToken);
    //localStorage.setItem('access_token', accessToken);
    //localStorage.setItem('refresh_token', refreshToken);
  }

  /**
   * Update access token in storage
   * @param accessToken new access token
   */
  updateAccessToken(accessToken: string): void {
    sessionStorage.setItem('access_token', accessToken);
    //localStorage.setItem('access_token', accessToken);
  }

  removeTokens(): void {
    sessionStorage.removeItem('access_token');
    sessionStorage.removeItem('refresh_token');
    //localStorage.removeItem('access_token');
    //localStorage.removeItem('refresh_token');
  }
  removeUserInfo(): void {
    sessionStorage.removeItem('userInfo');
   // localStorage.removeItem('userInfo');
  }

  private isTokenValid(): boolean {
    const token = sessionStorage.getItem('access_token') || localStorage.getItem('access_token');
    return !!token;
  }

  private getUserName(): string {
    const userInfo = sessionStorage.getItem('userInfo');
    if (userInfo) {
      try {
        const user = JSON.parse(userInfo);
        return user.userName || 'User';
      } catch (e) {
        return 'User';
      }
    }
    return 'User';
  }

  setLoggedIn(loggedIn: boolean, userName?: string): void {
    this.loggedInSubject.next(loggedIn);
    if (userName) {
      this.userNameSubject.next(userName);
    }
  }

  setUserName(userName: string): void {
    this.userNameSubject.next(userName);
  }

  checkAndUpdateLoginStatus(): void {
    const isLoggedIn = this.isTokenValid();
    this.loggedInSubject.next(isLoggedIn);
    this.userNameSubject.next(this.getUserName());
  }

  getLoggedInStatus(): boolean {
    return this.isTokenValid();
  }
}

import { Injectable } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { Observable } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class RegistrationService {
  private apiUrl = '/customerSubmit';
  private loginUrl = '/loginV2';
  private logoutUrl = '/logoutV2';
  private pkceUrl = '/pkce/store'; // Endpoint to store PKCE data
  private tokenExchangeUrl = '/oauth2/token'; // Endpoint to exchange code for token
  private profileUrl = '/myAccount/profile';
  
  constructor(private http: HttpClient) { }
  
  signUp(adPost: any): Observable<any> {
    return this.http.post(this.apiUrl, adPost);
  }

  login(credentials: any): Observable<any> {
    return this.http.post(this.loginUrl, credentials);
  }

  logout(): Observable<any> {
    return this.http.post(this.logoutUrl, {});
  }

  storePKCEData(pkceData: any): Observable<any> {
    return this.http.post(this.pkceUrl, pkceData);
  }

  exchangeCodeForToken(tokenRequest: any): Observable<any> {
    // OAuth2 token endpoint requires form-encoded data, not JSON
    const params = new URLSearchParams();
    Object.keys(tokenRequest).forEach(key => {
      params.set(key, tokenRequest[key]);
    });
    
    const headers = new HttpHeaders({
      'Content-Type': 'application/x-www-form-urlencoded'
    });
    
    return this.http.post(this.tokenExchangeUrl, params.toString(), { headers });
  }

  getUserProfile(): Observable<any> {
    return this.http.get(this.profileUrl);
  }
}

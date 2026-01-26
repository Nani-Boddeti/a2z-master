import { Injectable } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { Observable } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class RegistrationService {
  private apiUrl = '/api/customerSubmit';
  private loginUrl = '/api/perform_login';
  private logoutUrl = '/api/logoutV2';
  private pkceUrl = '/pkce/store'; // Endpoint to store PKCE data
  private tokenExchangeUrl = '/oauth2/token'; // Endpoint to exchange code for token
  private profileUrl = '/api/myAccount/profile';
  private updateProfileUrl = '/api/myAccount/profile/update';
   private forgotPasswordEmailLinkUrl = '/api/forgot-password';
   private forgotPasswordSubmitUrl = '/api/reset-password';
  constructor(private http: HttpClient) { }
  
  signUp(adPost: any): Observable<any> {
     const jsonheaders = new HttpHeaders({
      'Content-Type': 'application/json'
    });
    return this.http.post(this.apiUrl, adPost,{headers: jsonheaders});
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

  updateProfile(profileData: any): Observable<any> {
    const jsonheaders = new HttpHeaders({
      'Content-Type': 'application/json'
    });
    return this.http.put(this.updateProfileUrl, profileData, { headers: jsonheaders });
  }

  submitForgotPasswordFrom(data: any): Observable<any> {
    const jsonheaders = new HttpHeaders({
      'Content-Type': 'application/json'
    });
    return this.http.post(this.forgotPasswordSubmitUrl, data, { headers: jsonheaders });
  }

  getForgotPasswordFromEmailLink(userName: any): Observable<any> {
    const url = `${this.forgotPasswordEmailLinkUrl}?userName=${userName}`;
    return this.http.get(url);
  }
}

import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Token } from '@angular/compiler';
import { Injectable } from '@angular/core';
import { CookieService } from 'ngx-cookie-service';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class OauthService {

  constructor(private http:HttpClient,private cookieService: CookieService) { }
  login!: HTMLElement;
  client_id :string ="oidc-client";
  redirectUri:string="http://localhost:4200";
  code_verifier:string="XX92viuZqu9NXYhORu7S_8h_hL1Zd1QNoG3lf5svE5QQREJy0AISHVHz2hyzjW47GKqauiYRadbKMWScjT8uvbYtzGu8RJLu_O9LeOJhstda14XQtRdzg4N3DlKtB0lr";
  client_secret:string="secret";
  getLoginPage() {
    
    const httpHeaders = new HttpHeaders()
    .set("Access-Control-Allow-Origin","*");
    const httpParams = new HttpParams()
    .set("response_type","code")
    .set("client_id","oidc-client")
    .set("code_challenge","8reAEqObnriL71znTPJfxW-dHgM17hfTZ4AJMre8vwg")
    .set("code_challenge_method","S256")
    .set("scope","app.read");

    const options = { params: httpParams, headers: httpHeaders };
    this.http.get<HTMLElement>("/oauth2/authorize",options)
    .subscribe(value=>this.login=value);
    return this.login;
  }

  retrieveAccessToken(code:string) {
    const body = `code=${code}&grant_type=authorization_code&client_id=oidc-client&code_verifier=XX92viuZqu9NXYhORu7S_8h_hL1Zd1QNoG3lf5svE5QQREJy0AISHVHz2hyzjW47GKqauiYRadbKMWScjT8uvbYtzGu8RJLu_O9LeOJhstda14XQtRdzg4N3DlKtB0lr&client_secret=secret`;
    const httpHeaders = new HttpHeaders()
    .set("Access-Control-Allow-Origin","*")
    .set("Content-type","application/x-www-form-urlencoded");
    this.http.post('/oauth2/token', 
      body, {headers: httpHeaders})
        .subscribe(
          data => this.saveToken(data),
          err => console.log('Invalid Credentials')); 
  }

  retrieveAccessTokenWithRefresh(code:string) {
    const body = `code=${code}&grant_type=authorization_code&client_id=oidc-client&code_verifier=XX92viuZqu9NXYhORu7S_8h_hL1Zd1QNoG3lf5svE5QQREJy0AISHVHz2hyzjW47GKqauiYRadbKMWScjT8uvbYtzGu8RJLu_O9LeOJhstda14XQtRdzg4N3DlKtB0lr&client_secret=secret`;
    const httpHeaders = new HttpHeaders()
    .set("Access-Control-Allow-Origin","*")
    .set("Content-type","application/x-www-form-urlencoded");
    this.http.post('/oauth2/token', 
      body, {headers: httpHeaders})
        .subscribe(
          data => this.saveToken(data),
          err => console.log('Invalid Credentials')); 
  }

  saveToken(token:any) {
    var expireDate = new Date().getTime() + (1000 * token.expires_in);
    this.cookieService.set("access_token", token.access_token, expireDate);
    console.log('Obtained Access token');
    window.location.href = 'http://localhost:4200';
  }

  checkCredentials() {
    return this.cookieService.check('access_token');
  } 

  logout() {
    this.cookieService.delete('access_token');
    window.location.reload();
  }
}

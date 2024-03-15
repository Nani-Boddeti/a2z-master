import { Component, OnInit } from '@angular/core';
import { OauthService } from '../oauth.service';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-login-register',
  templateUrl: './login-register.component.html',
  styleUrl: './login-register.component.css'
})
export class LoginRegisterComponent implements OnInit{
  constructor(private oauth:OauthService){}
  title : String = "Hello test login component";
 /*  loginPage :HTMLElement = this.oauth.getLoginPage(); */
 public isLoggedIn = false;
  login() {
    window.location.href = 
      'http://localhost:4200/oauth2/authorize?response_type=code&client_id=oidc-client&code_challenge=8reAEqObnriL71znTPJfxW-dHgM17hfTZ4AJMre8vwg&code_challenge_method=S256&scope=app.read';
    }

    ngOnInit(): void {
      let i = window.location.href.indexOf('code');
      this.isLoggedIn = this.oauth.checkCredentials();   
      if(!this.isLoggedIn && i != -1) {
        this.oauth.retrieveAccessToken(window.location.href.substring(i+5));
      }
      if(i != -1){
        window.location.href = 'http://localhost:4200';
      }
    }

    logout() {
      this.oauth.logout();
    }
}

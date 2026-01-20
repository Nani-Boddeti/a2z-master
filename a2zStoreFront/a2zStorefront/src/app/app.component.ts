import { Component, OnInit } from '@angular/core';
import { OauthTokenService } from './services/oauth-token.service';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit {
  title = 'a2zStorefront';
  public isHome: boolean = true;
  public isLoggedIn: boolean = false;
  private authCode: string = '';
  constructor(private oauthTokenService: OauthTokenService,private route: ActivatedRoute) {}
  ngOnInit() {

    this.route.queryParams.subscribe(params => {
    this.authCode = params['code']  || '';

    if (this.authCode) {
      this.oauthTokenService.exchangeCodeForToken(this.authCode).subscribe({
        next: () => {
          this.isLoggedIn = true;
          this.isHome = true;
          this.clearUrl();
        },
        error: (error: any) => {
          console.error('Error exchanging code for token', error);
          this.clearUrl();
        }
      });
    }
   
  });

   
  }

  private clearUrl(): void {
    window.history.replaceState({}, document.title, window.location.pathname);
  }
}

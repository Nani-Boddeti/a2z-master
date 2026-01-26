import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class RequestMatcherService {
  
 private WHITE_LIST_URLS : string[] = [		
            "/", "/api/ad/all", "/api/ad/view/**", "/api/c/**", "/api/customerSubmit",
            "/api/suggest/password", "/api/generate/otp/**", "/api/validateOTP",
            "/api/login**", "/api/loginV3", "/oauth2/**", "/oauth2/token","/api/login", "/api/search/**",
            "/api/search/all", "/.well-known/**","/api/perform_login","/api/search/category/**","/api/reset-password","/api/forgot-password"
    ];

  // Match request.url against URL list
  matchRequest(url: string): boolean {
    return this.WHITE_LIST_URLS.some(pattern => this.matchPattern(url, pattern));
  }

  // Handle wildcard matching (supports /**)
  private matchPattern(url: string, pattern: string): boolean {
    // Convert pattern to regex
    const regexPattern = pattern
      .replace(/\/\\*\\*\/?$/g, '/.*')  // /** → /.*
      .replace(/\*\*/g, '.*')           // ** → .*
      .replace(/\*/g, '[^/]*');         // * → [^/]* (non-slash chars)
    
    const regex = new RegExp(`^${regexPattern}$`);
    return regex.test(url);
  }

  // Get all protected patterns
  getProtectedUrls(): string[] {
    return [...this.WHITE_LIST_URLS];
  }
}

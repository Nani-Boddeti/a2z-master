import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class RequestMatcherService {
  
 private WHITE_LIST_URLS : string[] = [		
            "/", "/ad/all", "/ad/view/**", "/c/**", "/customerSubmit",
            "/suggest/password", "/generate/otp/**", "/validateOTP",
            "/login**", "/loginV3", "/oauth2/**", "/oauth2/token","/login", "/search/**",
            "/search/all", "/.well-known/**","/perform_login","/search/category/**"
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

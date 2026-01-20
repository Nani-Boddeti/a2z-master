# Logout Implementation Guide

## Overview

A complete logout endpoint `/logoutV2` has been implemented that securely clears all authentication tokens, session data, and cookies.

---

## Logout Endpoint Details

### Endpoint Information

**URL:** `POST /logoutV2`

**Authentication Required:** No (whitelisted endpoint)

**Response Type:** `LoginResponse`

### Request

No request body required. The endpoint automatically clears the current user's session.

```bash
curl -X POST http://localhost:8080/logoutV2
```

### Success Response (200 OK)

```json
{
  "success": true,
  "message": "Logout successful",
  "statusCode": 200,
  "redirectUrl": "http://localhost:4200/login",
  "nextStep": "login",
  "additionalInfo": {
    "logoutTime": 1735207200000,
    "message": "You have been successfully logged out"
  }
}
```

### Error Response (500 Internal Server Error)

```json
{
  "success": false,
  "message": "Logout failed: ...",
  "statusCode": 500,
  "errorCode": "LOGOUT_ERROR",
  "additionalInfo": {
    "errorType": "Exception",
    "timestamp": 1735207200000
  }
}
```

---

## Logout Process (Step-by-Step)

### Step 1: Get Current User
- Retrieves the username from session (`currentUser` attribute)
- Logs the username being logged out

### Step 2: Remove Session Attributes
Clears the following from session:
- `currentUser` - Current user's username
- `accessToken` - OAuth2 access token
- `refreshToken` - OAuth2 refresh token
- `idToken` - OpenID Connect ID token

### Step 3: Invalidate Session
- Destroys the HTTP session completely
- Prevents session reuse

### Step 4: Clear Authentication Cookies
Removes all authentication-related cookies:
- `access_token` - Set MaxAge to 0 (deleted)
- `refresh_token` - Set MaxAge to 0 (deleted)
- `id_token` - Set MaxAge to 0 (deleted)
- `JSESSIONID` - Session cookie cleared

### Step 5: Return Response
- Returns success response with:
  - Logout confirmation message
  - Redirect URL to login page
  - Next step instruction
  - Logout timestamp

---

## Cookie Clearing Details

All cookies are cleared with the following settings:

```java
Cookie cookie = new Cookie("cookie_name", "");
cookie.setMaxAge(0);              // Expire immediately
cookie.setPath("/");              // Available to all paths
cookie.setHttpOnly(true);         // Not accessible via JavaScript
cookie.setSecure(true);           // Only sent over HTTPS
response.addCookie(cookie);
```

This ensures:
✅ Cookies are deleted from the browser
✅ Cannot be accessed by JavaScript (XSS protection)
✅ Only sent over secure HTTPS connections
✅ Available across all paths

---

## Angular Integration

### Logout Service

```typescript
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class LogoutService {
  
  constructor(private http: HttpClient) { }
  
  logout(): Observable<any> {
    return this.http.post('/logoutV2', {});
  }
}
```

### Logout Component

```typescript
import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { LogoutService } from './logout.service';

@Component({
  selector: 'app-logout',
  templateUrl: './logout.component.html',
  styleUrls: ['./logout.component.css']
})
export class LogoutComponent {
  
  constructor(
    private logoutService: LogoutService,
    private router: Router
  ) { }
  
  onLogout() {
    this.logoutService.logout().subscribe(
      (response) => {
        if (response.success) {
          // Clear tokens from localStorage
          localStorage.removeItem('accessToken');
          localStorage.removeItem('refreshToken');
          localStorage.removeItem('userId');
          localStorage.removeItem('userRole');
          localStorage.removeItem('currentUser');
          
          // Clear any other stored data
          sessionStorage.clear();
          
          // Redirect to login
          this.router.navigate(['/login']);
        } else {
          console.error('Logout failed:', response.message);
        }
      },
      (error) => {
        console.error('Logout error:', error);
        // Force redirect even if error occurs
        this.router.navigate(['/login']);
      }
    );
  }
}
```

### Logout Button in Navigation

```html
<nav class="navbar">
  <div class="navbar-brand">A2Z Application</div>
  
  <div class="navbar-right">
    <span class="user-name">{{ currentUser }}</span>
    <button (click)="onLogout()" class="btn-logout">
      Logout
    </button>
  </div>
</nav>
```

### Interceptor for Token Management

```typescript
import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const token = localStorage.getItem('accessToken');
    
    if (token) {
      req = req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });
    }
    
    return next.handle(req);
  }
}
```

---

## Security Configuration

The `/logoutV2` endpoint is whitelisted in `AuthorizationServerConfig.java`:

```java
private static final String[] WHITE_LIST_URLS = {
    // ...existing endpoints...
    "/logoutV2",                 // Custom logout endpoint
    // ...other endpoints...
};
```

This means:
✅ Logout is accessible without authentication
✅ Users can log out even if session is partially corrupted
✅ No authentication required to clear tokens

---

## Console Logging Output

When logout is called, you'll see:

```
Step 1: Logout initiated
Logging out user: naniv2614@test.com
Session attributes cleared
Session invalidated
Access token cookie cleared
Refresh token cookie cleared
ID token cookie cleared
Session cookie cleared
Authentication cookies cleared
Logout successful
```

---

## Testing the Logout Endpoint

### Using cURL

```bash
curl -X POST http://localhost:8080/logoutV2 \
  -H "Content-Type: application/json" \
  -c cookies.txt
```

### Using Postman

1. **Method:** POST
2. **URL:** `http://localhost:8080/logoutV2`
3. **Headers:** `Content-Type: application/json`
4. **Body:** (empty)
5. **Click Send**

Expected response:
```json
{
  "success": true,
  "message": "Logout successful",
  "statusCode": 200,
  "redirectUrl": "http://localhost:4200/login",
  "nextStep": "login"
}
```

### Using Angular

```typescript
this.logoutService.logout().subscribe(
  response => console.log('Logged out:', response),
  error => console.error('Logout failed:', error)
);
```

---

## Logout Flow Diagram

```
┌─────────────────────────────────────────────────┐
│         User Clicks Logout Button               │
└────────────────────┬────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────┐
│    POST /logoutV2 (No Body Required)            │
└────────────────────┬────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────┐
│  Step 1: Get Current User from Session         │
└────────────────────┬────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────┐
│  Step 2: Remove Session Attributes             │
│  - currentUser                                  │
│  - accessToken                                  │
│  - refreshToken                                 │
│  - idToken                                      │
└────────────────────┬────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────┐
│  Step 3: Invalidate Session                     │
└────────────────────┬────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────┐
│  Step 4: Clear Authentication Cookies           │
│  - access_token (MaxAge=0)                      │
│  - refresh_token (MaxAge=0)                     │
│  - id_token (MaxAge=0)                          │
│  - JSESSIONID (MaxAge=0)                        │
└────────────────────┬────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────┐
│  Step 5: Return Success Response                │
│  - message: "Logout successful"                 │
│  - redirectUrl: "/login"                        │
│  - statusCode: 200                              │
└────────────────────┬────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────┐
│  Angular: Clear localStorage                    │
│  Angular: Redirect to /login                    │
└─────────────────────────────────────────────────┘
```

---

## Session Cleanup Details

### What Gets Cleared

| Item | Storage | Status |
|------|---------|--------|
| currentUser | Session | ✓ Removed |
| accessToken | Session + Cookie | ✓ Removed |
| refreshToken | Session + Cookie | ✓ Removed |
| idToken | Session + Cookie | ✓ Removed |
| JSESSIONID | Cookie | ✓ Removed |
| Session Data | Server | ✓ Invalidated |

### What Remains (Client-side - Manual Cleanup)

The following should be cleared by Angular frontend:

```typescript
// Clear from localStorage
localStorage.removeItem('accessToken');
localStorage.removeItem('refreshToken');
localStorage.removeItem('userId');
localStorage.removeItem('userRole');
localStorage.removeItem('currentUser');

// Clear sessionStorage
sessionStorage.clear();

// Clear route guards cache
// (Depends on your implementation)
```

---

## Error Handling

### Common Logout Errors

| Error | Cause | Solution |
|-------|-------|----------|
| Session already invalidated | Multiple logout calls | Handled gracefully, returns success anyway |
| Cookie operation fails | Server error | Returns 500 error, logs details |
| Network error | Connection issue | Angular handles with error callback |

### Logout on Expired Session

If the session has already expired:
- The endpoint still succeeds
- Attempts to clear already-missing attributes
- Returns 200 OK

---

## Best Practices

✅ **Always Clear Frontend Storage** - Don't rely solely on server-side clearing
✅ **Redirect After Logout** - Send user to login page immediately
✅ **Clear All Tokens** - Remove from both localStorage and sessionStorage
✅ **Destroy Session** - Server invalidates the HTTP session
✅ **Delete Cookies** - All auth cookies set to MaxAge=0
✅ **Log Logout Events** - For security auditing
✅ **Handle Errors Gracefully** - Still redirect even if logout fails

---

## Security Considerations

### Token Security
- ✅ HTTP-Only cookies cannot be accessed by JavaScript
- ✅ Secure flag ensures HTTPS-only transmission
- ✅ MaxAge=0 causes immediate deletion

### Session Security
- ✅ Session.invalidate() destroys session on server
- ✅ Cannot reuse old session IDs
- ✅ Prevents session fixation attacks

### CSRF Protection
- ✅ Logout uses POST (not GET)
- ✅ CSRF tokens validated (if enabled)
- ✅ Proper cookie attributes set

---

## Implementation Complete ✅

The logout functionality is fully implemented and includes:

✅ **Server-side logout** - `/logoutV2` endpoint
✅ **Session clearing** - All attributes removed
✅ **Cookie deletion** - All auth cookies cleared
✅ **Proper response** - LoginResponse with redirect info
✅ **Error handling** - Exception handling and logging
✅ **Security whitelisting** - Endpoint accessible without auth
✅ **Angular integration** - Service and component examples
✅ **Documentation** - Complete implementation guide

Your logout feature is production-ready! 🚀


# OAuth2 Authorization Code Flow - Custom Login Implementation

## Overview
This document describes the custom `/login` endpoint implementation that serves as an alternative to the default OAuth2 login form. It implements the standard OAuth2 Authorization Code flow for secure authentication.

## Architecture

### Flow Diagram
```
Angular Frontend
    |
    v
POST /login (username + password)
    |
    v
Step 1: Authenticate credentials against database
    |
    v
Step 2: Request authorization code from OAuth2 Authorization Server
    |
    v
Step 3: Exchange authorization code for access/refresh tokens
    |
    v
Store tokens in session & secure cookies
    |
    v
Return LoginResponse to Angular with tokens
    |
    v
Angular redirects to dashboard
```

## Implementation Details

### 1. LoginRequest DTO
**File:** `com.a2z.data.LoginRequest`
```java
{
  "username": "user@example.com",
  "password": "securePassword123"
}
```

### 2. LoginResponse DTO
**File:** `com.a2z.data.LoginResponse`
```java
{
  "success": true,
  "message": "Login successful",
  "authCode": "authorization_code_xyz",
  "accessToken": "eyJhbGciOiJSUzI1NiIs...",
  "refreshToken": "eyJhbGciOiJSUzI1NiIs...",
  "redirectUrl": "http://localhost:4200/dashboard"
}
```

### 3. Main Login Endpoint

**Endpoint:** `POST /login`
**Location:** `HomeController.java`

#### Request
```json
{
  "username": "testuser",
  "password": "TestPass@123"
}
```

#### Response (Success)
```json
{
  "success": true,
  "message": "Login successful",
  "authCode": "SplCdRqsBjdjzgiNEZJ3z...",
  "accessToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "redirectUrl": "http://localhost:4200/dashboard"
}
```

#### Response (Failure)
```json
{
  "success": false,
  "message": "Invalid username or password"
}
```

### 4. Step-by-Step Process

#### Step 1: Authenticate User
- Username and password are validated against the database
- Uses `DefaultCustomerService.authenticateCustomer(username, password)`
- Password is validated using Spring Security's `PasswordEncoder`
- Returns `Customer` object if credentials are valid, `null` otherwise

#### Step 2: Request Authorization Code
- Calls `getAuthorizationCode(username)` method
- Makes POST request to OAuth2 Authorization Server at `/oauth2/authorize`
- Sends authorization request parameters:
  - `client_id`: "oidc-client"
  - `response_type`: "code"
  - `redirect_uri`: "http://localhost:8080/login/callback"
  - `scope`: "openid profile email"
  - `state`: Random UUID for CSRF protection
- Receives authorization code from server

#### Step 3: Exchange Code for Tokens
- Calls `exchangeCodeForTokens(authorizationCode)` method
- Makes POST request to OAuth2 Token Endpoint at `/oauth2/token`
- Sends token exchange parameters:
  - `grant_type`: "authorization_code"
  - `code`: The authorization code from Step 2
  - `redirect_uri`: "http://localhost:8080/login/callback"
  - `client_id`: "oidc-client"
  - `client_secret`: "secret"
- Receives tokens in response:
  - `access_token`: JWT token for API authentication (15 minutes)
  - `refresh_token`: Token to refresh access token (7 days)
  - `id_token`: Optional OIDC token with user information

#### Step 4: Store Tokens
**Session Attributes:**
```java
request.getSession().setAttribute("currentUser", username);
request.getSession().setAttribute("accessToken", accessToken);
request.getSession().setAttribute("refreshToken", refreshToken);
request.getSession().setAttribute("idToken", idToken);
```

**HTTP-Only Secure Cookies:**
```
Cookie: access_token=<JWT>; HttpOnly; Secure; Path=/; MaxAge=900
Cookie: refresh_token=<JWT>; HttpOnly; Secure; Path=/; MaxAge=604800
```

#### Step 5: Return Response
Returns `LoginResponse` with all tokens and redirect URL to Angular frontend.

## Angular Integration

### Login Service Example
```typescript
// login.service.ts
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class LoginService {
  
  constructor(private http: HttpClient) { }
  
  login(username: string, password: string) {
    const loginRequest = {
      username: username,
      password: password
    };
    
    return this.http.post('/login', loginRequest);
  }
}
```

### Login Component Example
```typescript
// login.component.ts
import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { LoginService } from './login.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  username: string = '';
  password: string = '';
  errorMessage: string = '';
  
  constructor(
    private loginService: LoginService,
    private router: Router
  ) { }
  
  onLogin() {
    this.loginService.login(this.username, this.password).subscribe(
      (response: any) => {
        if (response.success) {
          // Store tokens in localStorage
          localStorage.setItem('accessToken', response.accessToken);
          localStorage.setItem('refreshToken', response.refreshToken);
          localStorage.setItem('currentUser', this.username);
          
          // Redirect to dashboard
          this.router.navigate(['/dashboard']);
        } else {
          this.errorMessage = response.message;
        }
      },
      (error) => {
        this.errorMessage = 'Login failed. Please try again.';
        console.error('Login error:', error);
      }
    );
  }
}
```

### Login HTML Form Example
```html
<!-- login.component.html -->
<div class="login-container">
  <form (ngSubmit)="onLogin()">
    <div class="form-group">
      <label for="username">Username:</label>
      <input 
        type="text" 
        id="username" 
        [(ngModel)]="username" 
        name="username" 
        required>
    </div>
    
    <div class="form-group">
      <label for="password">Password:</label>
      <input 
        type="password" 
        id="password" 
        [(ngModel)]="password" 
        name="password" 
        required>
    </div>
    
    <div *ngIf="errorMessage" class="error-message">
      {{ errorMessage }}
    </div>
    
    <button type="submit" class="btn-login">Login</button>
  </form>
</div>
```

## Configuration

### Security Whitelist
Added the following endpoints to the security whitelist in `AuthorizationServerConfig.java`:
- `/login` - Custom login endpoint
- `/login/callback` - OAuth2 callback handler
- `/login/authorize` - Authorization URL generator

### Application Properties
Ensure the following is configured in `application.properties`:
```properties
# OAuth2 Configuration
oauth2.authorization-server=http://localhost:8080
oauth2.client-id=oidc-client
oauth2.client-secret=secret
oauth2.redirect-uri=http://localhost:8080/login/callback
oauth2.scopes=openid,profile,email
```

## Security Considerations

1. **HTTPS Only**: Set cookies as Secure to only transmit over HTTPS in production
2. **HttpOnly Cookies**: Tokens are stored in HttpOnly cookies to prevent XSS attacks
3. **State Parameter**: Random state UUID prevents CSRF attacks
4. **Password Encoder**: Passwords are encoded using Spring Security's `PasswordEncoder`
5. **Token Expiration**: 
   - Access Token: 15 minutes
   - Refresh Token: 7 days
6. **Redirect URI Validation**: Only whitelisted redirect URIs are accepted

## Error Handling

### Common Errors and Solutions

| Error | Cause | Solution |
|-------|-------|----------|
| `Invalid username or password` | Credentials don't match database | Verify username and password |
| `Failed to obtain authorization code` | OAuth2 server not responding | Check authorization server is running |
| `Failed to exchange authorization code` | Invalid client credentials | Verify client_id and client_secret |
| `Login failed` | General exception during login | Check server logs for details |

## Testing

### Using cURL
```bash
curl -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"TestPass@123"}'
```

### Using Postman
1. Method: POST
2. URL: `http://localhost:8080/login`
3. Headers: `Content-Type: application/json`
4. Body:
```json
{
  "username": "testuser",
  "password": "TestPass@123"
}
```

## Logging

The implementation includes console logging for debugging:
- Step 1: Authenticating user
- Step 2: Requesting authorization code
- Step 3: Exchanging authorization code for tokens
- Step 4: Storing tokens
- Step 5: Returning response

Enable DEBUG logging in `application.properties`:
```properties
logging.level.com.a2z=DEBUG
logging.level.org.springframework.security=DEBUG
```

## Database Schema

The system assumes the following schema for customer authentication:
```sql
CREATE TABLE customer (
  user_name VARCHAR(255) PRIMARY KEY,
  password VARCHAR(255) NOT NULL,
  email VARCHAR(255),
  first_name VARCHAR(255),
  last_name VARCHAR(255),
  role VARCHAR(50),
  -- other fields
);
```

## Conclusion

This custom login implementation provides a secure, standard OAuth2 Authorization Code flow that works seamlessly with Angular applications while maintaining compatibility with the Spring Authorization Server.


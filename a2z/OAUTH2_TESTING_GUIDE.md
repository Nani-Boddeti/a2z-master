# OAuth2 Authorization Code Flow - Testing Guide

## Prerequisites

1. Application running on `http://localhost:8080`
2. Angular frontend running on `http://localhost:4200`
3. MySQL database running with a2z schema
4. Redis running (for session storage)
5. A customer account created in the database

## Database Setup

### Create Test User

```sql
INSERT INTO customer (
  user_name, 
  password, 
  email, 
  first_name, 
  last_name, 
  role
) VALUES (
  'naniv2614@test.com',
  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36MB3DxO',  -- password: 12345
  'naniv2614@test.com',
  'Nani',
  'Kumar',
  'ROLE_USER'
);
```

### Verify Registered Client

```sql
SELECT * FROM oauth2_registered_client WHERE client_id = 'oidc-client';
```

Expected output includes:
- Multiple redirect_uris
- PKCE enabled
- Access token time to live: 900 seconds

## Test 1: Complete Login Flow with cURL

### Step 1: Login Request

```bash
curl -X POST http://localhost:8080/loginV2 \
  -H "Content-Type: application/json" \
  -d '{
    "username": "naniv2614@test.com",
    "password": "12345"
  }' \
  -v
```

**Expected Response (200 OK):**
```json
{
  "success": true,
  "message": "Login successful",
  "statusCode": 200,
  "username": "naniv2614@test.com",
  "email": "naniv2614@test.com",
  "firstName": "Nani",
  "lastName": "Kumar",
  "userRole": "ROLE_USER",
  "accessToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "idToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 900,
  "redirectUrl": "http://localhost:4200/dashboard",
  "nextStep": "dashboard",
  "sessionId": "abc123def456",
  "mfaRequired": false,
  "additionalInfo": {
    "loginTime": 1735689342000,
    "ipAddress": "127.0.0.1"
  }
}
```

**Check console logs:**
```
Step 1: Authenticating user: naniv2614@test.com
User authenticated successfully: naniv2614@test.com
Step 2: Requesting authorization code from OAuth2 server
Step 2a: Generating authorization code for user: naniv2614@test.com
Generated PKCE - State: <uuid>, Code Verifier: <base64-string>
Stored code verifier and state in session
Authorization request URL: http://localhost:8080/oauth2/authorize?...
Authorization endpoint response code: 302
Authorization redirect location: http://localhost:8080/login/callback?code=<code>&state=<state>
Authorization code extracted from redirect: <code>
Step 3: Exchanging authorization code for tokens
Retrieved code verifier from session: <base64-string>
Token endpoint response code: 200
Token response received
Successfully exchanged authorization code for tokens
Tokens obtained successfully
Login successful for user: naniv2614@test.com
```

### Step 2: Test Protected Endpoint

```bash
# Extract access_token from login response
ACCESS_TOKEN="<access_token_from_response>"

curl -X GET http://localhost:8080/api/test/protected \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -v
```

**Expected Response (200 OK):**
```
JWT PROTECTED STRING
```

### Step 3: Logout

```bash
curl -X POST http://localhost:8080/logoutV2 \
  -H "Content-Type: application/json" \
  -v
```

**Expected Response (200 OK):**
```json
{
  "success": true,
  "message": "Logout successful",
  "statusCode": 200,
  "redirectUrl": "http://localhost:4200/login",
  "nextStep": "login",
  "additionalInfo": {
    "logoutTime": 1735689400000,
    "message": "You have been successfully logged out"
  }
}
```

## Test 2: Angular Frontend Integration

### Service Implementation

**auth.service.ts:**
```typescript
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { tap } from 'rxjs/operators';

export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  success: boolean;
  message: string;
  accessToken: string;
  refreshToken: string;
  idToken: string;
  username: string;
  email: string;
  redirectUrl: string;
  nextStep: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080';
  private isAuthenticatedSubject = new BehaviorSubject<boolean>(false);
  public isAuthenticated$ = this.isAuthenticatedSubject.asObservable();

  constructor(private http: HttpClient) {
    this.checkAuthentication();
  }

  login(username: string, password: string): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/loginV2`, {
      username,
      password
    }).pipe(
      tap(response => {
        if (response.success) {
          this.storeTokens(response);
          this.isAuthenticatedSubject.next(true);
        }
      })
    );
  }

  logout(): Observable<any> {
    return this.http.post(`${this.apiUrl}/logoutV2`, {}).pipe(
      tap(() => {
        this.clearTokens();
        this.isAuthenticatedSubject.next(false);
      })
    );
  }

  private storeTokens(response: LoginResponse): void {
    localStorage.setItem('access_token', response.accessToken);
    localStorage.setItem('refresh_token', response.refreshToken);
    localStorage.setItem('id_token', response.idToken);
    localStorage.setItem('user_info', JSON.stringify({
      username: response.username,
      email: response.email
    }));
  }

  private clearTokens(): void {
    localStorage.removeItem('access_token');
    localStorage.removeItem('refresh_token');
    localStorage.removeItem('id_token');
    localStorage.removeItem('user_info');
  }

  getAccessToken(): string | null {
    return localStorage.getItem('access_token');
  }

  isAuthenticated(): boolean {
    return !!this.getAccessToken();
  }

  private checkAuthentication(): void {
    this.isAuthenticatedSubject.next(this.isAuthenticated());
  }
}
```

### HTTP Interceptor

**auth.interceptor.ts:**
```typescript
import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private authService: AuthService) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const token = this.authService.getAccessToken();

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

### Login Component

**login.component.ts:**
```typescript
import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService, LoginResponse } from '../services/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  username: string = '';
  password: string = '';
  isLoading: boolean = false;
  errorMessage: string = '';

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  login(): void {
    this.isLoading = true;
    this.errorMessage = '';

    this.authService.login(this.username, this.password).subscribe(
      (response: LoginResponse) => {
        if (response.success) {
          console.log('Login successful:', response);
          // Navigate to the redirect URL specified in response
          this.router.navigate([response.nextStep]);
        } else {
          this.errorMessage = response.message;
        }
        this.isLoading = false;
      },
      (error) => {
        console.error('Login failed:', error);
        this.errorMessage = 'Login failed. Please try again.';
        this.isLoading = false;
      }
    );
  }
}
```

**login.component.html:**
```html
<div class="login-container">
  <h2>Login</h2>
  
  <form (ngSubmit)="login()">
    <div class="form-group">
      <label for="username">Username:</label>
      <input 
        type="text" 
        id="username" 
        [(ngModel)]="username" 
        name="username"
        required
      >
    </div>

    <div class="form-group">
      <label for="password">Password:</label>
      <input 
        type="password" 
        id="password" 
        [(ngModel)]="password" 
        name="password"
        required
      >
    </div>

    <button type="submit" [disabled]="isLoading">
      {{ isLoading ? 'Logging in...' : 'Login' }}
    </button>
  </form>

  <div *ngIf="errorMessage" class="error-message">
    {{ errorMessage }}
  </div>
</div>
```

### App Module Setup

**app.module.ts:**
```typescript
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { FormsModule } from '@angular/forms';

import { AppComponent } from './app.component';
import { LoginComponent } from './components/login/login.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { AuthInterceptor } from './interceptors/auth.interceptor';
import { AppRoutingModule } from './app-routing.module';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    DashboardComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    FormsModule,
    AppRoutingModule
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
```

## Test 3: Error Scenarios

### Test 3a: Invalid Credentials

**Request:**
```bash
curl -X POST http://localhost:8080/loginV2 \
  -H "Content-Type: application/json" \
  -d '{
    "username": "naniv2614@test.com",
    "password": "wrongpassword"
  }'
```

**Expected Response (401 Unauthorized):**
```json
{
  "success": false,
  "message": "Invalid username or password",
  "statusCode": 401,
  "errorCode": "INVALID_CREDENTIALS"
}
```

### Test 3b: Missing Username

**Request:**
```bash
curl -X POST http://localhost:8080/loginV2 \
  -H "Content-Type: application/json" \
  -d '{
    "password": "12345"
  }'
```

**Expected Response (400 Bad Request):**
```json
{
  "success": false,
  "message": "Validation failed",
  "statusCode": 400,
  "violations": [
    {
      "fieldName": "username",
      "message": "Username is required"
    }
  ]
}
```

### Test 3c: Token Expiration

After 15 minutes (900 seconds), the access token expires.

**Request with expired token:**
```bash
curl -X GET http://localhost:8080/api/test/protected \
  -H "Authorization: Bearer <expired_token>"
```

**Expected Response (401 Unauthorized):**
```
Token has expired
```

**Solution:** Use refresh token to get new access token

```bash
curl -X POST http://localhost:8080/refreshToken \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "<refresh_token>"
  }'
```

## Test 4: PKCE Validation

### Verify PKCE is Working

Check console logs for:
1. Code verifier generation (43 characters, Base64URL)
2. Code challenge generation (SHA256 hash, Base64URL)
3. Code verifier stored in session
4. Code verifier retrieved from session during token exchange

**Sample Console Output:**
```
Generated code verifier: J1p9q8XyZqW2eRtUoP3kLmN4vBcDfGhIjKlMnOpQrSt=
Generated code challenge: abc123def456ghi789jkl012mno345pqr678stu901vwx
Stored code verifier and state in session
Retrieved code verifier from session: J1p9q8XyZqW2eRtUoP3kLmN4vBcDfGhIjKlMnOpQrSt=
```

## Test 5: Session Management

### Verify Session Storage

1. Login successfully
2. Check browser DevTools:
   - Application → Cookies → localhost:8080
   - Look for: `JSESSIONID`, `access_token`, `refresh_token`

3. Check server session:
   - Redis command: `KEYS spring:session*`
   - Look for session with oauth2_code_verifier, oauth2_state attributes

### Session Cleanup

1. Logout successfully
2. Verify cookies are cleared
3. Verify session attributes are removed

## Test 6: Multiple Concurrent Logins

Test that multiple users can login at same time without code verifier collision.

```bash
# Terminal 1
curl -X POST http://localhost:8080/loginV2 \
  -H "Content-Type: application/json" \
  -d '{"username": "user1@test.com", "password": "pass1"}' &

# Terminal 2
curl -X POST http://localhost:8080/loginV2 \
  -H "Content-Type: application/json" \
  -d '{"username": "user2@test.com", "password": "pass2"}' &

# Wait for both to complete
wait
```

Each should get unique code verifier and complete successfully.

## Debugging Tips

### 1. Enable Debug Logging

Add to `application.properties`:
```properties
logging.level.org.springframework.security=DEBUG
logging.level.org.springframework.security.oauth2=DEBUG
logging.level.com.a2z=DEBUG
```

### 2. Check Authorization Server Logs

Look for:
- Authorization request received
- Authorization code issued
- Token request received
- Token issued

### 3. Validate JWT Tokens

Use [jwt.io](https://jwt.io) to decode tokens:

1. Copy access_token from response
2. Paste in jwt.io
3. Verify:
   - Header: `alg: RS256`, `typ: JWT`
   - Payload: `iss`, `sub`, `aud`, `exp`, `scope`
   - Signature: Verified with public key

### 4. Monitor Database

```sql
-- Check registered client
SELECT * FROM oauth2_registered_client WHERE client_id = 'oidc-client';

-- Check authorization codes issued
SELECT * FROM oauth2_authorization WHERE principal_name = 'naniv2614@test.com' LIMIT 5;

-- Check tokens issued
SELECT token_type, authorization_grant_type FROM oauth2_authorization LIMIT 5;
```

### 5. Check Session Data

```bash
# Connect to Redis
redis-cli

# List all sessions
KEYS spring:session*

# View session attributes
HGETALL "spring:session:sessions:<session_id>"
```

## Performance Testing

### Load Test with ApacheBench

```bash
# Test login endpoint with 100 requests, 10 concurrent
ab -n 100 -c 10 -p login.json -T application/json \
  http://localhost:8080/loginV2
```

Where `login.json`:
```json
{
  "username": "naniv2614@test.com",
  "password": "12345"
}
```

### Expected Metrics

- Requests per second: > 50
- Mean response time: < 500ms
- 95% response time: < 1000ms
- 99% response time: < 2000ms

## Common Issues and Solutions

| Issue | Cause | Solution |
|-------|-------|----------|
| Code verifier not found in session | Session lost or expired | Check Redis, verify session timeout settings |
| PKCE validation failed | Mismatched code verifier | Verify code_verifier is stored and retrieved correctly |
| Redirect URI mismatch | Different redirect_uri used | Ensure all three requests use same URI |
| Invalid client credentials | Wrong client_id/secret | Check InitRegisteredClients.java configuration |
| Token endpoint 400 error | Missing or invalid parameters | Check console logs for exact error message |
| Access token not working | Token expired or invalid | Check token exp claim, use refresh token if needed |

## Next Steps After Testing

1. **Production Deployment**
   - Use HTTPS instead of HTTP
   - Store client_secret securely (environment variables)
   - Enable token encryption
   - Configure proper CORS policies

2. **Enhanced Security**
   - Implement token revocation endpoint
   - Add token introspection
   - Enable token binding
   - Implement rate limiting

3. **Monitoring & Analytics**
   - Log all authentication events
   - Monitor token issuance
   - Track failed login attempts
   - Alert on suspicious patterns

4. **User Experience**
   - Implement "Remember Me" functionality
   - Add token refresh automatically
   - Show token expiration warnings
   - Support multiple device login


# LoginRequest and LoginResponse - Complete DTO Objects

## Overview
Both LoginRequest and LoginResponse have been fully implemented with comprehensive fields for OAuth2 Authorization Code Flow integration.

---

## 1. LoginRequest DTO

**File:** `com.a2z.data.LoginRequest`

### Purpose
Receives login credentials and OAuth2 parameters from Angular frontend.

### Fields

#### Authentication Fields (Required)
- `username` - Username for authentication (3-50 characters, required)
- `password` - User password (minimum 5 characters, required)

#### OAuth2 Fields (Optional)
- `clientId` - OAuth2 client identifier
- `redirectUri` - Redirect URI after authentication
- `scope` - OAuth2 scopes (e.g., "openid profile email")
- `state` - CSRF protection state parameter
- `responseType` - OAuth2 response type (e.g., "code")
- `grantType` - Grant type for token exchange
- `codeChallenge` - PKCE code challenge
- `codeChallengeMethod` - PKCE method (S256 or plain)

#### Device/Client Information (Optional)
- `deviceId` - Unique device identifier
- `userAgent` - Client user agent string
- `ipAddress` - Client IP address
- `locale` - User's locale/language preference
- `rememberMe` - Remember me checkbox flag

### Constructors
```java
// Default constructor
public LoginRequest()

// Basic authentication
public LoginRequest(String username, String password)

// With client ID
public LoginRequest(String username, String password, String clientId)
```

### Validation Rules
- Username: Required, 3-50 characters
- Password: Required, minimum 5 characters

### Example Request
```json
{
  "username": "user@example.com",
  "password": "SecurePass@123",
  "clientId": "oidc-client",
  "scope": "openid profile email",
  "rememberMe": true,
  "locale": "en_US"
}
```

---

## 2. LoginResponse DTO

**File:** `com.a2z.data.LoginResponse`

### Purpose
Returns authentication result, tokens, and user information to Angular frontend.

### Fields

#### Response Status
- `success` - Whether login was successful (boolean)
- `message` - Response message
- `statusCode` - HTTP status code (200, 401, 500, etc.)
- `errorCode` - Error code for failures (e.g., "INVALID_CREDENTIALS")

#### OAuth2 Tokens
- `authCode` - Authorization code from OAuth2 server
- `accessToken` - JWT access token for API calls
- `refreshToken` - Token to refresh expired access token
- `idToken` - OpenID Connect ID token (optional)
- `tokenType` - Token type (typically "Bearer")
- `expiresIn` - Token expiration time in seconds

#### User Information
- `username` - Authenticated username
- `email` - User email address
- `firstName` - User's first name
- `lastName` - User's last name
- `userRole` - User's role (e.g., "ROLE_USER", "ROLE_ADMIN")
- `userId` - User's database ID

#### Navigation
- `redirectUrl` - URL to redirect user after login
- `nextStep` - Next action/step for frontend (e.g., "dashboard", "mfa")

#### Token Metadata
- `scope` - Granted OAuth2 scopes
- `state` - State parameter from request
- `issuedAt` - Token issue timestamp
- `expiresAt` - Token expiration timestamp

#### Device/Session Information
- `sessionId` - Server session ID
- `deviceId` - Client device ID
- `ipAddress` - Client IP address
- `mfaRequired` - Whether MFA is required
- `mfaMethod` - MFA method if required (e.g., "SMS", "TOTP")

#### Additional Data
- `additionalInfo` - Map for custom key-value pairs
- `locale` - User's locale/language
- `rememberMeEnabled` - Whether remember me is enabled

### Constructors
```java
// Default constructor
public LoginResponse()

// Basic response
public LoginResponse(boolean success, String message)

// With tokens
public LoginResponse(boolean success, String message, String authCode, 
                    String accessToken, String refreshToken)
```

### Methods
```java
// Add custom information
void addAdditionalInfo(String key, Object value)

// All getters and setters for fields
```

### Example Success Response
```json
{
  "success": true,
  "message": "Login successful",
  "statusCode": 200,
  "authCode": "SplCdRqsBjdjzgiNEZJ3z...",
  "accessToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "idToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 900,
  "username": "user@example.com",
  "email": "user@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "userRole": "ROLE_USER",
  "userId": 123,
  "scope": "openid profile email",
  "sessionId": "ABC123DEF456",
  "mfaRequired": false,
  "redirectUrl": "http://localhost:4200/dashboard",
  "nextStep": "dashboard",
  "additionalInfo": {
    "loginTime": 1735207200000,
    "ipAddress": "192.168.1.100"
  }
}
```

### Example Failure Response
```json
{
  "success": false,
  "message": "Invalid username or password",
  "statusCode": 401,
  "errorCode": "INVALID_CREDENTIALS",
  "additionalInfo": {
    "timestamp": 1735207200000,
    "errorType": "AuthenticationException"
  }
}
```

---

## 3. Implementation in HomeController

### Login Endpoint
```
POST /login
Content-Type: application/json

Request Body: LoginRequest
Response Body: LoginResponse (200 OK or error status)
```

### What Happens in the Login Method

1. **Step 1: Authenticate Credentials**
   - Validates username/password against database
   - Returns 401 if credentials invalid

2. **Step 2: Get Authorization Code**
   - Requests authorization code from OAuth2 server
   - Returns 500 if code generation fails

3. **Step 3: Exchange Code for Tokens**
   - Exchanges authorization code for tokens
   - Returns 500 if exchange fails

4. **Step 4: Store Tokens**
   - Saves tokens in HTTP session
   - Sets secure HTTP-only cookies

5. **Step 5: Populate LoginResponse**
   - Adds user details
   - Adds token metadata
   - Adds session information
   - Returns 200 with complete response

---

## 4. Security Features

✅ **Validation**
- Username and password validation
- Size and format constraints

✅ **Token Security**
- HTTP-Only cookies (prevent XSS)
- Secure flag (HTTPS only)
- Token expiration (15 min access, 7 days refresh)

✅ **Session Management**
- Session ID tracking
- Device tracking capability
- IP address logging

✅ **Error Handling**
- Specific error codes for different failures
- Detailed error information in responses
- Timestamp logging

✅ **CSRF Protection**
- State parameter support
- PKCE support (code challenge)

---

## 5. Angular Integration Example

### Login Service
```typescript
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class LoginService {
  
  constructor(private http: HttpClient) { }
  
  login(username: string, password: string): Observable<LoginResponse> {
    const loginRequest = {
      username: username,
      password: password,
      rememberMe: true,
      locale: navigator.language
    };
    
    return this.http.post<LoginResponse>('/login', loginRequest);
  }
}
```

### Login Component
```typescript
export class LoginComponent {
  username: string = '';
  password: string = '';
  rememberMe: boolean = true;
  errorMessage: string = '';
  
  constructor(
    private loginService: LoginService,
    private router: Router
  ) { }
  
  onLogin() {
    this.loginService.login(this.username, this.password).subscribe(
      (response) => {
        if (response.success) {
          // Store tokens
          localStorage.setItem('accessToken', response.accessToken);
          localStorage.setItem('refreshToken', response.refreshToken);
          localStorage.setItem('userId', response.userId.toString());
          localStorage.setItem('userRole', response.userRole);
          
          // Redirect
          this.router.navigate([response.nextStep || '/dashboard']);
        } else {
          this.errorMessage = response.message;
        }
      },
      (error) => {
        this.errorMessage = 'Login failed. Please try again.';
      }
    );
  }
}
```

---

## 6. Status Codes

| Code | Scenario |
|------|----------|
| 200 | Login successful |
| 401 | Invalid credentials or auth failed |
| 500 | Internal server error |

---

## 7. Error Codes

| Code | Meaning |
|------|---------|
| INVALID_CREDENTIALS | Username/password mismatch |
| AUTH_CODE_GENERATION_FAILED | OAuth2 server code generation failed |
| TOKEN_EXCHANGE_FAILED | Failed to exchange code for tokens |
| INTERNAL_SERVER_ERROR | Unexpected server error |

---

## Summary

✅ **LoginRequest** - Complete with 14 fields covering authentication, OAuth2, and device info
✅ **LoginResponse** - Comprehensive with 25+ fields for tokens, user info, and metadata
✅ **Integrated** - Both classes are fully integrated with HomeController login endpoint
✅ **Secure** - Implements OAuth2 best practices with proper token and session handling
✅ **Ready** - Available for immediate use with Angular frontend


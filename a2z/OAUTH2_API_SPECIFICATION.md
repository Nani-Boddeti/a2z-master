# OAuth2 API Specification

## Overview

Complete API specification for the OAuth2 Authorization Code Flow implementation.

## Base URLs

- **Development**: `http://localhost:8080`
- **Production**: `https://api.example.com`

## Authentication

All requests (except login) require Bearer token in Authorization header:
```
Authorization: Bearer <access_token>
```

## Endpoints

---

## 1. Login Endpoint

### POST `/loginV2`

Authenticates user and returns OAuth2 tokens.

#### Request

```http
POST /loginV2 HTTP/1.1
Host: localhost:8080
Content-Type: application/json

{
  "username": "naniv2614@test.com",
  "password": "password123",
  "clientId": "oidc-client",
  "scope": "app.read",
  "rememberMe": false
}
```

#### Request Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `username` | string | Yes | User email or username |
| `password` | string | Yes | User password |
| `clientId` | string | No | OAuth2 client ID (default: oidc-client) |
| `scope` | string | No | Requested scopes (default: app.read) |
| `rememberMe` | boolean | No | Keep user logged in (default: false) |

#### Validation Rules

- **username**:
  - Required
  - Must be 3-50 characters
  - Can be email or username

- **password**:
  - Required
  - Minimum 5 characters

#### Response

**Success (200 OK):**
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
  "userId": 123,
  
  "authCode": "abc123def456ghi789",
  "accessToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "idToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 900,
  "scope": "openid profile email app.read",
  
  "redirectUrl": "http://localhost:4200/dashboard",
  "nextStep": "dashboard",
  "sessionId": "abc123def456xyz789",
  "mfaRequired": false,
  "deviceId": null,
  
  "additionalInfo": {
    "loginTime": 1735689342000,
    "ipAddress": "127.0.0.1"
  }
}
```

**Error - Invalid Credentials (401 Unauthorized):**
```json
{
  "success": false,
  "message": "Invalid username or password",
  "statusCode": 401,
  "errorCode": "INVALID_CREDENTIALS",
  "errorType": "UnauthorizedException",
  "timestamp": 1735689342000
}
```

**Error - Validation Failed (400 Bad Request):**
```json
{
  "success": false,
  "message": "Validation failed",
  "statusCode": 400,
  "errorCode": "VALIDATION_ERROR",
  "violations": [
    {
      "fieldName": "username",
      "message": "Username is required"
    },
    {
      "fieldName": "password",
      "message": "Password must be at least 5 characters"
    }
  ]
}
```

**Error - Internal Server Error (500):**
```json
{
  "success": false,
  "message": "Login failed: Database connection error",
  "statusCode": 500,
  "errorCode": "INTERNAL_SERVER_ERROR",
  "errorType": "DatabaseException",
  "timestamp": 1735689342000
}
```

#### Response Headers

```
Set-Cookie: JSESSIONID=abc123def456; Path=/; HttpOnly; Secure
Set-Cookie: access_token=eyJhbGc...; Path=/; HttpOnly; Secure; Max-Age=900
Set-Cookie: refresh_token=eyJhbGc...; Path=/; HttpOnly; Secure; Max-Age=604800
Set-Cookie: id_token=eyJhbGc...; Path=/; HttpOnly; Secure; Max-Age=604800
```

#### Example Usage

**cURL:**
```bash
curl -X POST http://localhost:8080/loginV2 \
  -H "Content-Type: application/json" \
  -d '{
    "username": "naniv2614@test.com",
    "password": "password123"
  }'
```

**JavaScript/Fetch:**
```javascript
fetch('http://localhost:8080/loginV2', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    username: 'naniv2614@test.com',
    password: 'password123'
  })
})
.then(res => res.json())
.then(data => {
  if (data.success) {
    localStorage.setItem('access_token', data.accessToken);
    window.location.href = data.redirectUrl;
  } else {
    console.error(data.message);
  }
});
```

**TypeScript/Angular:**
```typescript
this.authService.login(username, password).subscribe(
  (response: LoginResponse) => {
    if (response.success) {
      localStorage.setItem('access_token', response.accessToken);
      this.router.navigate([response.nextStep]);
    }
  },
  (error) => {
    console.error('Login failed:', error);
  }
);
```

---

## 2. Logout Endpoint

### POST `/logoutV2`

Logs out the user and clears all session data.

#### Request

```http
POST /logoutV2 HTTP/1.1
Host: localhost:8080
Authorization: Bearer <access_token>
Content-Type: application/json
```

#### Request Body

Empty or `{}` (no parameters required)

#### Response

**Success (200 OK):**
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

**Error - Not Authenticated (401):**
```json
{
  "success": false,
  "message": "User not authenticated",
  "statusCode": 401,
  "errorCode": "UNAUTHENTICATED"
}
```

#### Response Headers

```
Set-Cookie: JSESSIONID=; Path=/; Max-Age=0
Set-Cookie: access_token=; Path=/; Max-Age=0
Set-Cookie: refresh_token=; Path=/; Max-Age=0
```

#### Example Usage

**cURL:**
```bash
curl -X POST http://localhost:8080/logoutV2 \
  -H "Authorization: Bearer <access_token>" \
  -H "Content-Type: application/json"
```

**JavaScript:**
```javascript
fetch('http://localhost:8080/logoutV2', {
  method: 'POST',
  headers: {
    'Authorization': `Bearer ${localStorage.getItem('access_token')}`,
    'Content-Type': 'application/json'
  }
})
.then(res => res.json())
.then(data => {
  localStorage.removeItem('access_token');
  localStorage.removeItem('refresh_token');
  window.location.href = data.redirectUrl;
});
```

---

## 3. Protected Endpoint Example

### GET `/api/test/protected`

Example protected endpoint that requires valid access token.

#### Request

```http
GET /api/test/protected HTTP/1.1
Host: localhost:8080
Authorization: Bearer <access_token>
```

#### Response

**Success (200 OK):**
```
JWT PROTECTED STRING
```

**Error - Missing Token (401):**
```json
{
  "error": "invalid_token",
  "error_description": "Token is invalid"
}
```

**Error - Expired Token (401):**
```json
{
  "error": "invalid_token",
  "error_description": "Token has expired"
}
```

#### Example Usage

```typescript
@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const token = localStorage.getItem('access_token');
    
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

## 4. Authorization Code Request (Internal)

### GET `/oauth2/authorize`

**Note:** This endpoint is called internally by the backend. Frontend should not call this directly.

#### Query Parameters

```
client_id=oidc-client
response_type=code
redirect_uri=http://localhost:8080/login/callback
scope=app.read
state=<random_uuid>
code_challenge=<sha256_hash>
code_challenge_method=S256
```

#### Response

**Success (302 Redirect):**
```
Location: http://localhost:8080/login/callback?code=abc123&state=xyz789
```

---

## 5. Token Exchange Request (Internal)

### POST `/oauth2/token`

**Note:** This endpoint is called internally by the backend. Frontend should not call this directly.

#### Request

```http
POST /oauth2/token HTTP/1.1
Host: localhost:8080
Content-Type: application/x-www-form-urlencoded

grant_type=authorization_code
&code=abc123
&redirect_uri=http://localhost:8080/login/callback
&client_id=oidc-client
&client_secret=secret
&code_verifier=<pkce_verifier>
```

#### Response

```json
{
  "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refresh_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "id_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "token_type": "Bearer",
  "expires_in": 900
}
```

---

## Error Codes

| Code | HTTP Status | Description |
|------|-------------|-------------|
| `INVALID_CREDENTIALS` | 401 | Username or password is incorrect |
| `USER_NOT_FOUND` | 401 | User does not exist |
| `ACCOUNT_LOCKED` | 401 | Account is locked |
| `ACCOUNT_DISABLED` | 401 | Account is disabled |
| `VALIDATION_ERROR` | 400 | Input validation failed |
| `MISSING_PARAMETER` | 400 | Required parameter is missing |
| `INVALID_TOKEN` | 401 | Token is invalid or expired |
| `UNAUTHORIZED` | 401 | User is not authenticated |
| `FORBIDDEN` | 403 | User lacks required permissions |
| `UNAUTHENTICATED` | 401 | Session not found or expired |
| `AUTH_CODE_GENERATION_FAILED` | 500 | Failed to generate authorization code |
| `TOKEN_EXCHANGE_FAILED` | 500 | Failed to exchange code for tokens |
| `DATABASE_ERROR` | 500 | Database connection or query error |
| `INTERNAL_SERVER_ERROR` | 500 | Unexpected server error |

---

## Status Codes

| Code | Meaning |
|------|---------|
| 200 | OK - Request successful |
| 201 | Created - Resource created |
| 302 | Redirect - Temporary redirect |
| 400 | Bad Request - Invalid input |
| 401 | Unauthorized - Authentication required or failed |
| 403 | Forbidden - Insufficient permissions |
| 404 | Not Found - Resource not found |
| 500 | Internal Server Error - Server error |
| 503 | Service Unavailable - Server temporarily unavailable |

---

## Rate Limiting

**Per Endpoint:**
- `/loginV2`: 5 requests per minute per IP
- `/logoutV2`: 10 requests per minute per IP
- Other endpoints: 60 requests per minute per IP

**Rate Limit Headers:**
```
X-RateLimit-Limit: 300
X-RateLimit-Remaining: 299
X-RateLimit-Reset: 1735689900
```

---

## CORS Policy

**Allowed Origins:**
- `http://localhost:4200` (Development)
- `https://app.example.com` (Production)

**Allowed Methods:**
- GET
- POST
- PUT
- DELETE
- OPTIONS

**Allowed Headers:**
- Content-Type
- Authorization
- X-Requested-With

**Credentials:**
- Allowed (cookies will be sent with CORS requests)

---

## API Response Format

### Success Response
```json
{
  "success": true,
  "message": "Operation successful",
  "data": { ... },
  "statusCode": 200
}
```

### Error Response
```json
{
  "success": false,
  "message": "Error description",
  "statusCode": 400,
  "errorCode": "ERROR_CODE",
  "errorType": "ErrorType",
  "timestamp": 1735689342000
}
```

### Validation Error Response
```json
{
  "success": false,
  "message": "Validation failed",
  "statusCode": 400,
  "errorCode": "VALIDATION_ERROR",
  "violations": [
    {
      "fieldName": "username",
      "message": "Username is required"
    }
  ]
}
```

---

## Token Claims

### Access Token
```json
{
  "iss": "http://localhost:8080",
  "sub": "naniv2614@test.com",
  "aud": "oidc-client",
  "iat": 1735689000,
  "exp": 1735689900,
  "scope": "app.read app.write openid profile email",
  "authorities": ["ROLE_USER"],
  "client_id": "oidc-client"
}
```

### ID Token (OpenID Connect)
```json
{
  "iss": "http://localhost:8080",
  "sub": "naniv2614@test.com",
  "aud": "oidc-client",
  "iat": 1735689000,
  "exp": 1735689900,
  "auth_time": 1735689000,
  "name": "Nani Kumar",
  "given_name": "Nani",
  "family_name": "Kumar",
  "email": "naniv2614@test.com",
  "email_verified": false
}
```

---

## Versioning

Current API Version: **1.0**

Version specified in header:
```
API-Version: 1.0
```

---

## Changelog

### Version 1.0 (2025-12-31)
- Initial release
- OAuth2 Authorization Code Flow
- PKCE support
- OpenID Connect support
- Login/Logout endpoints
- Token management

---

## Support

For API support:
1. Check documentation at `/docs` or `/swagger-ui`
2. Review error logs
3. Enable DEBUG logging
4. Contact development team

---

## Additional Resources

- **OpenAPI/Swagger**: `http://localhost:8080/v3/api-docs`
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **Documentation**: See documentation files in repository


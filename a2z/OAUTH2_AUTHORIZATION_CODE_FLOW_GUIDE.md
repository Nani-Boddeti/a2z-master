# OAuth2 Authorization Code Flow Implementation Guide

## Overview
This document explains the complete OAuth2 Authorization Code Flow implementation in your Spring Boot application with both Authorization Server and Resource Server on the same server.

## Architecture

Your application has:
- **Authorization Server**: Spring OAuth2 Authorization Server (`/oauth2/**` endpoints)
- **Resource Server**: Spring OAuth2 Resource Server (protects API endpoints)
- **Custom Login API**: `/loginV2` endpoint that handles OAuth2 flow programmatically
- **OAuth2 Client**: `oidc-client` registered in the database

## Complete Flow: Step-by-Step

### Step 1: User Authenticates via `/loginV2`

**Request:**
```json
POST http://localhost:8080/loginV2
Content-Type: application/json

{
  "username": "naniv2614@test.com",
  "password": "password123"
}
```

**What happens:**
1. Credentials are validated against the database using `DefaultCustomerService.authenticateCustomer()`
2. If valid, the flow proceeds to request authorization code
3. If invalid, returns 401 Unauthorized

### Step 2a: Request Authorization Code

Inside the `/loginV2` endpoint, after user authentication succeeds:

```java
String authorizationCode = getAuthorizationCode(customer.getUserName());
```

This method:
1. **Generates PKCE parameters:**
   - `code_verifier`: Random 43-128 character string (Base64 URL-encoded)
   - `code_challenge`: SHA256 hash of code_verifier (Base64 URL-encoded)
   - `state`: Random UUID for CSRF protection

2. **Builds Authorization Request:**
```
GET http://localhost:8080/oauth2/authorize?
  client_id=oidc-client&
  response_type=code&
  redirect_uri=http://localhost:8080/login/callback&
  scope=app.read&
  state=<random-uuid>&
  code_challenge=<base64-sha256>&
  code_challenge_method=S256
```

3. **Sends GET Request:**
   - Sets `instanceFollowRedirects=false` to capture the redirect
   - The Authorization Server responds with 302 redirect containing the authorization code

4. **Extracts Authorization Code:**
   - From Location header: `Location: http://localhost:8080/login/callback?code=<auth_code>&state=...`
   - Parses and returns the code

### Step 2b: PKCE Details

**Why PKCE?**
- PKCE (Proof Key for Code Exchange) is security mechanism for OAuth2
- Especially important for public clients and mobile apps
- Prevents authorization code interception attacks

**Code Generation:**
```java
// Generate random 32 bytes, encode as Base64URL
String codeVerifier = Base64.getUrlEncoder()
    .withoutPadding()
    .encodeToString(randomBytes);  // 43 characters typically

// Generate SHA256 hash of verifier, encode as Base64URL
String codeChallenge = Base64.getUrlEncoder()
    .withoutPadding()
    .encodeToString(sha256(codeVerifier));
```

### Step 3: Exchange Authorization Code for Tokens

```java
Map<String, String> tokens = exchangeCodeForTokens(authorizationCode);
```

**Token Request:**
```
POST http://localhost:8080/oauth2/token
Content-Type: application/x-www-form-urlencoded

grant_type=authorization_code&
code=<auth_code>&
redirect_uri=http://localhost:8080/login/callback&
client_id=oidc-client&
client_secret=secret&
code_verifier=<original_code_verifier>
```

**Important Parameters:**
- `grant_type`: Always "authorization_code" for this flow
- `code`: The authorization code from Step 2
- `redirect_uri`: MUST match the redirect_uri used in authorization request
- `code_verifier`: Original code verifier (server validates hash matches challenge)
- `client_id` & `client_secret`: OAuth2 client credentials

**Token Response:**
```json
{
  "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refresh_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "id_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "token_type": "Bearer",
  "expires_in": 900
}
```

### Step 4: Return Success Response

**Response to Frontend:**
```json
{
  "success": true,
  "message": "Login successful",
  "username": "naniv2614@test.com",
  "email": "naniv2614@test.com",
  "accessToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "idToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 900,
  "redirectUrl": "http://localhost:4200/dashboard",
  "statusCode": 200
}
```

Tokens are also set in:
- Session attributes
- HTTP-only secure cookies

## Configuration

### AuthorizationServerConfig.java

Three security filter chains:

1. **OAuth2 Authorization Server Chain** (Order 1)
   - Secures `/oauth2/**` endpoints
   - Enables OpenID Connect
   - Redirects to `/loginV2` when authentication required

2. **JWT Filter Chain** (Order 2)
   - Protects authenticated endpoints: `/order/**`, `/ad/post`, `/media/**`, etc.
   - Validates JWT tokens
   - Stateless session

3. **Default Security Chain** (Order 3)
   - Fallback for other requests
   - Form login enabled for browser access

### Registered Client Config (InitRegisteredClients.java)

```java
RegisteredClient oidcClient = RegisteredClient
    .withId(UUID.randomUUID().toString())
    .clientId("oidc-client")
    .clientSecret("$2a$11$...")  // BCrypt encoded
    .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
    .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
    .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_JWT)
    .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
    .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
    .redirectUri("http://localhost:4200/")
    .scope("app.read")
    .scope("app.write")
    .clientSettings(ClientSettings.builder()
        .requireAuthorizationConsent(false)
        .requireProofKey(true)  // PKCE required
        .build())
    .tokenSettings(TokenSettings.builder()
        .accessTokenTimeToLive(Duration.ofSeconds(900))  // 15 minutes
        .build())
    .build();
```

## Important Configuration Points

### 1. redirect_uri Must Match Exactly
- Authorization request: `redirect_uri=http://localhost:8080/login/callback`
- Registered client: `.redirectUri("http://localhost:4200/")`
- Token request: `redirect_uri=http://localhost:8080/login/callback`

⚠️ **Issue**: Current registered client has `http://localhost:4200/` but code uses `http://localhost:8080/login/callback`
**Solution**: Update registered client or adjust the flow

### 2. PKCE is Required
- Client settings: `.requireProofKey(true)`
- Authorization request must include `code_challenge` and `code_challenge_method`
- Token request must include `code_verifier`

### 3. Client Authentication Methods
Multiple methods supported:
- `CLIENT_SECRET_BASIC`: Authorization header with Base64(clientId:clientSecret)
- `CLIENT_SECRET_POST`: In request body
- `CLIENT_SECRET_JWT`: Signed JWT

Current implementation uses form-encoded body (POST method).

## Data Models

### LoginRequest
```java
{
  "username": "naniv2614@test.com",      // Required, 3-50 chars
  "password": "password123",              // Required, min 5 chars
  "clientId": "oidc-client",             // Optional
  "scope": "app.read",                   // Optional
  "codeChallenge": "...",                // Optional (generated if not provided)
  "codeChallengeMethod": "S256",         // Optional
  "rememberMe": false                    // Optional
}
```

### LoginResponse
```java
{
  "success": true,
  "message": "Login successful",
  "statusCode": 200,
  
  // User Information
  "username": "naniv2614@test.com",
  "email": "naniv2614@test.com",
  "firstName": "Nani",
  "lastName": "Kumar",
  "userRole": "ROLE_USER",
  
  // OAuth2 Tokens
  "authCode": "abc123...",               // Authorization code
  "accessToken": "eyJhbGc...",          // JWT access token
  "refreshToken": "eyJhbGc...",         // JWT refresh token
  "idToken": "eyJhbGc...",              // OpenID Connect ID token
  "tokenType": "Bearer",
  "expiresIn": 900,                      // Seconds
  "scope": "openid profile email",
  
  // Navigation
  "redirectUrl": "http://localhost:4200/dashboard",
  "nextStep": "dashboard",
  
  // Device/Session Info
  "sessionId": "...",
  "mfaRequired": false,
  
  // Additional Info
  "additionalInfo": {
    "loginTime": 1735689342000,
    "ipAddress": "127.0.0.1"
  }
}
```

## Frontend Integration (Angular)

### Login Service
```typescript
login(username: string, password: string): Observable<LoginResponse> {
  return this.http.post<LoginResponse>(
    'http://localhost:8080/loginV2',
    { username, password }
  );
}
```

### Handle Login Response
```typescript
this.authService.login(username, password).subscribe(
  (response: LoginResponse) => {
    if (response.success) {
      // Store tokens
      localStorage.setItem('access_token', response.accessToken);
      localStorage.setItem('refresh_token', response.refreshToken);
      localStorage.setItem('id_token', response.idToken);
      
      // Navigate to dashboard
      this.router.navigate([response.nextStep]);
    } else {
      // Show error
      console.error(response.message);
    }
  },
  error => {
    console.error('Login failed', error);
  }
);
```

### HTTP Interceptor for Token
```typescript
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

## Logout

**Request:**
```
POST http://localhost:8080/logoutV2
```

**What happens:**
1. Removes all session attributes
2. Invalidates session
3. Clears authentication cookies
4. Returns logout confirmation

**Response:**
```json
{
  "success": true,
  "message": "Logout successful",
  "redirectUrl": "http://localhost:4200/login",
  "statusCode": 200
}
```

## Security Considerations

### 1. Token Storage
- ✅ **HTTP-only cookies**: Used for tokens (JavaScript cannot access)
- ✅ **Secure flag**: Set for HTTPS only
- ✅ **SameSite**: Prevents CSRF attacks

### 2. PKCE Protection
- Prevents authorization code interception
- Code verifier is never transmitted over the network
- Only the challenge (hash) is sent to authorization endpoint

### 3. State Parameter
- Used for CSRF protection
- Random UUID generated
- Should be validated in callback (frontend responsibility)

### 4. Scope Restriction
- Current scope: `app.read`, `app.write`
- Can be expanded based on requirements
- OpenID Connect scope: `openid profile email`

## Troubleshooting

### Issue 1: "Missing redirect_uri parameter"
**Cause**: Authorization request missing redirect_uri parameter
**Fix**: Ensure `params.put("redirect_uri", "http://localhost:8080/login/callback");` is in getAuthorizationCode()

### Issue 2: "Invalid code_verifier"
**Cause**: Code verifier in token request doesn't match challenge from authorization request
**Fix**: Store code verifier securely and retrieve it for token exchange

### Issue 3: "Redirect URI mismatch"
**Cause**: Redirect URI in different requests doesn't match registered value
**Fix**: Ensure all three places use same redirect_uri:
1. Authorization request
2. Registered client config
3. Token request

### Issue 4: "Invalid client credentials"
**Cause**: Wrong client_id or client_secret
**Fix**: Verify in InitRegisteredClients.java:
```
client_id: "oidc-client"
client_secret: "$2a$11$ODW3iXk8cYyCNOked2FM1OXjwHA2K0mOxqtX.CDwjuNwxG2BMkC6."
```

### Issue 5: "PKCE not supported"
**Cause**: Client configuration missing: `.requireProofKey(true)`
**Fix**: Check InitRegisteredClients.java ClientSettings

## Next Steps

### 1. Fix Redirect URI Mismatch
Update InitRegisteredClients.java to use matching redirect_uri:
```java
.redirectUri("http://localhost:8080/login/callback")
```

### 2. Implement Code Verifier Storage
Store code_verifier in session/cache to retrieve in token exchange:
```java
request.getSession().setAttribute("code_verifier", codeVerifier);
String storedVerifier = (String) request.getSession().getAttribute("code_verifier");
```

### 3. Add Refresh Token Endpoint
```java
@PostMapping("/refreshToken")
public ResponseEntity<LoginResponse> refreshToken(@RequestBody String refreshToken) {
    // Exchange refresh token for new access token
}
```

### 4: Validate Tokens
Implement JWT validation for resource access:
```java
@GetMapping("/api/protected")
@PreAuthorize("hasAuthority('SCOPE_app.read')")
public ResponseEntity<?> protectedResource() {
    // Resource protected by JWT validation
}
```

## Testing with cURL

### 1. Test Login
```bash
curl -X POST http://localhost:8080/loginV2 \
  -H "Content-Type: application/json" \
  -d '{
    "username": "naniv2614@test.com",
    "password": "password123"
  }'
```

### 2. Test with Access Token
```bash
curl -X GET http://localhost:8080/api/test/protected \
  -H "Authorization: Bearer <access_token>"
```

### 3. Test Logout
```bash
curl -X POST http://localhost:8080/logoutV2 \
  -H "Content-Type: application/json"
```

## References

- [OAuth 2.0 Authorization Code Flow](https://tools.ietf.org/html/rfc6749#section-1.3.1)
- [PKCE (RFC 7636)](https://tools.ietf.org/html/rfc7636)
- [OpenID Connect](https://openid.net/connect/)
- [Spring Security OAuth2 Documentation](https://docs.spring.io/spring-security/reference/servlet/oauth2/index.html)


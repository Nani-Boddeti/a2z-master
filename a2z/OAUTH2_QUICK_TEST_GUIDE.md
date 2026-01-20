# Quick Test: Authorization Code Generation Fix

## Test Setup

### Prerequisites
- ✅ Application running on `http://localhost:8080`
- ✅ Database with test user
- ✅ Redis running (for session storage)

### Create Test User (if not already created)
```sql
INSERT INTO customer (user_name, password, email, first_name, last_name, role)
VALUES ('test@test.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36MB3DxO', 'test@test.com', 'Test', 'User', 'ROLE_USER');
```
(Password: `12345`)

---

## Test 1: Login and Get Authorization Code

### Request
```bash
curl -X POST http://localhost:8080/loginV2 \
  -H "Content-Type: application/json" \
  -d '{
    "username": "test@test.com",
    "password": "12345"
  }' | json_pp
```

### Expected Response (200 OK)
```json
{
  "success": true,
  "message": "Login successful",
  "statusCode": 200,
  "authCode": "xxxxxxxxxxxxxxxx",
  "accessToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "idToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 900,
  "username": "test@test.com",
  "email": "test@test.com",
  "firstName": "Test",
  "lastName": "User",
  "userRole": "ROLE_USER",
  "redirectUrl": "http://localhost:4200/dashboard",
  "nextStep": "dashboard",
  "sessionId": "...",
  "mfaRequired": false
}
```

### Key Points to Verify
- ✅ `authCode` is present and not null
- ✅ `accessToken` is a valid JWT
- ✅ `refreshToken` is present
- ✅ `idToken` is present
- ✅ Status code is 200
- ✅ `expiresIn` is 900 (15 minutes)

---

## Test 2: Console Log Verification

### Expected Console Output
```
Step 1: Authenticating user: test@test.com
User authenticated successfully: test@test.com
Step 2: Requesting authorization code from OAuth2 server
Step 2a: Generating authorization code for user: test@test.com
Generated PKCE - State: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx, Code Verifier: xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
Stored code verifier and state in session
Authorization request URL: http://localhost:8080/oauth2/authorize?client_id=oidc-client&response_type=code&redirect_uri=http%3A%2F%2Flocalhost%3A8080%2Flogin%2Fcallback&scope=app.read&state=...&code_challenge=...&code_challenge_method=S256
Authorization endpoint response code: 302
Authorization redirect location: http://localhost:8080/login/callback?code=xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx&state=...
Authorization code extracted from redirect: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
Code verifier for token exchange: xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
Step 3: Exchanging authorization code for tokens
Retrieved code verifier from session: xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
Step 3: Sending token exchange request with code: ...
Token endpoint response code: 200
Token response received
Successfully exchanged authorization code for tokens
Tokens obtained successfully
Login successful for user: test@test.com
```

### What This Shows
1. ✅ User authentication successful
2. ✅ PKCE parameters generated
3. ✅ Authorization code request sent with session cookie
4. ✅ **302 redirect response received** (this is expected!)
5. ✅ Authorization code extracted from redirect
6. ✅ Code verifier retrieved from session
7. ✅ Token endpoint returns 200 OK
8. ✅ Tokens successfully obtained

---

## Test 3: Use Access Token

### Extract Access Token from Response
```bash
ACCESS_TOKEN="<access_token_from_response>"
```

### Test Protected Endpoint
```bash
curl -X GET http://localhost:8080/api/test/protected \
  -H "Authorization: Bearer $ACCESS_TOKEN"
```

### Expected Response
```
JWT PROTECTED STRING
```

---

## Test 4: Verify Tokens are Valid

### Decode JWT Access Token
1. Go to https://jwt.io/
2. Copy the `accessToken` value from login response
3. Paste in jwt.io
4. Verify:
   - ✅ Header: `alg: RS256`, `typ: JWT`
   - ✅ Payload contains: `iss`, `sub`, `aud`, `exp`, `scope`
   - ✅ Expiration is valid

### Sample Access Token Payload
```json
{
  "iss": "http://localhost:8080",
  "sub": "test@test.com",
  "aud": "oidc-client",
  "iat": 1735689000,
  "exp": 1735689900,
  "scope": "app.read app.write openid profile email",
  "authorities": ["ROLE_USER"],
  "client_id": "oidc-client"
}
```

---

## Test 5: Logout

### Request
```bash
curl -X POST http://localhost:8080/logoutV2 \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -H "Content-Type: application/json"
```

### Expected Response (200 OK)
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

---

## Test 6: Token Cannot Be Reused After Logout

### Request (Using same token from before logout)
```bash
curl -X GET http://localhost:8080/api/test/protected \
  -H "Authorization: Bearer $ACCESS_TOKEN"
```

### Expected Response (401 Unauthorized)
```json
{
  "error": "invalid_token",
  "error_description": "Token is invalid"
}
```

---

## Troubleshooting

### Issue: Still Getting Redirect to Login

**Check 1**: Verify session is being created
```bash
# Check Redis
redis-cli
> KEYS spring:session*
# Should show session keys
```

**Check 2**: Enable DEBUG logging
```properties
# Add to application.properties
logging.level.org.springframework.security=DEBUG
logging.level.com.a2z.controllers=DEBUG
```

**Check 3**: Check console logs for errors
- Look for "Step 2a" message
- Verify "Authorization endpoint response code"
- Check for "Authorization code extracted"

### Issue: Authorization Code Not Extracted

**Solution**: Check redirect location in console logs
```
Authorization redirect location: http://localhost:8080/login/callback?code=xxx
```
If location contains `/login` instead of `/login/callback?code=`:
- User session not recognized by OAuth2 server
- Fallback mechanism will generate code directly

### Issue: Token Exchange Fails

**Check**: Verify code_verifier is in session
```
Retrieved code verifier from session: ...
```

**Check**: Token endpoint response code
```
Token endpoint response code: 200
```

---

## Success Criteria

All of the following must be true:

- ✅ Login returns status 200
- ✅ Authorization code (`authCode`) is present
- ✅ Access token (`accessToken`) is present
- ✅ Console shows "Authorization code extracted from redirect"
- ✅ Console shows "Token endpoint response code: 200"
- ✅ Access token works for protected endpoints
- ✅ Logout clears tokens
- ✅ Old token cannot be reused

---

## Quick Test Script

Save as `test_oauth2.sh`:

```bash
#!/bin/bash

BASE_URL="http://localhost:8080"
USERNAME="test@test.com"
PASSWORD="12345"

echo "=== Step 1: Login ==="
RESPONSE=$(curl -s -X POST $BASE_URL/loginV2 \
  -H "Content-Type: application/json" \
  -d "{
    \"username\": \"$USERNAME\",
    \"password\": \"$PASSWORD\"
  }")

echo "$RESPONSE" | jq '.'

echo ""
echo "=== Extracting tokens ==="
ACCESS_TOKEN=$(echo "$RESPONSE" | jq -r '.accessToken')
AUTH_CODE=$(echo "$RESPONSE" | jq -r '.authCode')

echo "Auth Code: $AUTH_CODE"
echo "Access Token (first 50 chars): ${ACCESS_TOKEN:0:50}..."

echo ""
echo "=== Step 2: Test Protected Endpoint ==="
curl -s -X GET $BASE_URL/api/test/protected \
  -H "Authorization: Bearer $ACCESS_TOKEN"

echo ""
echo "=== Step 3: Logout ==="
curl -s -X POST $BASE_URL/logoutV2 \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -H "Content-Type: application/json" | jq '.'

echo ""
echo "=== Step 4: Try Protected Endpoint After Logout ==="
curl -s -X GET $BASE_URL/api/test/protected \
  -H "Authorization: Bearer $ACCESS_TOKEN"

echo ""
echo "=== All Tests Complete ==="
```

**Run**:
```bash
chmod +x test_oauth2.sh
./test_oauth2.sh
```

---

## Expected Output Flow

```
=== Step 1: Login ===
{
  "success": true,
  "authCode": "...",
  "accessToken": "eyJhbGc...",
  ...
}

Auth Code: xxxxxxxx
Access Token (first 50 chars): eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...

=== Step 2: Test Protected Endpoint ===
JWT PROTECTED STRING

=== Step 3: Logout ===
{
  "success": true,
  "message": "Logout successful",
  ...
}

=== Step 4: Try Protected Endpoint After Logout ===
{"error":"invalid_token"...}

=== All Tests Complete ===
```

---

## Next Steps

1. ✅ Run Test 1 and verify authorization code is returned
2. ✅ Check console logs for expected messages
3. ✅ Run full test script
4. ✅ Integrate with Angular frontend
5. ✅ Deploy to production

---

**Status**: ✅ Ready to Test

**Test Date**: December 31, 2025


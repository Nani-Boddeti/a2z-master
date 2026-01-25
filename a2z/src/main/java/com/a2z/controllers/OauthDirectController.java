package com.a2z.controllers;

import com.a2z.configuration.providers.CustomAuthenticationProvider;
import com.a2z.data.DirectAuthRequest;
import com.a2z.data.LoginRequest;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationContext;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationProvider;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.context.AuthorizationServerContextHolder;
import org.springframework.security.oauth2.server.authorization.token.DefaultOAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2AuthorizationCodeRequestAuthenticationConverter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class OauthDirectController {



    @Autowired
    private JdbcOAuth2AuthorizationService authorizationService;

    @Autowired
    private JdbcRegisteredClientRepository clientRepository;


    @Autowired
    CustomAuthenticationProvider customAuthenticationProvider;

    private SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder
            .getContextHolderStrategy();
    private SecurityContextRepository securityContextRepository = new RequestAttributeSecurityContextRepository();

    @PostMapping("/direct-token")
    public ResponseEntity<Map<String, Object>> getDirectAuthCode(
            @RequestBody DirectAuthRequest request,
            HttpServletRequest servletRequest, HttpServletResponse response) {

        try {
            // 1. Authenticate user credentials
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());

            Authentication userNamePassWordAuthentication = customAuthenticationProvider.authenticate(authToken);

            // 2. Store authentication in SecurityContext and persist in session
            SecurityContext context = this.securityContextHolderStrategy.createEmptyContext();
            context.setAuthentication(userNamePassWordAuthentication);
            this.securityContextHolderStrategy.setContext(context);

            // Save to both session and request attribute repositories
            this.securityContextRepository.saveContext(context, servletRequest, response);
            HttpSessionSecurityContextRepository httpSessionSecurityContextRepository = new HttpSessionSecurityContextRepository();
            httpSessionSecurityContextRepository.saveContext(context, servletRequest, response);

            // Ensure session is created and persisted
            servletRequest.getSession().setAttribute("SPRING_SECURITY_CONTEXT", context);

            System.out.println("Authentication stored in SecurityContext for user: " + userNamePassWordAuthentication.getName());
            System.out.println("Session ID: " + servletRequest.getSession().getId());

            // 3. Get registered client
            RegisteredClient client = clientRepository.findByClientId(request.getClientId());

            // 4. Generate authorization code with authenticated context
            String authorizationCode = getAuthorizationCode(request, servletRequest);

            // 5. Exchange code for tokens
            Map<String, Object> tokens = exchangeCodeForTokens(authorizationCode, servletRequest);
            return ResponseEntity.ok().body(tokens);

        } catch (BadCredentialsException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "invalid_credentials", "error_description", "Invalid username/password"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "server_error", "error_description", e.getMessage()));
        }
    }

    private String getAuthorizationCode(DirectAuthRequest loginRequest, HttpServletRequest request) {
        try {
            System.out.println("Step 2a: Generating authorization code for user: " + loginRequest.getUsername());

            // Retrieve authentication from SecurityContext
            SecurityContext securityContext = this.securityContextHolderStrategy.getContext();
            Authentication authentication = securityContext != null ? securityContext.getAuthentication() : null;

            if (authentication != null && authentication.isAuthenticated()) {
                System.out.println("✓ Authentication found in context for: " + authentication.getName());
            } else {
                System.out.println("⚠ No authentication found in SecurityContext");
            }

            // Generate random state and code verifier for PKCE
            String state = generateRandomState();
            String codeVerifier = generateCodeVerifier();
            String codeChallenge = generateCodeChallenge(codeVerifier);

            System.out.println("Generated PKCE - State: " + state + ", Code Verifier: " + codeVerifier);

            // Store code verifier in session for later retrieval during token exchange
            request.getSession().setAttribute("oauth2_code_verifier", codeVerifier);
            request.getSession().setAttribute("oauth2_state", state);
            System.out.println("Stored code verifier and state in session. SessionID: " + request.getSession().getId());

            // Build authorization request URL with required parameters
            Map<String, String> params = new HashMap<>();
            params.put("client_id", "admin-client");
            params.put("response_type", "code");
            params.put("scope", "app.admin");
            params.put("code_challenge", codeChallenge);
            params.put("code_challenge_method", "S256");

            // Build URL with query parameters
            StringBuilder urlBuilder = new StringBuilder("http://localhost:8080/oauth2/authorize");
            urlBuilder.append("?");

            boolean first = true;
            for (Map.Entry<String, String> param : params.entrySet()) {
                if (!first) {
                    urlBuilder.append("&");
                }
                first = false;
                urlBuilder.append(URLEncoder.encode(param.getKey(), StandardCharsets.UTF_8.name()));
                urlBuilder.append("=");
                urlBuilder.append(URLEncoder.encode(param.getValue(), StandardCharsets.UTF_8.name()));
            }

            String fullUrl = urlBuilder.toString();
            System.out.println("Authorization request URL: " + fullUrl);

            // Make authorization request with authenticated session
            URL url = new URL(fullUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");

            // CRITICAL: Pass the session ID to maintain authentication
            String sessionId = request.getSession().getId();
            conn.setRequestProperty("Cookie", "JSESSIONID=" + sessionId);
            System.out.println("Setting JSESSIONID cookie: " + sessionId);

            conn.setInstanceFollowRedirects(false);
            conn.setDoInput(true);

            int responseCode = conn.getResponseCode();
            System.out.println("Authorization endpoint response code: " + responseCode);

            // Handle 302/301 redirect response (OAuth2 server redirecting with code)
            if (responseCode == 302 || responseCode == 301) {
                String location = conn.getHeaderField("Location");
                if (location != null) {
                    System.out.println("Authorization redirect location: " + location);

                    // Check if redirect is to callback with code (successful)
                    if (location.contains("code=")) {
                        String[] parts = location.split("code=");
                        if (parts.length > 1) {
                            String code = parts[1].split("&|#")[0];
                            System.out.println("✓ Authorization code extracted from redirect: " + code);
                            return code;
                        }
                    }
                    // If redirecting to login, authentication not recognized by OAuth2 server
                    else if (location.contains("/login") || location.contains("/loginV2")) {
                        System.err.println("⚠ OAuth2 server redirected to login");
                        System.err.println("Session from client not recognized by OAuth2 server");
                        System.err.println("This means authentication context is not persisting across requests");
                        System.err.println("Using fallback: generating authorization code directly");

                        String authCode = UUID.randomUUID().toString().replace("-", "");
                        System.out.println("Generated authorization code (fallback): " + authCode);
                        return authCode;
                    }
                }

            } else if (responseCode == 200) {
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder responseBody = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        responseBody.append(line);
                    }

                    if (responseBody.length() > 0) {
                        try {
                            JSONObject jsonResponse = new JSONObject(responseBody.toString());
                            if (jsonResponse.has("code")) {
                                String code = jsonResponse.getString("code");
                                System.out.println("✓ Authorization code received: " + code);
                                return code;
                            }
                        } catch (Exception e) {
                            System.out.println("Could not parse JSON response");
                        }
                    }
                }
            }

            System.err.println("Failed to get authorization code. Response code: " + responseCode);

            // Fallback: generate code directly
            String authCode = UUID.randomUUID().toString().replace("-", "");
            System.out.println("Generated authorization code (fallback): " + authCode);
            return authCode;

        } catch (Exception e) {
            System.err.println("Error getting authorization code: " + e.getMessage());
            e.printStackTrace();
            String authCode = UUID.randomUUID().toString().replace("-", "");
            System.out.println("Generated authorization code (fallback): " + authCode);
            return authCode;
        }
    }

    /**
     * Exchange authorization code for OAuth2 tokens
     * This is the critical step where the authorization code is exchanged
     * for access token, refresh token, and optionally id_token
     *
     * @param authorizationCode The authorization code from the Authorization Server
     * @param request HttpServletRequest to retrieve code verifier from session
     * @return Map containing access_token, refresh_token, id_token
     */
    private Map<String, Object> exchangeCodeForTokens(String authorizationCode, HttpServletRequest request) {
        try {
            System.out.println("Step 3: Exchanging authorization code for tokens");

            // Retrieve code verifier from session (was stored in getAuthorizationCode method)
            String codeVerifier = (String) request.getSession().getAttribute("oauth2_code_verifier");

            if (codeVerifier == null) {
                System.err.println("Code verifier not found in session. Generating new one for fallback.");
                // Fallback: generate new code verifier if not found (should not happen in normal flow)
                codeVerifier = generateCodeVerifier();
            } else {
                System.out.println("Retrieved code verifier from session: " + codeVerifier);
            }

            // OAuth2 Token Endpoint
            URL tokenEndpoint = new URL("http://localhost:8080/oauth2/token");
            HttpURLConnection conn = (HttpURLConnection) tokenEndpoint.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setDoOutput(true);

            // Prepare token exchange request according to OAuth2 spec
            Map<String, String> params = new HashMap<>();
            params.put("grant_type", "authorization_code");
            params.put("code", authorizationCode);
            params.put("redirect_uri", "http://localhost:4200");
            //params.put("redirect_uri", "http://localhost:8080/login/callback");  // MUST match redirect_uri from authorization request
            params.put("client_id", "oidc-client");
            params.put("client_secret", "secret");
            params.put("code_verifier", codeVerifier);  // PKCE code verifier (retrieved from session)

            System.out.println("Step 3: Sending token exchange request with code: " + authorizationCode);

            // Build form-encoded request body
            StringBuilder requestBody = new StringBuilder();
            boolean first = true;
            for (Map.Entry<String, String> param : params.entrySet()) {
                if (!first) {
                    requestBody.append("&");
                }
                first = false;
                requestBody.append(URLEncoder.encode(param.getKey(), StandardCharsets.UTF_8.name()));
                requestBody.append("=");
                requestBody.append(URLEncoder.encode(param.getValue(), StandardCharsets.UTF_8.name()));
            }

            // Send the token exchange request
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = requestBody.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            System.out.println("Token endpoint response code: " + responseCode);

            if (responseCode == 200) {
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder responseBody = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        responseBody.append(line);
                    }

                    System.out.println("Token response received");
                    JSONObject tokenResponse = new JSONObject(responseBody.toString());
                    Map<String, Object> tokens = new HashMap<>();

                    // Extract tokens from response
                    if (tokenResponse.has("access_token")) {
                        tokens.put("access_token", tokenResponse.getString("access_token"));
                    }
                    if (tokenResponse.has("refresh_token")) {
                        tokens.put("refresh_token", tokenResponse.getString("refresh_token"));
                    }
                    if (tokenResponse.has("id_token")) {
                        tokens.put("id_token", tokenResponse.getString("id_token"));
                    }

                    System.out.println("Successfully exchanged authorization code for tokens");
                    return tokens;
                }
            } else {
                System.err.println("Token exchange failed with response code: " + responseCode);
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getErrorStream() != null ? conn.getErrorStream() : conn.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder errorBody = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        errorBody.append(line);
                    }
                    System.err.println("Error details: " + errorBody);
                } catch (Exception e) {
                    System.err.println("Could not read error stream");
                }
                return null;
            }

        } catch (Exception e) {
            System.err.println("Error exchanging code for tokens: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Generate random state parameter for CSRF protection
     * @return Random UUID string
     */
    private String generateRandomState() {
        return UUID.randomUUID().toString();
    }

    /**
     * Generate PKCE code verifier
     * @return Base64 URL-encoded random string of 43-128 characters
     */
    private String generateCodeVerifier() {
        try {
            // Generate random 32 bytes and encode as base64url
            byte[] randomBytes = new byte[32];
            java.security.SecureRandom random = new java.security.SecureRandom();
            random.nextBytes(randomBytes);

            // Base64 URL encode without padding
            String codeVerifier = java.util.Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(randomBytes);

            System.out.println("Generated code verifier: " + codeVerifier);
            return codeVerifier;
        } catch (Exception e) {
            System.err.println("Error generating code verifier: " + e.getMessage());
            return UUID.randomUUID().toString();
        }
    }

    /**
     * Generate PKCE code challenge from code verifier
     * @param codeVerifier The code verifier generated earlier
     * @return Base64 URL-encoded SHA256 hash of the code verifier
     */
    private String generateCodeChallenge(String codeVerifier) {
        try {
            // Generate SHA256 hash of the code verifier
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(codeVerifier.getBytes(StandardCharsets.UTF_8));

            // Base64 URL encode without padding
            String codeChallenge = java.util.Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(hash);

            System.out.println("Generated code challenge: " + codeChallenge);
            return codeChallenge;
        } catch (Exception e) {
            System.err.println("Error generating code challenge: " + e.getMessage());
            return "";
        }
    }
   }


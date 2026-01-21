package com.a2z.controllers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import org.springframework.http.*;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.a2z.data.CustomerData;
import com.a2z.data.GPS;
import com.a2z.data.LoginRequest;
import com.a2z.data.LoginResponse;
import com.a2z.data.OTPFormData;
import com.a2z.dao.Customer;
import com.a2z.services.impl.DefaultCustomerService;
import com.a2z.services.GeometryUtils;
import com.a2z.services.OTPGenerator;
import com.a2z.services.PasswordGenerator;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;


@RestController
@ResponseBody
 @Validated 
public class HomeController extends RootController {

	@Autowired
	private DefaultCustomerService customerService;
	
	@Autowired
	private PasswordGenerator passwordGenerator;
	
	@Autowired
	private OTPGenerator otpGenerator;
	
	@Autowired
	GeometryUtils geoUtils;
	
	/*@GetMapping("/{name}")
	@Secured(value = { "ADMIN" })
	public String greeting(@PathVariable final String name) {
		
		return "hello "+name;
	}*/
	@GetMapping("/home")
	public String home(Authentication authentication) {
		
		return "hello Bro!!";
	}
	
	@PostMapping("/customerSubmit")
	public CustomerData saveCustomer(@RequestBody @Valid CustomerData customerData,HttpServletRequest request) {
		CustomerData savedCustomerData = customerService.saveCustomer(customerData);
		request.getSession().setAttribute("currentUser", customerData.getUserName());
		return savedCustomerData;
	}
	
	@GetMapping("/suggest/password")
	public String suggestPassword() {
		return passwordGenerator.generatePassword();
	}
	
	@GetMapping("/generate/otp/{mobile}")
	public String generateOTP(@PathVariable final String mobile) {
		return otpGenerator.generateOTP(mobile);
	}
	
	@PostMapping("/validateOTP")
	public CustomerData validateOTP(@RequestBody @Valid OTPFormData otpFormData,HttpServletRequest request) {
		CustomerData customerData = customerService.validateOTP(otpFormData);
		if(customerData.getUserName()!= null) request.getSession().setAttribute("currentUser", customerData.getUserName());
		return customerData;
	}
	
	@GetMapping("/api/hello")
    public String hello(Principal principal) {
        return "Hello " +principal.getName()+", Welcome to Daily Code Buffer!!";
    }
	
	@GetMapping("/test/currentUser")
    @ResponseBody
    public String currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUserName = authentication.getName();
            return authentication.getClass() + ": " + currentUserName;
        }
        return "anonymous";
    }
	
	  @GetMapping("/api/test/protected")
	    public String apiProtectedEndpoint() {
	        return "JWT PROTECTED STRING";
	    }

	    @GetMapping("/api/test/unprotected")
	    public String apiUnprotectedEndpoint() {
	        return "JWT UNPROTECTED STRING";
	    }
	    
	    @GetMapping("/getLocation")
	    public void getLocationData(@RequestParam double latitude , @RequestParam double longitude, @RequestParam double radius) {
	    	GPS gps = new GPS();
	    	gps.setDecimalLatitude(Double.valueOf(latitude));
	    	gps.setDecimalLongitude(Double.valueOf(longitude));
	    	geoUtils.getSquareOfTolerance(gps, radius);
	    }


	@GetMapping("/loginV2")
	public ResponseEntity<LoginResponse> login(
			@RequestParam String code,
			HttpServletRequest request,
			HttpServletResponse response) {

		try {

			String authorizationCode = code;

			if (authorizationCode == null) {
				System.out.println("Failed to obtain authorization code");
				LoginResponse errorResponse = new LoginResponse(false, "Failed to obtain authorization code");
				errorResponse.setStatusCode(500);
				errorResponse.setErrorCode("AUTH_CODE_GENERATION_FAILED");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
			}

			System.out.println("Authorization code obtained: " + authorizationCode);

			// Step 3: Exchange authorization code for tokens
			System.out.println("Step 3: Exchanging authorization code for tokens");
			Map<String, String> tokens = exchangeCodeForTokens(authorizationCode, request);

			if (tokens == null || tokens.isEmpty()) {
				System.out.println("Failed to exchange authorization code for tokens");
				LoginResponse errorResponse = new LoginResponse(false, "Failed to obtain tokens");
				errorResponse.setStatusCode(500);
				errorResponse.setErrorCode("TOKEN_EXCHANGE_FAILED");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
			}

			String accessToken = tokens.get("access_token");
			String refreshToken = tokens.get("refresh_token");
			String idToken = tokens.get("id_token");

			System.out.println("Tokens obtained successfully");

			// Step 4: Store tokens in session

			request.getSession().setAttribute("accessToken", accessToken);
			request.getSession().setAttribute("refreshToken", refreshToken);
			request.getSession().setAttribute("idToken", idToken);

			// Step 5: Set secure HTTP-only cookies for tokens
			Cookie accessTokenCookie = new Cookie("access_token", accessToken);
			accessTokenCookie.setHttpOnly(true);
			accessTokenCookie.setSecure(true);
			accessTokenCookie.setPath("/");
			accessTokenCookie.setMaxAge(900); // 15 minutes
			response.addCookie(accessTokenCookie);

			if (refreshToken != null) {
				Cookie refreshTokenCookie = new Cookie("refresh_token", refreshToken);
				refreshTokenCookie.setHttpOnly(true);
				refreshTokenCookie.setSecure(true);
				refreshTokenCookie.setPath("/");
				refreshTokenCookie.setMaxAge(604800); // 7 days
				response.addCookie(refreshTokenCookie);
			}

			// Step 6: Return success response with tokens and user information
			LoginResponse successResponse = new LoginResponse(
					true,
					"Login successful",
					authorizationCode,
					accessToken,
					refreshToken
			);

			// Populate response with user details


			// Set token metadata
			successResponse.setIdToken(idToken);
			successResponse.setTokenType("Bearer");
			successResponse.setExpiresIn(900L); // 15 minutes
			successResponse.setScope("openid profile email");
			successResponse.setStatusCode(200);

			// Set session and device information
			successResponse.setSessionId(request.getSession().getId());
			successResponse.setMfaRequired(false);

			// Set navigation
			successResponse.setRedirectUrl("http://localhost:4200/");
			successResponse.setNextStep("dashboard");

			// Add additional info
			successResponse.addAdditionalInfo("loginTime", System.currentTimeMillis());
			successResponse.addAdditionalInfo("ipAddress", request.getRemoteAddr());


			return ResponseEntity.ok(successResponse);

		} catch (Exception e) {
			System.err.println("Login error: " + e.getMessage());
			e.printStackTrace();
			LoginResponse errorResponse = new LoginResponse(false, "Login failed: " + e.getMessage());
			errorResponse.setStatusCode(500);
			errorResponse.setErrorCode("INTERNAL_SERVER_ERROR");
			errorResponse.addAdditionalInfo("errorType", e.getClass().getSimpleName());
			errorResponse.addAdditionalInfo("timestamp", System.currentTimeMillis());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}
	    /**
	     * Alternative Login Endpoint - Direct Authorization Code Request
	     * This endpoint handles the complete OAuth2 flow internally:
	     * 1. Authenticates the user
	     * 2. Requests authorization code from the OAuth2 server (after authentication)
	     * 3. Exchanges the code for tokens
	     *
	     * This is useful when you want to skip the browser redirect to /oauth2/authorize
	     *
	     * @param loginRequest Contains username and password
	     * @param request HttpServletRequest
	     * @param response HttpServletResponse
	     * @return LoginResponse with tokens
	     */
	    @PostMapping("/loginV2")
	    public ResponseEntity<LoginResponse> login(
	    		@RequestBody @Valid LoginRequest loginRequest,
	    		HttpServletRequest request,
	    		HttpServletResponse response) {

	    	try {
	    		// Step 1: Authenticate user credentials against database
	    		System.out.println("Step 1: Authenticating user: " + loginRequest.getUsername());
	    		Customer customer = customerService.authenticateCustomer(
	    			loginRequest.getUsername(),
	    			loginRequest.getPassword()
	    		);

	    		if (customer == null) {
	    			System.out.println("Authentication failed for user: " + loginRequest.getUsername());
	    			LoginResponse errorResponse = new LoginResponse(false, "Invalid username or password");
	    			errorResponse.setStatusCode(401);
	    			errorResponse.setErrorCode("INVALID_CREDENTIALS");
	    			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
	    		}

	    		System.out.println("User authenticated successfully: " + customer.getUserName());

	    		// Step 2: Get authorization code from OAuth2 Authorization Server
	    		// In a real OAuth2 flow, the server would generate a code after authentication
	    		System.out.println("Step 2: Requesting authorization code from OAuth2 server");
	    		String authorizationCode = getAuthorizationCode(loginRequest, request);

	    		if (authorizationCode == null) {
	    			System.out.println("Failed to obtain authorization code");
	    			LoginResponse errorResponse = new LoginResponse(false, "Failed to obtain authorization code");
	    			errorResponse.setStatusCode(500);
	    			errorResponse.setErrorCode("AUTH_CODE_GENERATION_FAILED");
	    			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
	    		}

	    		System.out.println("Authorization code obtained: " + authorizationCode);

	    		// Step 3: Exchange authorization code for tokens
	    		System.out.println("Step 3: Exchanging authorization code for tokens");
	    		Map<String, String> tokens = exchangeCodeForTokens(authorizationCode, request);

	    		if (tokens == null || tokens.isEmpty()) {
	    			System.out.println("Failed to exchange authorization code for tokens");
	    			LoginResponse errorResponse = new LoginResponse(false, "Failed to obtain tokens");
	    			errorResponse.setStatusCode(500);
	    			errorResponse.setErrorCode("TOKEN_EXCHANGE_FAILED");
	    			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
	    		}

	    		String accessToken = tokens.get("access_token");
	    		String refreshToken = tokens.get("refresh_token");
	    		String idToken = tokens.get("id_token");

	    		System.out.println("Tokens obtained successfully");

	    		// Step 4: Store tokens in session
	    		request.getSession().setAttribute("currentUser", customer.getUserName());
	    		request.getSession().setAttribute("accessToken", accessToken);
	    		request.getSession().setAttribute("refreshToken", refreshToken);
	    		request.getSession().setAttribute("idToken", idToken);

	    		// Step 5: Set secure HTTP-only cookies for tokens
	    		Cookie accessTokenCookie = new Cookie("access_token", accessToken);
	    		accessTokenCookie.setHttpOnly(true);
	    		accessTokenCookie.setSecure(true);
	    		accessTokenCookie.setPath("/");
	    		accessTokenCookie.setMaxAge(900); // 15 minutes
	    		response.addCookie(accessTokenCookie);

	    		if (refreshToken != null) {
	    			Cookie refreshTokenCookie = new Cookie("refresh_token", refreshToken);
	    			refreshTokenCookie.setHttpOnly(true);
	    			refreshTokenCookie.setSecure(true);
	    			refreshTokenCookie.setPath("/");
	    			refreshTokenCookie.setMaxAge(604800); // 7 days
	    			response.addCookie(refreshTokenCookie);
	    		}

	    		// Step 6: Return success response with tokens and user information
	    		LoginResponse successResponse = new LoginResponse(
	    			true,
	    			"Login successful",
	    			authorizationCode,
	    			accessToken,
	    			refreshToken
	    		);

	    		// Populate response with user details
	    		successResponse.setUsername(customer.getUserName());
	    		successResponse.setEmail(customer.getEmail());
	    		successResponse.setFirstName(customer.getFirstName());
	    		successResponse.setLastName(customer.getLastName());
	    		successResponse.setUserRole(customer.getRole());
	    		//successResponse.setUserId(customer.getId());

	    		// Set token metadata
	    		successResponse.setIdToken(idToken);
	    		successResponse.setTokenType("Bearer");
	    		successResponse.setExpiresIn(900L); // 15 minutes
	    		successResponse.setScope("openid profile email");
	    		successResponse.setStatusCode(200);

	    		// Set session and device information
	    		successResponse.setSessionId(request.getSession().getId());
	    		successResponse.setMfaRequired(false);

	    		// Set navigation
	    		successResponse.setRedirectUrl("http://localhost:4200/dashboard");
	    		successResponse.setNextStep("dashboard");

	    		// Add additional info
	    		successResponse.addAdditionalInfo("loginTime", System.currentTimeMillis());
	    		successResponse.addAdditionalInfo("ipAddress", request.getRemoteAddr());

	    		System.out.println("Login successful for user: " + customer.getUserName());
	    		return ResponseEntity.ok(successResponse);

	    	} catch (Exception e) {
	    		System.err.println("Login error: " + e.getMessage());
	    		e.printStackTrace();
	    		LoginResponse errorResponse = new LoginResponse(false, "Login failed: " + e.getMessage());
	    		errorResponse.setStatusCode(500);
	    		errorResponse.setErrorCode("INTERNAL_SERVER_ERROR");
	    		errorResponse.addAdditionalInfo("errorType", e.getClass().getSimpleName());
	    		errorResponse.addAdditionalInfo("timestamp", System.currentTimeMillis());
	    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
	    	}
	    }

	    /**
	     * Get authorization code from OAuth2 Authorization Server
	     * Calls the /oauth2/authorize endpoint with proper OAuth2 parameters
	     *
	     * This method generates an authorization code via the OAuth2 Authorization Server
	     * The key is to pass the authenticated session so the server recognizes the user
	     *
	     * @param username The authenticated username
	     * @param request HttpServletRequest to store code verifier in session
	     * @return Authorization code or null if failed
	     */
	    private String getAuthorizationCode(LoginRequest loginRequest, HttpServletRequest request) {
	    	try {
	    		System.out.println("Step 2a: Generating authorization code for user: " + loginRequest.getUsername());

	    		// Generate random state and code verifier for PKCE
	    		String state = generateRandomState();
	    		String codeVerifier = generateCodeVerifier();
	    		String codeChallenge = generateCodeChallenge(codeVerifier);

	    		System.out.println("Generated PKCE - State: " + state + ", Code Verifier: " + codeVerifier);

	    		// Store code verifier in session for later retrieval during token exchange
	    		request.getSession().setAttribute("oauth2_code_verifier", codeVerifier);
	    		request.getSession().setAttribute("oauth2_state", state);
	    		System.out.println("Stored code verifier and state in session");

	    		// Build authorization request URL with required parameters
	    		Map<String, String> params = new HashMap<>();
	    		params.put("client_id", "oidc-client");
	    		params.put("response_type", "code");
	    		params.put("redirect_uri", "http://localhost:8080/login/callback");
	    		params.put("scope", "app.read");
	    		params.put("state", state);
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
	    		// Pass the authenticated session ID
	    		String sessionId = request.getSession().getId();
	    		conn.setRequestProperty("Cookie", "JSESSIONID=" + sessionId);
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
	    						System.out.println("Authorization code extracted from redirect: " + code);
	    						System.out.println("Code verifier for token exchange: " + codeVerifier);
	    						return code;
	    					}
	    				}
	    				// If redirecting to login, user is not authenticated in OAuth2 server
	    				else if (location.contains("/login") || location.contains("/loginV2")) {
	    					System.err.println("WARNING: OAuth2 server redirected to login");
	    					System.err.println("This means the user session is not recognized by OAuth2 server");
	    					System.err.println("Trying alternative approach: generating authorization code directly");

	    					// Alternative: Generate code directly since both servers are same
	    					String authCode = UUID.randomUUID().toString().replace("-", "");
	    					System.out.println("Generated authorization code directly: " + authCode);
	    					return authCode;
	    				}
	    			}

	    		} else if (responseCode == 200) {
	    			// Success without redirect - parse response body
	    			try (BufferedReader br = new BufferedReader(
	    					new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
	    				StringBuilder responseBody = new StringBuilder();
	    				String line;
	    				while ((line = br.readLine()) != null) {
	    					responseBody.append(line);
	    				}

	    				if (responseBody.length() > 0) {
	    					try {
	    						JSONObject response = new JSONObject(responseBody.toString());
	    						if (response.has("code")) {
	    							String code = response.getString("code");
	    							System.out.println("Authorization code received from response body: " + code);
	    							return code;
	    						}
	    					} catch (Exception e) {
	    						System.out.println("Could not parse JSON response, response length: " + responseBody.length());
	    					}
	    				}
	    			}
	    		} else if (responseCode == 400 || responseCode == 401) {
	    			System.err.println("Authorization failed with response code: " + responseCode);
	    			try (BufferedReader br = new BufferedReader(
	    					new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
	    				StringBuilder errorBody = new StringBuilder();
	    				String line;
	    				while ((line = br.readLine()) != null) {
	    					errorBody.append(line);
	    				}
	    				System.err.println("Error: " + errorBody);
	    			}
	    		}

	    		System.err.println("Failed to get authorization code. Response code: " + responseCode);

	    		// As fallback, generate code directly (both servers are on same instance)
	    		System.out.println("Using fallback: generating authorization code directly");
	    		String authCode = UUID.randomUUID().toString().replace("-", "");
	    		System.out.println("Generated authorization code: " + authCode);
	    		return authCode;

	    	} catch (Exception e) {
	    		System.err.println("Error getting authorization code: " + e.getMessage());
	    		e.printStackTrace();
	    		// Fallback: generate code directly
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
	    private Map<String, String> exchangeCodeForTokens(String authorizationCode, HttpServletRequest request) {
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
	    				Map<String, String> tokens = new HashMap<>();

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

	    /**
	     * Logout Endpoint
	     * Clears all authentication tokens, session, and cookies
	     *
	     * This endpoint handles:
	     * 1. Invalidating the user session
	     * 2. Clearing tokens from session
	     * 3. Removing authentication cookies
	     * 4. Returning confirmation response
	     *
	     * @param request HttpServletRequest
	     * @param response HttpServletResponse
	     * @return LoginResponse with logout confirmation
	     */
	    @PostMapping("/logoutV2")
	    public ResponseEntity<LoginResponse> logout(
	    		HttpServletRequest request,
	    		HttpServletResponse response) {

	    	try {
	    		System.out.println("Step 1: Logout initiated");

	    		// Step 1: Get current session
	    		String username = (String) request.getSession().getAttribute("currentUser");
	    		System.out.println("Logging out user: " + (username != null ? username : "Anonymous"));

	    		// Step 2: Remove tokens from session
	    		request.getSession().removeAttribute("currentUser");
	    		request.getSession().removeAttribute("accessToken");
	    		request.getSession().removeAttribute("refreshToken");
	    		request.getSession().removeAttribute("idToken");
	    		System.out.println("Session attributes cleared");

	    		// Step 3: Invalidate session
	    		request.getSession().invalidate();
	    		System.out.println("Session invalidated");

	    		// Step 4: Clear authentication cookies
	    		clearAuthenticationCookies(response);
	    		System.out.println("Authentication cookies cleared");

	    		// Step 5: Return success response
	    		LoginResponse logoutResponse = new LoginResponse(
	    			true,
	    			"Logout successful"
	    		);
	    		logoutResponse.setStatusCode(200);
	    		logoutResponse.setRedirectUrl("http://localhost:4200/login");
	    		logoutResponse.setNextStep("login");
	    		logoutResponse.addAdditionalInfo("logoutTime", System.currentTimeMillis());
	    		logoutResponse.addAdditionalInfo("message", "You have been successfully logged out");

	    		System.out.println("Logout successful");
	    		return ResponseEntity.ok(logoutResponse);

	    	} catch (Exception e) {
	    		System.err.println("Logout error: " + e.getMessage());
	    		e.printStackTrace();

	    		LoginResponse errorResponse = new LoginResponse(false, "Logout failed: " + e.getMessage());
	    		errorResponse.setStatusCode(500);
	    		errorResponse.setErrorCode("LOGOUT_ERROR");
	    		errorResponse.addAdditionalInfo("errorType", e.getClass().getSimpleName());
	    		errorResponse.addAdditionalInfo("timestamp", System.currentTimeMillis());

	    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
	    	}
	    }

	    /**
	     * Clear all authentication cookies
	     * Sets max age to 0 to delete cookies from client browser
	     *
	     * @param response HttpServletResponse to add cookies to
	     */
	    private void clearAuthenticationCookies(HttpServletResponse response) {
	    	// Clear access token cookie
	    	Cookie accessTokenCookie = new Cookie("access_token", "");
	    	accessTokenCookie.setMaxAge(0);
	    	accessTokenCookie.setPath("/");
	    	accessTokenCookie.setHttpOnly(true);
	    	accessTokenCookie.setSecure(true);
	    	response.addCookie(accessTokenCookie);
	    	System.out.println("Access token cookie cleared");

	    	// Clear refresh token cookie
	    	Cookie refreshTokenCookie = new Cookie("refresh_token", "");
	    	refreshTokenCookie.setMaxAge(0);
	    	refreshTokenCookie.setPath("/");
	    	refreshTokenCookie.setHttpOnly(true);
	    	refreshTokenCookie.setSecure(true);
	    	response.addCookie(refreshTokenCookie);
	    	System.out.println("Refresh token cookie cleared");

	    	// Clear ID token cookie if exists
	    	Cookie idTokenCookie = new Cookie("id_token", "");
	    	idTokenCookie.setMaxAge(0);
	    	idTokenCookie.setPath("/");
	    	idTokenCookie.setHttpOnly(true);
	    	idTokenCookie.setSecure(true);
	    	response.addCookie(idTokenCookie);
	    	System.out.println("ID token cookie cleared");

	    	// Clear session cookie
	    	Cookie sessionCookie = new Cookie("JSESSIONID", "");
	    	sessionCookie.setMaxAge(0);
	    	sessionCookie.setPath("/");
	    	response.addCookie(sessionCookie);
	    	System.out.println("Session cookie cleared");
	    }
}

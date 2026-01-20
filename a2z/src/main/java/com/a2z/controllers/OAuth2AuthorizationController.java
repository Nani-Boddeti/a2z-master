/*
package com.a2z.controllers;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

*/
/**
 * Custom OAuth2 Authorization Endpoint Controller
 * Handles authorization requests from OAuth2 clients
 * This is a wrapper around Spring Authorization Server's /oauth2/authorize endpoint
 *//*

@Controller
@RequestMapping("/oauth2")
public class OAuth2AuthorizationController {

    private final RegisteredClientRepository registeredClientRepository;

    public OAuth2AuthorizationController(RegisteredClientRepository registeredClientRepository) {
        this.registeredClientRepository = registeredClientRepository;
    }

    */
/**
     * Custom authorize endpoint that handles authorization code flow
     *
     * This endpoint:
     * 1. Checks if user is authenticated
     * 2. If not authenticated, redirects to login page
     * 3. If authenticated, generates authorization code
     * 4. Redirects back to client with authorization code
     *
     * @param clientId OAuth2 client identifier (mapped from client_id)
     * @param responseType Must be "code" for authorization code flow (mapped from response_type)
     * @param redirectUri Where to redirect after authorization (mapped from redirect_uri); optional - will be resolved from RegisteredClient if missing and only one redirect exists
     * @param scope Requested scopes
     * @param state CSRF protection state parameter
     * @param codeChallenge PKCE code_challenge (optional)
     * @param codeChallengeMethod PKCE method (optional)
     * @return RedirectView to either login or callback with code
     *//*

    @GetMapping("/authorize")
    public RedirectView authorize(
            @RequestParam(name = "client_id") String clientId,
            @RequestParam(name = "response_type", defaultValue = "code") String responseType,
            @RequestParam(name = "redirect_uri", required = false) String redirectUri,
            @RequestParam(name = "scope", defaultValue = "openid profile email") String scope,
            @RequestParam(name = "state", required = false) String state,
            @RequestParam(name = "code_challenge", required = false) String codeChallenge,
            @RequestParam(name = "code_challenge_method", required = false) String codeChallengeMethod) {

        System.out.println("=== OAuth2 Authorization Request ===");
        System.out.println("Client ID: " + clientId);
        System.out.println("Response Type: " + responseType);
        System.out.println("Redirect URI (request): " + redirectUri);
        System.out.println("Scope: " + scope);
        System.out.println("State: " + state);
        System.out.println("code_challenge: " + codeChallenge);
        System.out.println("code_challenge_method: " + codeChallengeMethod);

        // Resolve redirect URI if not provided in request
        String resolvedRedirectUri = redirectUri;
        if (resolvedRedirectUri == null || resolvedRedirectUri.isBlank()) {
            RegisteredClient rc = registeredClientRepository.findByClientId(clientId);
            if (rc != null && rc.getRedirectUris() != null && rc.getRedirectUris().size() == 1) {
                resolvedRedirectUri = rc.getRedirectUris().iterator().next();
                System.out.println("Resolved redirect URI from RegisteredClient: " + resolvedRedirectUri);
            } else {
                System.err.println("Unable to resolve redirect_uri for client: " + clientId);
                // Redirect to a clear error endpoint so caller sees a message
                return new RedirectView("/error?error=missing_redirect_uri", true);
            }
        }

        // Check if user is authenticated
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated()
                && !authentication.getAuthorities().isEmpty();

        System.out.println("Is Authenticated: " + isAuthenticated);

        if (!isAuthenticated) {
            // Redirect to login page with return URL
            System.out.println("User not authenticated, redirecting to login");
            StringBuilder loginUrl = new StringBuilder("/loginV2?redirect=");
            try {
                String returnUrl = buildAuthorizationUrl(clientId, responseType, resolvedRedirectUri, scope, state, codeChallenge, codeChallengeMethod);
                loginUrl.append(URLEncoder.encode(returnUrl, StandardCharsets.UTF_8.name()));
            } catch (Exception e) {
                System.err.println("Error encoding return URL: " + e.getMessage());
            }
            return new RedirectView(loginUrl.toString(), true);
        }

        // User is authenticated, generate authorization code
        System.out.println("User authenticated, generating authorization code");
        String authorizationCode = generateAuthorizationCode();
        System.out.println("Authorization Code Generated: " + authorizationCode);

        // Build redirect URL with authorization code (use standard param names)
        StringBuilder callbackUrl = new StringBuilder(resolvedRedirectUri);
        if (resolvedRedirectUri.contains("?")) {
            callbackUrl.append("&");
        } else {
            callbackUrl.append("?");
        }
        callbackUrl.append("code=").append(authorizationCode);
        if (state != null) {
            callbackUrl.append("&state=").append(state);
        }

        System.out.println("Redirecting to callback: " + callbackUrl.toString());
        return new RedirectView(callbackUrl.toString(), true);
    }

    */
/**
     * Generate a unique authorization code
     * In production, this should be persisted with expiration time
     *
     * @return Authorization code
     *//*

    private String generateAuthorizationCode() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    */
/**
     * Build authorization URL with parameters
     *
     * @param clientId OAuth2 client identifier
     * @param responseType Response type (code)
     * @param redirectUri Redirect URI
     * @param scope Requested scopes
     * @param state State parameter
     * @param codeChallenge PKCE challenge
     * @param codeChallengeMethod PKCE method
     * @return Complete authorization URL
     *//*

    private String buildAuthorizationUrl(String clientId, String responseType, String redirectUri,
                                        String scope, String state, String codeChallenge, String codeChallengeMethod) {
        StringBuilder url = new StringBuilder("/oauth2/authorize");
        url.append("?client_id=").append(clientId);
        url.append("&response_type=").append(responseType);
        url.append("&redirect_uri=").append(URLEncoder.encode(redirectUri, StandardCharsets.UTF_8));
        url.append("&scope=").append(URLEncoder.encode(scope, StandardCharsets.UTF_8));
        if (state != null) {
            url.append("&state=").append(state);
        }
        if (codeChallenge != null) {
            url.append("&code_challenge=").append(codeChallenge);
        }
        if (codeChallengeMethod != null) {
            url.append("&code_challenge_method=").append(codeChallengeMethod);
        }
        return url.toString();
    }
}

*/

package com.a2z.filters;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class BearerTokenFilter extends OncePerRequestFilter {

	// Paths that should NOT have token injected (public endpoints)
	private static final String[] EXCLUDED_PATHS = {
		"/api/c/",           // Category endpoints
		"/api/ad/all",       // Ad listing
		"/api/ad/view/",     // Ad viewing
		"/api/search/",      // Search
		"/api/login",        // Login
		"/oauth2/",      // OAuth2
		"/api/customerSubmit", // Registration
		"/api/suggest/password", // Password suggestions
		"/api/generate/otp/",   // OTP generation
		"/api/validateOTP",     // OTP validation
		"/",             // Root
		"/uploads/",     // Static uploads
		"/error"         // Error pages
	};

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        // Skip filter for excluded paths
        boolean isExcluded = false;
        for (String excludedPath : EXCLUDED_PATHS) {
            if (path.startsWith(excludedPath)) {
                isExcluded = true;
                break;
            }
        }

        if (!isExcluded) {
            try {
                // Extract token from session only for protected endpoints
                String token = (String) request.getSession(false).getAttribute("accessToken");

                if (token != null && !token.isEmpty()) {
                    // Add the token to the Authorization header
                    request = new HttpServletRequestWrapper(request) {
                        @Override
                        public String getHeader(String name) {
                            if ("Authorization".equalsIgnoreCase(name)) {
                                return "Bearer " + token;
                            }
                            return super.getHeader(name);
                        }
                    };
                }
            } catch (Exception e) {
                // Session might not exist yet or is not fully initialized
                // Continue without token
            }
        }

        filterChain.doFilter(request, response);
    }

	
}

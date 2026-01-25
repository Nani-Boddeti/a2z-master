package com.a2z.filters;

import com.a2z.dao.Customer;
import com.a2z.persistence.PODCustomerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@Component
public class AdminAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private PODCustomerRepository customerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Only intercept admin login endpoint
        if (!request.getRequestURI().equals("/api/admin/login")) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!request.getMethod().equals("POST")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Parse login request
            com.a2z.data.AdminLoginRequest loginRequest = objectMapper.readValue(request.getInputStream(), com.a2z.data.AdminLoginRequest.class);

            // Authenticate admin user
            Optional<Customer> customerOpt = customerRepository.findById(loginRequest.getUsername());

            if (customerOpt.isEmpty()) {
                sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Invalid username or password");
                return;
            }

            Customer customer = customerOpt.get();

            // Check if user is admin (has ROLE_ADMIN)
            if (!"ROLE_ADMIN".equals(customer.getRole())) {
                sendErrorResponse(response, HttpStatus.FORBIDDEN, "User is not an admin");
                return;
            }

            // Verify password
            if (!passwordEncoder.matches(loginRequest.getPassword(), customer.getPassword())) {
                sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Invalid username or password");
                return;
            }

            // Check if user is disabled
            if (customer.isDisabled()) {
                sendErrorResponse(response, HttpStatus.FORBIDDEN, "User account is disabled");
                return;
            }

            // Generate admin token (app.write scope)
            String adminToken = generateAdminToken(customer);

            // Send success response with token
            com.a2z.data.AdminLoginResponse loginResponse = new com.a2z.data.AdminLoginResponse(
                true,
                adminToken,
                customer.getUserName(),
                customer.getEmail(),
                "app.write",
                "Admin authenticated successfully"
            );

            response.setStatus(HttpStatus.OK.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(objectMapper.writeValueAsString(loginResponse));

        } catch (IOException e) {
            sendErrorResponse(response, HttpStatus.BAD_REQUEST, "Invalid request format");
        }
    }

    private String generateAdminToken(Customer customer) {
        // This is a placeholder. You should integrate with your JWT token generation logic
        // For now, returning a simple token that includes admin identifier
        Map<String, Object> tokenData = new HashMap<>();
        tokenData.put("username", customer.getUserName());
        tokenData.put("scope", "app.admin");
        tokenData.put("type", "admin");
        tokenData.put("timestamp", System.currentTimeMillis());

        try {
            return objectMapper.writeValueAsString(tokenData);
        } catch (Exception e) {
            return UUID.randomUUID().toString();
        }
    }

    private void sendErrorResponse(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        com.a2z.data.AdminLoginResponse errorResponse = new com.a2z.data.AdminLoginResponse(false, message);

        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}


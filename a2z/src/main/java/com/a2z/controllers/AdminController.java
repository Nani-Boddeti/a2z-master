package com.a2z.controllers;

import com.a2z.data.AdminUserRequest;
import com.a2z.data.AdminUserResponse;
import com.a2z.services.impl.AdminManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin("http://localhost:4200")
@Secured("SCOPE_app.admin")
public class AdminController {

    @Autowired
    private AdminManagementService adminManagementService;

    @PostMapping("/create-user")
    public ResponseEntity<AdminUserResponse> createAdminUser(@Valid @RequestBody AdminUserRequest request) {
        AdminUserResponse response = adminManagementService.createAdminUser(request);

        if (response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Admin API is running");
    }
}


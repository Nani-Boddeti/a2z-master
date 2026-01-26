package com.a2z.controllers;

import com.a2z.data.PasswordResetRequest;
import com.a2z.services.impl.ForgotPasswordTokenGeneratorService;
import com.a2z.services.interfaces.CustomerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@ResponseBody
@Validated
@RequestMapping("/api")
public class ForgotPasswordController {
    @Autowired
    private CustomerService customerService;
    @GetMapping("/forgot-password")
    public ResponseEntity forgotPassword(HttpServletRequest request, @RequestParam(required = true) String userName){
        customerService.sendForgotPasswordLink(userName);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping("/reset-password")
    public ResponseEntity resetPassword(@RequestBody @Valid PasswordResetRequest request) {
        ForgotPasswordTokenGeneratorService.ResetToken resetToken = customerService.verifyUserForForgotPasswordLink(request.getToken());
        if (resetToken!=null) {
            customerService.updatePassword(request.getNewPassword(), resetToken.userId());
            return ResponseEntity.ok("Password reset successful");
        }
        return ResponseEntity.badRequest().body("Invalid or expired token");
    }
}

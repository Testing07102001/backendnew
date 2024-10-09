package org.kreyzon.stripe.controller;

import org.kreyzon.stripe.dto.OtpVerificationRequest;
import org.kreyzon.stripe.dto.PasswordResetRequest;
import org.kreyzon.stripe.dto.PasswordResetResponse;
import org.kreyzon.stripe.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PasswordResetController {
    @Autowired
    private UserService userService;

    @PostMapping("/request-otp")
    public ResponseEntity<?> requestOtp(@RequestBody PasswordResetRequest request) {
        String email = request.getEmail();

        // Check if the email exists in the database
        if (userService.isEmailExists(email)) {
            // If the email exists, send OTP
            if (userService.sendOtp(email)) {
                PasswordResetResponse response = new PasswordResetResponse("success");
                return ResponseEntity.ok(response);
            } else {
                PasswordResetResponse response = new PasswordResetResponse("unsuccess");
                return ResponseEntity.badRequest().body(response);
            }
        } else {
            // If the email does not exist, return an error response
            PasswordResetResponse response = new PasswordResetResponse("User not found");
            return ResponseEntity.status(404).body(response);
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody OtpVerificationRequest request) {
        String email = request.getEmail();
        String enteredOtp = request.getEnteredOtp();

        boolean isOtpValid = userService.verifyOtp(email, enteredOtp);

        if (isOtpValid) {
            return ResponseEntity.ok().body("OTP verified successfully");
        } else {
            return ResponseEntity.badRequest().body("Invalid OTP");
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetRequest request) {
        try {
            userService.resetPassword(request.getEmail(), request.getNewPassword());
            return ResponseEntity.ok(new PasswordResetResponse("Password reset successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new PasswordResetResponse(e.getMessage()));
        }
    }
}

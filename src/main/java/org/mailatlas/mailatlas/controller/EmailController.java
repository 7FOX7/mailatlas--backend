
package org.mailatlas.mailatlas.controller;

import org.mailatlas.mailatlas.dto.response.EmailResponse;
import org.mailatlas.mailatlas.entity.UserData;
import org.mailatlas.mailatlas.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmailController {
    @Autowired
    private EmailService emailService;

    @GetMapping
    @RequestMapping("/verify/{email}")
    public ResponseEntity<EmailResponse> verifyEmail(@PathVariable String email) {
        if (email.isEmpty()) {
            return ResponseEntity.badRequest().header("Invalid", "Email is not found in the URL.").build();
        }

        UserData data = emailService.getData(email);
        if (data == null) {
            return ResponseEntity.status(500).header("Server Error", "Failed to verify email.").build();
        }
        // fill a response
        EmailResponse emailResponse = EmailResponse.builder()
                .createdAt(data.getCreatedAt())
                .email(data.getEmail())
                .firstName(data.getFirstName())
                .lastName(data.getLastName())
                .gender(data.getGender())
                .dateOfBirth(data.getDateOfBirth())
                .isVerified(data.getIsVerified())
                .build();

        return ResponseEntity.ok(emailResponse);
    }
}
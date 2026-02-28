
package org.mailatlas.mailatlas.controller;

import org.mailatlas.mailatlas.dto.response.EmailResponse;
import org.mailatlas.mailatlas.entity.UserData;
import org.mailatlas.mailatlas.service.VerifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/verify")
public class VerifyController {
    @Autowired
    private VerifyService verifyService;

    @GetMapping
    @RequestMapping("/{email}")
    public ResponseEntity<EmailResponse> verifyEmail(@PathVariable String email) {
        if (email.isEmpty()) {
            return ResponseEntity.badRequest().header("Invalid", "Email is not found in the URL.").build();
        }

        UserData data = verifyService.getData(email);
        if (data == null) {
            return ResponseEntity.ok().header("Email status", "Provided email failed verification.").build();
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
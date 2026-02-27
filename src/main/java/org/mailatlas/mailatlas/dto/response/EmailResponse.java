package org.mailatlas.mailatlas.dto.response;

import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public class EmailResponse {
    public LocalDateTime createdAt;
    public String email;
    public String firstName;
    public String lastName;
    public String gender;
    public LocalDate dateOfBirth;
    public Boolean isVerified;
}

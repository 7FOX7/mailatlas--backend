package org.mailatlas.mailatlas.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
public class UserData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "email", unique = true, nullable = false, updatable = false)
    private String email;

    @Column(name = "first_name", nullable = true, updatable = false)
    private String firstName;

    @Column(name = "last_name", nullable = true, updatable = false)
    private String lastName;

    @Column(name = "gender", nullable = true, updatable = false)
    private String gender;

    @Column(name = "date_of_birth", nullable = true, updatable = false)
    private LocalDate dateOfBirth;

    @Column(name = "is_verified", nullable = false, updatable = false)
    private Boolean isVerified;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}

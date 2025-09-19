package com.project.sunauloNepal.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.time.LocalDateTime;

import com.project.sunauloNepal.ENUM.EmailStatus;
import com.project.sunauloNepal.ENUM.IdentityStatus;
import com.project.sunauloNepal.ENUM.Role;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmailStatus emailStatus = EmailStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IdentityStatus identityStatus = IdentityStatus.PENDING;

    @Column(updatable = false)
    private LocalDateTime createdAt;
  
    private String phoneNumber;
    
    private String fullAddress;
    private double longitude;
    private double latitude;

    //Automatically set createdAt before insert
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}

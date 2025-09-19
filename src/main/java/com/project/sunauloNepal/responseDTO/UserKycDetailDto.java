package com.project.sunauloNepal.responseDTO;

import java.time.LocalDateTime;

import com.project.sunauloNepal.ENUM.IdentityStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserKycDetailDto {
    private Long kycId;
    private String fullName;
    private String email;
    private String documentFrontImage;
    private String documentBackImage;
    private String userPhoto;
    private IdentityStatus status;
    private LocalDateTime submittedAt;
}

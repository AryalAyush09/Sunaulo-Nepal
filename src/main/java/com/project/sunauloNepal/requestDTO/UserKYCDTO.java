package com.project.sunauloNepal.requestDTO;


import lombok.*;

import java.time.LocalDateTime;

import com.project.sunauloNepal.ENUM.IdentityStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserKYCDTO {
	private Long id;
    private Long userId;
    private String documentType;
    private String documentFrontImage;
    private String documentBackImage;
    private String userPhoto;
    private IdentityStatus status;
    private LocalDateTime submittedAt;
    private LocalDateTime verifiedAt;
}

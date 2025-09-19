package com.project.sunauloNepal.responseDTO;

import com.project.sunauloNepal.ENUM.AuthorityType;
import com.project.sunauloNepal.ENUM.IdentityStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthorityDetailDto {
    private Long id;
    private String fullName;
    private String email;
    private AuthorityType authorityType;

    private String photo;
    private String citizenshipFrontImage;
    private String citizenshipBackImage;
    private String authorityIdentityCardImage;
    private String phoneNumber;
    private String fullAddress;
    private Double latitude;
    private Double longitude;

    private IdentityStatus status;
}

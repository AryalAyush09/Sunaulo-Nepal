package com.project.sunauloNepal.responseDTO;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthorityProfileResponseDto {
    private Long id;
    private String authorityFullName;
    private String email;
    private String phoneNumber;
    private String authorityType;

    private String fullAddress;
    private Double latitude;
    private Double longitude;

    private KycDto kyc;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class KycDto {
        private String profilePhoto;
        private String citizenshipFrontImage;
        private String citizenshipBackImage;
        private String authorityIdentityCardImage;
    }
}



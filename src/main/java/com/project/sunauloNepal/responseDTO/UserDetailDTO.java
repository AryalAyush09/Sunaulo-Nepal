package com.project.sunauloNepal.responseDTO;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDetailDTO {
    private Long id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String identityStatus; // e.g. PENDING, VERIFIED
}

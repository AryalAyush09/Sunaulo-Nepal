package com.project.sunauloNepal.responseDTO;

import com.project.sunauloNepal.ENUM.AuthorityType;
import com.project.sunauloNepal.ENUM.IdentityStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthorityListDto {
    private Long id;
    private String fullName;
    private String email;
    private AuthorityType authorityType;
    private IdentityStatus status; // user ko status
}

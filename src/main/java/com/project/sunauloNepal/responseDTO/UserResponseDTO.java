package com.project.sunauloNepal.responseDTO;

import com.project.sunauloNepal.ENUM.IdentityStatus;
import com.project.sunauloNepal.ENUM.Role;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDTO {
	private String fullName;
    private Role role;
    private IdentityStatus status;
}

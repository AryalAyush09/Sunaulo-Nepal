package com.project.sunauloNepal.responseDTO;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorityMessageDTO {
    private String citizenId;
    private String authorityId;
    private String text;

}


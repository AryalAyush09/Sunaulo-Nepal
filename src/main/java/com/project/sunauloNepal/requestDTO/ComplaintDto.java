package com.project.sunauloNepal.requestDTO;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import com.project.sunauloNepal.ENUM.AuthorityType;

@Getter 
@Setter
@NoArgsConstructor 
@AllArgsConstructor
@Builder
public class ComplaintDto {
    private String text;
    private MultipartFile[] mediaPaths;   // optional
    private Double latitude;   
    private Double longitude;
    private String fullAddress;

    //  New fields for differentiating
    private AuthorityType authorityType;   // e.g., "WARD", "POLICE", "FIRE"
    private Long authorityId;       // optional: if specific ward/admin is selected
}

package com.project.sunauloNepal.requestDTO;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class UserKYCRequestDTO {
//	private String citizenshipNumber;
	private String phonenumber;
    private MultipartFile frontImage;
    private MultipartFile backImage;
    private MultipartFile userPhoto;
}

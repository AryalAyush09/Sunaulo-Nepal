package com.project.sunauloNepal.responseDTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ComplaintResponseDTO {
    private Long complaintId;
    private String userText;
    private String n8nText;
    private String fullAddress;
    private String complaintStatus;
    private String authorityName;
}



package com.project.sunauloNepal.responseDTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WebhookResponseDTO {

    private Output output; // <-- maps the "output" field from n8n

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Output {
        private Long complaintId;
        private String authorityType;
        private String message;
    }
}

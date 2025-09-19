package com.project.sunauloNepal.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.project.sunauloNepal.responseDTO.ApiResponse;
import com.project.sunauloNepal.responseDTO.WebhookPayload;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebhookService {

    private final RestTemplate restTemplate = new RestTemplate();

    // n8n webhook URL (can move to application.properties)
    private static final String WEBHOOK_URL = 
    "https://pradip69.app.n8n.cloud/webhook-test/2e96294e-763b-4e0a-90fb-b7d024c26f68";

    public ApiResponse<String> sendComplaintWebhook(WebhookPayload payload) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer secret-token"); // optional
            
            HttpEntity<WebhookPayload> request = new HttpEntity<>(payload, headers);
       
            ResponseEntity<String> response = restTemplate.postForEntity
            		(WEBHOOK_URL, request, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info(" Webhook sent successfully to {}", WEBHOOK_URL);
                return new ApiResponse<>(true, "Webhook delivered", response.getBody());
            } else {
                log.error(" Webhook failed with status {}", response.getStatusCode());
                return new ApiResponse<>(false, "Webhook delivery failed", null);
            }
        } catch (Exception e) {
            log.error("Webhook exception: {}", e.getMessage(), e);
            
            return new ApiResponse<>(false, "Webhook error: " + e.getMessage(), null);
        }
    }
}

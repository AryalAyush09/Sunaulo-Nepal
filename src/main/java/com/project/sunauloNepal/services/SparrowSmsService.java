package com.project.sunauloNepal.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class SparrowSmsService implements SmsService {

	@Value("${sparrow.api.url}")
	private String apiUrl;

	@Value("${sparrow.token}")
	private String token;

	@Value("${sparrow.from}")
	private String from;


    private final RestTemplate restTemplate = new RestTemplate();

    

    @Override
    public void sendSms(String phoneNumber, String message) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("token", token);
            formData.add("from", from);
            formData.add("to", phoneNumber);
            formData.add("text", message);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formData, headers);

            ResponseEntity<String> response =
                    restTemplate.postForEntity(apiUrl, request, String.class);
                    log.info("SMS API Response: {} - {}", response.getStatusCode(), response.getBody());

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("✅ SMS sent successfully to {} | Response: {}", phoneNumber, response.getBody());
            } else {
                log.warn("❌ Failed to send SMS to {} | Status: {} | Response: {}",
                        phoneNumber, response.getStatusCode(), response.getBody());
            }
        } catch (Exception ex) {
            log.error("🔥 Error sending SMS to {}: {}", phoneNumber, ex.getMessage(), ex);
        }
    }
}

//    @Override
//    public void sendSms(String phoneNumber, String message) {
//        try {
//            // Sparrow expects application/x-www-form-urlencoded OR JSON
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//
//            Map<String, String> params = new HashMap<>();
//            params.put("token", token);
//            params.put("from", from);
//            params.put("to", phoneNumber);
//            params.put("text", message);
//
//            HttpEntity<Map<String, String>> request = new HttpEntity<>(params, headers);
//
//            ResponseEntity<String> response =
//                    restTemplate.postForEntity(apiUrl, request, String.class);
//
//            if (response.getStatusCode().is2xxSuccessful()) {
//                log.info("✅ SMS sent successfully to {} | Response: {}", phoneNumber, response.getBody());
//            } else {
//                log.warn("❌ Failed to send SMS to {} | Status: {} | Response: {}",
//                        phoneNumber, response.getStatusCode(), response.getBody());
//            }
//
//        } catch (Exception ex) {
//            log.error("🔥 Error sending SMS to {}: {}", phoneNumber, ex.getMessage(), ex);
//        }
//    }
//}

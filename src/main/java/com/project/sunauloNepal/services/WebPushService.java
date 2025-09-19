package com.project.sunauloNepal.services;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebPushService {

    private final PushService pushService; // Library like web-push-java

    public void sendNotification(Map<String, Object> payload, Map<String, Object> subscription) {
        try {
            // Convert payload to JSON string
            String payloadJson = new ObjectMapper().writeValueAsString(payload);

            Notification notification = new Notification(
                    (String) subscription.get("endpoint"),
                    (String) ((Map<String, Object>) subscription.get("keys")).get("p256dh"),
                    (String) ((Map<String, Object>) subscription.get("keys")).get("auth"),
                    payloadJson.getBytes()
            );

            pushService.send(notification);

            log.info("Web push sent successfully to endpoint {}", subscription.get("endpoint"));
        } catch (Exception ex) {
            log.error("Failed to send web push to endpoint {}: {}", subscription.get("endpoint"), ex.getMessage(), ex);
            // Optionally: remove invalid subscription from DB
        }
    }
}

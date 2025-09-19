package com.project.sunauloNepal.controller;

import com.project.sunauloNepal.entities.AuthorityProfile;
import com.project.sunauloNepal.exception.BadRequestException;
import com.project.sunauloNepal.repository.AuthorityProfileRepository;
import com.project.sunauloNepal.responseDTO.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private AuthorityProfileRepository authorityRepository;

    /**
     * Save subscription details (browser push subscription) for authority
     */
    @PostMapping("/subscribe")
    public ResponseEntity<ApiResponse<?>> subscribe(
            Authentication auth, @RequestBody Map<String, Object> subscription) {

        if (subscription == null || subscription.isEmpty()) {
            throw new BadRequestException("Subscription data cannot be empty");
        }

        Long authId;
        try {
            authId = Long.parseLong(auth.getName());
        } catch (NumberFormatException e) {
            throw new BadRequestException("Invalid authentication ID");
        }

        AuthorityProfile authority = authorityRepository.findById(authId)
                .orElseThrow(() -> new BadRequestException("Authority not found"));

        try {
            ObjectMapper mapper = new ObjectMapper();
            String subscriptionJson = mapper.writeValueAsString(subscription);

            authority.getDeviceTokens().add(subscriptionJson);
            authorityRepository.save(authority);

            return ResponseEntity.ok(new ApiResponse<>(true, "Subscription saved", null));

        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse<>(false, "Subscription failed", null));
        }
    }
}
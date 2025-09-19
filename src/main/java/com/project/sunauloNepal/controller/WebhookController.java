package com.project.sunauloNepal.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.sunauloNepal.entities.AuthorityProfile;
import com.project.sunauloNepal.responseDTO.ApiResponse;
import com.project.sunauloNepal.responseDTO.WebhookResponseDTO;
import com.project.sunauloNepal.services.ComplaintService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/webhook/callback")
public class WebhookController {

    private final ComplaintService service;

//    @PostMapping("/n8n")
//    public ResponseEntity<ApiResponse<AuthorityProfile>> receiveN8nAuthority(@RequestBody WebhookResponseDTO dto) {
//        ApiResponse<AuthorityProfile> response = service.handleN8nCallback(dto);
//       return ResponseEntity.ok(response);
//   }
}




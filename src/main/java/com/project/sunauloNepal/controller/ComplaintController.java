package com.project.sunauloNepal.controller;

import com.project.sunauloNepal.requestDTO.ComplaintDto;
import com.project.sunauloNepal.responseDTO.ApiResponse;
import com.project.sunauloNepal.services.ComplaintService;

import lombok.RequiredArgsConstructor;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/complaints")
@RequiredArgsConstructor
public class ComplaintController {

    private final ComplaintService complaintService;

    @PostMapping(value = "/send", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<?>> submitComplaint(Authentication auth,
                                                          @ModelAttribute ComplaintDto dto) {
        Long userId = Long.parseLong(auth.getName());
        ApiResponse<Map<String, Object>> response = complaintService.saveComplaint(userId, dto);
        return ResponseEntity.ok(response);
    }

}

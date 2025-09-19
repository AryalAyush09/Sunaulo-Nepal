package com.project.sunauloNepal.controller;

import com.project.sunauloNepal.entities.AuthorityProfile;
import com.project.sunauloNepal.requestDTO.AuthorityRegisterRequestDto;
import com.project.sunauloNepal.responseDTO.ApiResponse;
import com.project.sunauloNepal.responseDTO.AuthorityProfileResponseDto;
import com.project.sunauloNepal.services.AuthorityService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth/authority")
@RequiredArgsConstructor
public class AuthorityAuthController {

    private final AuthorityService authorityService;

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> registerAuthority(@ModelAttribute AuthorityRegisterRequestDto dto) {
        ApiResponse<Map<String, String>> saved = authorityService.registerAuthority(dto);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getProfile(Authentication auth) {
        Long authId = Long.parseLong(auth.getName());
        AuthorityProfile profile = authorityService.getAuthorityById(authId);

        AuthorityProfileResponseDto responseDto = authorityService.mapToDto(profile);

        return ResponseEntity.ok(new ApiResponse<>(true, "Authority profile fetched", responseDto));
    }

}

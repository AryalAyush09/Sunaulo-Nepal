package com.project.sunauloNepal.controller;

import com.project.sunauloNepal.requestDTO.UserKYCDTO;
import com.project.sunauloNepal.requestDTO.UserKYCRequestDTO;
import com.project.sunauloNepal.responseDTO.ApiResponse;
import com.project.sunauloNepal.responseDTO.ComplaintResponseDTO;
import com.project.sunauloNepal.responseDTO.UserDetailDTO;
import com.project.sunauloNepal.services.UserService;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping(value = "/kyc", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<UserKYCDTO>> uploadKYC(Authentication auth,
                                                              @ModelAttribute UserKYCRequestDTO dto) {
        Long userId = Long.parseLong(auth.getName()); // NumberFormatException auto-handled
        UserKYCDTO responseDTO = userService.uploadKYC(userId, dto);
        return ResponseEntity.ok(ApiResponse.<UserKYCDTO>builder()
                .success(true)
                .message("KYC submitted successfully. Pending verification.")
                .data(responseDTO)
                .build());
    }
    
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDetailDTO>> getUserDetail(Authentication auth) {
        Long userId = Long.parseLong(auth.getName());
        UserDetailDTO dto = userService.getUserDetail(userId);

        return ResponseEntity.ok(ApiResponse.<UserDetailDTO>builder()
                .success(true)
                .message("User details fetched successfully.")
                .data(dto)
                .build());
    }
    
    @GetMapping("/mycomplaints")
    public ResponseEntity<ApiResponse<List<ComplaintResponseDTO>>> getMyComplaints(Authentication auth) {
        Long userId = Long.parseLong(auth.getName());
        List<ComplaintResponseDTO> complaints = userService.getMyComplaints(userId);
        return ResponseEntity.ok(ApiResponse.<List<ComplaintResponseDTO>>builder()
                .success(true)
                .message("User complaints fetched successfully.")
                .data(complaints)
                .build());
    }
}

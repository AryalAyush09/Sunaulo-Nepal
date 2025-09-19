package com.project.sunauloNepal.controller;


import com.project.sunauloNepal.ENUM.IdentityStatus;
import com.project.sunauloNepal.responseDTO.*;
import com.project.sunauloNepal.services.AdminService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // ---------------- User KYC ----------------
    @GetMapping("/users/kyc/pending")
    public ResponseEntity<ApiResponse<List<UserKycListDto>>> getPendingUsers() {
        List<UserKycListDto> list = adminService.getPendingUsersKYC();
        return ResponseEntity.ok(ApiResponse.<List<UserKycListDto>>builder()
                .success(true)
                .message("Pending users fetched successfully")
                .data(list)
                .build());
    }

    @GetMapping("/users/kyc/verified")
    public ResponseEntity<ApiResponse<List<UserKycListDto>>> getVerifiedUsers() {
        List<UserKycListDto> list = adminService.getVerifiedUsersKYC();
        return ResponseEntity.ok(ApiResponse.<List<UserKycListDto>>builder()
                .success(true)
                .message("Verified users fetched successfully")
                .data(list)
                .build());
    }

    @GetMapping("/users/kyc/count")
    public ResponseEntity<ApiResponse<Object>> countUsersByStatus() {
        long verified = adminService.countUsersByStatus(IdentityStatus.VERIFIED);
        long pending = adminService.countUsersByStatus(IdentityStatus.PENDING);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("User counts fetched successfully")
                .data(Map.of("verified", verified, "pending", pending))
                .build());
    }

    @GetMapping("/users/kyc/{id}")
    public ResponseEntity<ApiResponse<UserKycDetailDto>> getUserKycDetail(@PathVariable Long id) {
        UserKycDetailDto detail = adminService.getUserKycDetail(id);
        return ResponseEntity.ok(ApiResponse.<UserKycDetailDto>builder()
                .success(true)
                .message("User KYC detail fetched successfully")
                .data(detail)
                .build());
    }

    @PostMapping("/users/kyc/{id}/approve")
    public ResponseEntity<ApiResponse<?>> approveUser(@PathVariable Long id) {
        adminService.approveUserKYC(id);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("User KYC approved successfully")
                .build());
    }

    @PostMapping("/users/kyc/{id}/reject")
    public ResponseEntity<ApiResponse<?>> rejectUser(@PathVariable Long id, @RequestParam String reason) {
        adminService.rejectUserKYC(id, reason);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("User KYC rejected successfully")
                .build());
    }

    // ---------------- Authority ----------------
    @GetMapping("/authorities/pending")
    public ResponseEntity<ApiResponse<List<AuthorityListDto>>> getPendingAuthorities() {
        List<AuthorityListDto> list = adminService.getPendingAuthorities();
        return ResponseEntity.ok(ApiResponse.<List<AuthorityListDto>>builder()
                .success(true)
                .message("Pending authorities fetched successfully")
                .data(list)
                .build());
    }

    @GetMapping("/authorities/verified")
    public ResponseEntity<ApiResponse<List<AuthorityListDto>>> getVerifiedAuthorities() {
        List<AuthorityListDto> list = adminService.getVerifiedAuthorities();
        return ResponseEntity.ok(ApiResponse.<List<AuthorityListDto>>builder()
                .success(true)
                .message("Verified authorities fetched successfully")
                .data(list)
                .build());
    }

    @GetMapping("/authorities/count")
    public ResponseEntity<ApiResponse<Object>> countAuthoritiesByStatus() {
        long verified = adminService.countAuthoritiesByStatus(IdentityStatus.VERIFIED);
        long pending = adminService.countAuthoritiesByStatus(IdentityStatus.PENDING);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Authority counts fetched successfully")
                .data(Map.of("verified", verified, "pending", pending))
                .build());
    }

    @GetMapping("/authorities/{id}")
    public ResponseEntity<ApiResponse<AuthorityDetailDto>> getAuthorityDetail(@PathVariable Long id) {
        AuthorityDetailDto detail = adminService.getAuthorityDetail(id);
        return ResponseEntity.ok(ApiResponse.<AuthorityDetailDto>builder()
                .success(true)
                .message("Authority detail fetched successfully")
                .data(detail)
                .build());
    }

    @PostMapping("/authorities/{id}/approve")
    public ResponseEntity<ApiResponse<?>> approveAuthority(@PathVariable Long id) {
        adminService.approveAuthority(id);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Authority approved successfully")
                .build());
    }

    @PostMapping("/authorities/{id}/reject")
    public ResponseEntity<ApiResponse<?>> rejectAuthority(@PathVariable Long id, @RequestParam String reason) {
        adminService.rejectAuthority(id, reason);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Authority rejected successfully")
                .build());
    }
}


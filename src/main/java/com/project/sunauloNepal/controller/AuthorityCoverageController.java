package com.project.sunauloNepal.controller;

import com.project.sunauloNepal.entities.AuthorityCoveragePolygon;
import com.project.sunauloNepal.requestDTO.CoveragePolygonRequest;
import com.project.sunauloNepal.responseDTO.ApiResponse;
import com.project.sunauloNepal.responseDTO.CoveragePolygonResponse;
import com.project.sunauloNepal.services.AuthorityCoverageService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/authority-coverage")
@RequiredArgsConstructor
public class AuthorityCoverageController {

    private final AuthorityCoverageService coverageService;

    /**
     * Admin creates a polygon for an authority.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> addCoverage(@RequestBody CoveragePolygonRequest req) {
        AuthorityCoveragePolygon saved = coverageService.addCoveragePolygon(req);
        CoveragePolygonResponse response = coverageService.mapToDto(saved); // ✅

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Coverage polygon created successfully", response)
        );
    }

    /**
     * List all polygons for a specific authority.
     */
    @GetMapping("/{authorityId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> listCoverage(@PathVariable Long authorityId) {
        List<AuthorityCoveragePolygon> list = coverageService.listCoveragePolygons(authorityId);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Coverage polygons fetched successfully", list)
        );
    }

    /**
     * Find authorities whose polygon contains a given point.
     * Example: /api/admin/authority-coverage/contains?lat=27.684&lon=83.463
     */
    @GetMapping("/contains")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> findContainingAuthorities(
            @RequestParam double lat,
            @RequestParam double lon
    ) {
        var ids = coverageService.findAuthoritiesByPoint(lat, lon);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Authorities covering the given point", ids)
        );
    }
}

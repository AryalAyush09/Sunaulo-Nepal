package com.project.sunauloNepal.responseDTO;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CoveragePolygonResponse {
    private Long id;
    private Long authorityId;
    private List<double[]> coordinates;
}


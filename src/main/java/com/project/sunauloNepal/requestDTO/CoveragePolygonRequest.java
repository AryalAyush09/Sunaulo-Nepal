package com.project.sunauloNepal.requestDTO;

import lombok.Data;
import java.util.List;

@Data
public class CoveragePolygonRequest {
    private Long authorityId;
    /**
     * Coordinates in (longitude, latitude) order.
     * The first and last point will be closed automatically if not already.
     */
    private List<double[]> coordinates;
}

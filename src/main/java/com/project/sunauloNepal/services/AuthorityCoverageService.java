package com.project.sunauloNepal.services;

import com.project.sunauloNepal.entities.AuthorityCoveragePolygon;
import com.project.sunauloNepal.entities.AuthorityProfile;
import com.project.sunauloNepal.exception.BadRequestException;
import com.project.sunauloNepal.repository.AuthorityCoveragePolygonRepository;
import com.project.sunauloNepal.repository.AuthorityProfileRepository;
import com.project.sunauloNepal.requestDTO.CoveragePolygonRequest;

import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorityCoverageService {

    private final AuthorityCoveragePolygonRepository coverageRepo;
    private final AuthorityProfileRepository authorityProfileRepo;

    private static final GeometryFactory GEOM_FACTORY = new GeometryFactory(new PrecisionModel(), 4326);

//   
//      Add a coverage polygon for an authority.
//     
    @Transactional 
    public AuthorityCoveragePolygon addCoveragePolygon(CoveragePolygonRequest req) {
        AuthorityProfile authority = authorityProfileRepo.findById(req.getAuthorityId())
                .orElseThrow(() -> new BadRequestException("Authority not found"));

        Polygon polygon = buildPolygon4326(req.getCoordinates());
        if (!polygon.isValid()) {
            throw new BadRequestException("Invalid polygon geometry");
        }

        AuthorityCoveragePolygon entity = AuthorityCoveragePolygon.builder()
                .authorityProfile(authority)
                .area(polygon)
                .build();

        return coverageRepo.save(entity);
    }

//    
//      List all polygons for a given authority
//     
    public List<AuthorityCoveragePolygon> listCoveragePolygons(Long authorityId) {
        return coverageRepo.findByAuthorityProfile_Id(authorityId);
    }

    
//      Given a (lat, lon) from user complaint, find authorities whose coverage contains it.
//      Input order is (lat, lon); repository expects (lon, lat)
     
    public List<Long> findAuthoritiesByPoint(double userLat, double userLon) {
        return coverageRepo.findAuthorityIdsContainingPoint(userLon, userLat);
    }

    // ---------------- Helper ----------------
    private Polygon buildPolygon4326(List<double[]> coords) {
        if (coords == null || coords.size() < 3) {
            throw new BadRequestException("At least 3 coordinates are required to make a polygon");
        }

        // Ensure closed ring: first == last
        double[] first = coords.get(0);
        double[] last  = coords.get(coords.size() - 1);
        boolean closed = first[0] == last[0] && first[1] == last[1];
        int n = coords.size() + (closed ? 0 : 1);

        Coordinate[] ring = new Coordinate[n];
        for (int i = 0; i < coords.size(); i++) {
            double[] c = coords.get(i);
            // order is (lon, lat)
            ring[i] = new Coordinate(c[0], c[1]);
        }
        if (!closed) {
            ring[n - 1] = new Coordinate(first[0], first[1]);
        }

        LinearRing shell = new LinearRing(new CoordinateArraySequence(ring), GEOM_FACTORY);
        return new Polygon(shell, null, GEOM_FACTORY);
    }
}

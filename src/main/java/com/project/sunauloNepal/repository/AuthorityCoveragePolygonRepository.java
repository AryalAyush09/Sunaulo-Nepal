package com.project.sunauloNepal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.sunauloNepal.entities.AuthorityCoveragePolygon;

import java.util.List;

@Repository
public interface AuthorityCoveragePolygonRepository extends JpaRepository<AuthorityCoveragePolygon, Long> {

    List<AuthorityCoveragePolygon> findByAuthorityProfile_Id(Long authorityId);

    /**
     * Find authority IDs whose polygon contains a point (lon, lat).
     * Using native SQL because ST_Contains is a PostGIS function.
     * NOTE: ST_SetSRID(ST_MakePoint(lon, lat), 4326) – order is (lon, lat)
     */
    @Query(value = """
    		SELECT DISTINCT acp.authority_profile_id
    		FROM authority_coverage_polygons acp
    		JOIN authority_profiles ap 
    		  ON ap.user_id = acp.authority_profile_id
    		WHERE ap.authority_type = ?3
    		  AND ST_Contains(
    		    acp.area,
    		    ST_SetSRID(ST_MakePoint(?1, ?2), 4326)
    		  )
    		""", nativeQuery = true)
    	List<Long> findAuthoritiesByCategoryAndLocation(double lon, double lat, String category);

    @Query(value = "SELECT ap.user_id FROM authority_profiles ap " +
            "JOIN authority_coverage_polygons p ON p.authority_profile_id = ap.user_id " +
            "WHERE ST_Contains(p.area, ST_SetSRID(ST_MakePoint(:lon, :lat), 4326))", 
    nativeQuery = true)
List<Long> findAuthorityIdsContainingPoint(@Param("lon") double lon, @Param("lat") double lat);

}

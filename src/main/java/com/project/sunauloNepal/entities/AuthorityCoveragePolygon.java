package com.project.sunauloNepal.entities;

import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Polygon;


@Entity
@Table(name = "authority_coverage_polygons")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AuthorityCoveragePolygon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "authority_profile_id", nullable = false)
    private AuthorityProfile authorityProfile;

    /**
     * Coverage area polygon in SRID 4326 (WGS84)
     */
    @Column(name = "area", columnDefinition = "geometry(Polygon,4326)", nullable = false)
    private Polygon area;
}


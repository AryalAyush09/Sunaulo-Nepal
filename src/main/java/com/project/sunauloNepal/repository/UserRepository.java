package com.project.sunauloNepal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.sunauloNepal.ENUM.IdentityStatus;
import com.project.sunauloNepal.entities.User;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    
    @Query("""
    	    SELECT u FROM User u 
    	    JOIN AuthorityCoveragePolygon acp 
    	      ON ST_Contains(acp.area, ST_SetSRID(ST_MakePoint(u.longitude, u.latitude), 4326)) = true
    	    WHERE acp.authorityProfile.id = :authorityId
    	""")
    	List<User> findUsersWithinCoverage(@Param("authorityId") Long authorityId);
	long countByIdentityStatus(IdentityStatus status);

//    Optional<User> findByOauthProviderAndOauthId(String provider, String oauthId);
}

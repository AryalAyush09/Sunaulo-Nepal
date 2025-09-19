package com.project.sunauloNepal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.sunauloNepal.ENUM.AuthorityType;
import com.project.sunauloNepal.ENUM.IdentityStatus;
import com.project.sunauloNepal.entities.AuthorityProfile;
import com.project.sunauloNepal.entities.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthorityProfileRepository extends JpaRepository<AuthorityProfile, Long> {

    // Fetch by user
    Optional<AuthorityProfile> findByUser(User user);

    // Fetch all authorities whose User has specific identity status
 // Fetch all authorities whose User has specific identity status
    List<AuthorityProfile> findByUser_IdentityStatus(IdentityStatus status);

    Optional<AuthorityProfile> findByAuthorityType(AuthorityType authorityType);
    
    List<AuthorityProfile> findAllByAuthorityType(AuthorityType type);

	long countByUser_IdentityStatus(IdentityStatus status);
}

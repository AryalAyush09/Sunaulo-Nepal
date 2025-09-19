package com.project.sunauloNepal.repository;

import com.project.sunauloNepal.ENUM.IdentityStatus;
import com.project.sunauloNepal.entities.User;
import com.project.sunauloNepal.entities.UserKYC;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserKYCRepository extends JpaRepository<UserKYC, Long> {
	
    // Check if a user has at least one verified KYC
    boolean existsByUserAndStatus(User user, IdentityStatus status);

    // Get all KYCs for a user
    List<UserKYC> findByUser(User user);

    // Get all KYCs by status (for admin review)
    List<UserKYC> findByStatus(IdentityStatus status);
}

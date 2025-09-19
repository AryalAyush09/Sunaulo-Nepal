package com.project.sunauloNepal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.sunauloNepal.ENUM.OtpPurpose;
import com.project.sunauloNepal.entities.OtpToken;
import com.project.sunauloNepal.entities.User;

import java.time.LocalDateTime;

public interface OtpRepository extends JpaRepository<OtpToken, Long> {

    OtpToken findTopByUserAndPurposeOrderByGeneratedAtDesc(User user, OtpPurpose purpose);

    void deleteAllByUserAndPurposeAndExpiresAtBefore(User user, OtpPurpose purpose, LocalDateTime now);
}


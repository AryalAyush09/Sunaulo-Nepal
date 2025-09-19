package com.project.sunauloNepal.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.sunauloNepal.ENUM.ComplaintCategory;
import com.project.sunauloNepal.entities.Complaint;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    List<Complaint> findByUserIdAndCategoryAndCreatedAtAfter(
            Long userId,
            ComplaintCategory category,
            LocalDateTime afterTime
    );
    
    List<Complaint> findByUserIdOrderByCreatedAtDesc(Long userId);
}
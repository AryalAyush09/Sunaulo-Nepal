package com.project.sunauloNepal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.sunauloNepal.entities.Conversation;

import java.util.Optional;
import java.util.List;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {


	    Optional<Conversation> findByComplaintId(Long complaintId);

	    List<Conversation> findByUserId(Long userId);

	    List<Conversation> findByAuthorityId(Long authorityId);
	    
	    Optional<Conversation> findByUserIdAndAuthorityId(Long userId, Long authorityId);

	    Optional<Conversation> findByUserIdAndAuthorityIdAndComplaintId(Long userId, Long authorityId, Long complaintId);
	}
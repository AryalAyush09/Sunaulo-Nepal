package com.project.sunauloNepal.services;

import com.project.sunauloNepal.ENUM.IdentityStatus;
import com.project.sunauloNepal.entities.Complaint;
import com.project.sunauloNepal.entities.User;
import com.project.sunauloNepal.entities.UserKYC;
import com.project.sunauloNepal.exception.BadRequestException;
import com.project.sunauloNepal.repository.ComplaintRepository;
import com.project.sunauloNepal.repository.UserKYCRepository;
import com.project.sunauloNepal.repository.UserRepository;
import com.project.sunauloNepal.requestDTO.UserKYCDTO;
import com.project.sunauloNepal.requestDTO.UserKYCRequestDTO;
import com.project.sunauloNepal.responseDTO.ComplaintResponseDTO;
import com.project.sunauloNepal.responseDTO.UserDetailDTO;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserKYCRepository userKYCRepository;
    private final FileService fileService;
    private final ComplaintRepository complaintRepository;

    @Transactional
    public UserKYCDTO uploadKYC(Long userId, UserKYCRequestDTO request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found"));

        String frontFileName;
        String backFileName;
        String photoFileName;

        try {
            frontFileName = fileService.saveImageFile(request.getFrontImage(), "userkyc/" + userId);
            backFileName = fileService.saveImageFile(request.getBackImage(), "userkyc/" + userId);
            photoFileName = fileService.saveImageFile(request.getUserPhoto(), "userkyc/" + userId);
        } catch (Exception e) {
            throw new BadRequestException("Failed to save KYC images: " + e.getMessage());
        }

        UserKYC userKYC = UserKYC.builder()
        		
                .user(user)
                .documentType("Citizenship")
                .documentFrontImage(frontFileName)
                .documentBackImage(backFileName)
                .userPhoto(photoFileName)
                .status(IdentityStatus.PENDING)
                .submittedAt(LocalDateTime.now())
                .build();

        userKYCRepository.save(userKYC);

        // update User identityStatus; JPA dirty checking will handle save automatically
        user.setIdentityStatus(IdentityStatus.PENDING);

        return mapToDTO(userKYC);
    }
    
    @Transactional(readOnly = true)
    public UserDetailDTO getUserDetail(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found"));

        return UserDetailDTO.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .identityStatus(user.getIdentityStatus().name())
                .build();
    }

    	public List<ComplaintResponseDTO> getMyComplaints(Long userId) {
    	    List<Complaint> complaints = complaintRepository.findByUserIdOrderByCreatedAtDesc(userId);
    	    List<ComplaintResponseDTO> dtoList = new ArrayList<>();

    	    for (Complaint c : complaints) {
    	        ComplaintResponseDTO dto = ComplaintResponseDTO.builder()
    	                .complaintId(c.getId())
    	                .userText(c.getTranscribedText())
    	                .n8nText(c.getCategory() != null ? c.getCategory().name() : null)
    	                .fullAddress(c.getFullAddress())
    	                .complaintStatus(c.getStatus() != null ? c.getStatus().name() : null)
    	                .authorityName(c.getAuthority() != null ? c.getAuthority().getFullName() : null)
    	                .build();
    	        dtoList.add(dto);
    	    }

    	    return dtoList;
    	}


    private UserKYCDTO mapToDTO(UserKYC userKYC) {
        return UserKYCDTO.builder()
                .id(userKYC.getId())
                .userId(userKYC.getUser().getId())
                .documentType(userKYC.getDocumentType())
                .documentFrontImage(userKYC.getDocumentFrontImage())
                .documentBackImage(userKYC.getDocumentBackImage())
                .userPhoto(userKYC.getUserPhoto())
                .status(userKYC.getStatus())
                .submittedAt(userKYC.getSubmittedAt())
                .verifiedAt(userKYC.getVerifiedAt())
                .build();
    }
}

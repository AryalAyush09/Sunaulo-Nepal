package com.project.sunauloNepal.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.project.sunauloNepal.ENUM.EmailStatus;
import com.project.sunauloNepal.ENUM.IdentityStatus;
import com.project.sunauloNepal.ENUM.Role;
import com.project.sunauloNepal.entities.AuthorityProfile;
import com.project.sunauloNepal.entities.User;
import com.project.sunauloNepal.exception.BadRequestException;
import com.project.sunauloNepal.repository.AuthorityProfileRepository;
import com.project.sunauloNepal.repository.UserRepository;
import com.project.sunauloNepal.requestDTO.AuthorityRegisterRequestDto;
import com.project.sunauloNepal.responseDTO.ApiResponse;
import com.project.sunauloNepal.responseDTO.AuthorityProfileResponseDto;
import com.project.sunauloNepal.util.PhoneUtil;

@Service
@RequiredArgsConstructor
public class AuthorityService {

    private final AuthorityProfileRepository authorityRepository;
    private final UserRepository userRepo;
    private final BCryptPasswordEncoder passwordEncoder;
    private final FileService fileService;

    // Authority registration
	    @Transactional
	    public ApiResponse<Map<String, String>> registerAuthority(AuthorityRegisterRequestDto dto) {
	       
	    	if (userRepo.findByEmail(dto.getEmail()).isPresent()) {
	            throw new BadRequestException("Email already exists");
	        }
	
	        // Create user record
	        User user = User.builder()
	                .fullName(dto.getFullName())
	                .email(dto.getEmail())
	                .password(passwordEncoder.encode(dto.getPassword()))
	                .role(Role.AUTHORITY)
	                .fullAddress(dto.getFullAddress())
	                .latitude(dto.getLatitude())
	                .longitude(dto.getLongitude())
	                .phoneNumber(dto.getPhoneNumber())
	              
	                .emailStatus(EmailStatus.PENDING)
	                .identityStatus(IdentityStatus.PENDING)
	                .build();
	
        userRepo.save(user);

        // Save authority profile
        AuthorityProfile profile = AuthorityProfile.builder()
                .user(user)
                .authorityType(dto.getAuthorityType())
                .photo(fileService.saveImageFile(dto.getProfilePhoto(), "profile"))
                .citizenshipFrontImage(fileService.saveImageFile(dto.getCitizenshipFrontImage(), "citizenship"))
                .citizenshipBackImage(fileService.saveImageFile(dto.getCitizenshipBackImage(), "citizenship"))
                .authorityIdentityCardImage(fileService.saveImageFile(dto.getAuthorityIdentityCardImage(), "idcard"))
                .latitude(dto.getLatitude())          // pass lat from DTO
                .longitude(dto.getLongitude())        // pass long from DTO
                .fullAddress(dto.getFullAddress())
//                .phoneNumber(dto.getPhoneNumber())
                .phoneNumber(PhoneUtil.formatToE164(dto.getPhoneNumber()))
                .fullName(dto.getFullName())
                .build();

        authorityRepository.save(profile);

        return new ApiResponse<>(true, "Authority registered successfully. Please verify your email.",
                Map.of("email", user.getEmail()));
    }
	    
	    public AuthorityProfileResponseDto mapToDto(AuthorityProfile profile) {
	        User user = profile.getUser();

	        return AuthorityProfileResponseDto.builder()
	                .id(profile.getId())
	                .authorityFullName(user.getFullName())
	                .email(user.getEmail())
	                .phoneNumber(PhoneUtil.formatToE164(profile.getPhoneNumber()))
	                .authorityType(profile.getAuthorityType().name())
	                .fullAddress(profile.getFullAddress())
	                .latitude(profile.getLatitude())
	                .longitude(profile.getLongitude())
	                .kyc(
	                    AuthorityProfileResponseDto.KycDto.builder()
	                            .profilePhoto(profile.getPhoto())
	                            .citizenshipFrontImage(profile.getCitizenshipFrontImage())
	                            .citizenshipBackImage(profile.getCitizenshipBackImage())
	                            .authorityIdentityCardImage(profile.getAuthorityIdentityCardImage())
	                            .build()
	                )
	                .build();
	    }

	    
  public AuthorityProfile getAuthorityById(Long id) { 
	   return authorityRepository.findById(id) 
			   .orElseThrow(() -> new BadRequestException("Authority not found")); }
}


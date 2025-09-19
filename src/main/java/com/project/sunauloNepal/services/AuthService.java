package com.project.sunauloNepal.services;

import com.project.sunauloNepal.ENUM.EmailStatus;
import com.project.sunauloNepal.ENUM.IdentityStatus;
import com.project.sunauloNepal.ENUM.OtpPurpose;
import com.project.sunauloNepal.ENUM.Role;
import com.project.sunauloNepal.config.JwtUtil;
import com.project.sunauloNepal.entities.AuthorityProfile;
import com.project.sunauloNepal.entities.User;
import com.project.sunauloNepal.exception.BadRequestException;
import com.project.sunauloNepal.repository.AuthorityProfileRepository;
import com.project.sunauloNepal.repository.UserRepository;
import com.project.sunauloNepal.requestDTO.*;
import com.project.sunauloNepal.responseDTO.ApiResponse;
import com.project.sunauloNepal.responseDTO.LocationDTO;
import com.project.sunauloNepal.responseDTO.LoginResponseDTO;
import com.project.sunauloNepal.responseDTO.UserResponseDTO;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final OtpService otpService;
    private final AuthorityProfileRepository authorityRepository;
   
//      Register new users     
    @Transactional
    public ApiResponse<Map<String, String>> register(RegisterRequestDTO request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BadRequestException("Email already exists");
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .emailStatus(EmailStatus.PENDING)
                .identityStatus(IdentityStatus.UNVERIFIED)
                .fullAddress(request.getFullAddress())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .build();

        userRepository.save(user);

        // Send verification OTP after registration
        otpService.generateOtpForUsers(user.getEmail(), OtpPurpose.VERIFY_EMAIL);

        return new ApiResponse<>(true,
                "User registered successfully. Please verify your email.",
                Map.of("email", user.getEmail()));
    }

// Login user
 
    @Transactional(readOnly = true)
    public ApiResponse<LoginResponseDTO> login(LoginRequestDTO request){
        try {
            //  Fetch user by email
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new BadRequestException("Email not registered"));

            //  Validate password
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                return new ApiResponse<>(false, "Invalid credentials", null);
            }

            //  Validate role and statuses
            Role role = user.getRole();
            switch (role) {
                case USER -> {
                    if (user.getEmailStatus() != EmailStatus.VERIFIED) {
                        return new ApiResponse<>(false, "Email is not verified", null);
                    }
                }
                case AUTHORITY -> {
                    if (user.getEmailStatus() != EmailStatus.VERIFIED) {
                        return new ApiResponse<>(false, "Email is not verified", null);
                    }
                    if (user.getIdentityStatus() != IdentityStatus.VERIFIED) {
                        return new ApiResponse<>(false, "Authority identity is not approved", null);
                    }
                }
                case ADMIN -> {
                    // No restrictions for admin
                }
                default -> {
                    return new ApiResponse<>(false, "Unknown role assigned", null);
                }
            }

            // 4. Generate JWT token
            String token = jwtUtil.generateToken(user.getId(), role);
            System.out.println("Token generated: " + token);

            // 5. Prepare user response DTO
            UserResponseDTO userResponse = UserResponseDTO.builder()
                    .fullName(user.getFullName())
                    .role(user.getRole())
                    .status(user.getIdentityStatus())
                    .build();

            //  Initialize locationResponse as null to avoid compilation error
            LocationDTO locationResponse = null;

            // 7. Prepare location based on role
            if (role == Role.AUTHORITY) {
                // Fetch location from AuthorityProfile
                System.out.println("Fetching authority profile for user ID " + user.getId());
                AuthorityProfile profile = authorityRepository.findById(user.getId())
                        .orElseThrow(() -> new BadRequestException("Authority profile not found"));

                System.out.println("Authority profile found: " + profile);

                locationResponse = LocationDTO.builder()
                        .fullAddress(profile.getFullAddress())
                        .latitude(profile.getLatitude())
                        .longitude(profile.getLongitude())
                        .build();
            } else if (role == Role.USER) {
                // Fetch location from User
                System.out.println("Building location from User entity");
                locationResponse = LocationDTO.builder()
                        .fullAddress(user.getFullAddress())
                        .latitude(user.getLatitude())
                        .longitude(user.getLongitude())
                        .build();
            }
            // For ADMIN or other roles, locationResponse remains null

            // 8. Build final login response DTO
            LoginResponseDTO loginResponse = LoginResponseDTO.builder()
                    .user(userResponse)
                    .location(locationResponse)
                    .token(token)
                    .build();

            return new ApiResponse<>(true, "Login successful", loginResponse);

        } catch (BadRequestException ex) {
            System.err.println("BadRequestException: " + ex.getMessage());
            return new ApiResponse<>(false, ex.getMessage(), null);
        } catch (Exception ex) {
            ex.printStackTrace(); // Print the stack trace for the server error
            return new ApiResponse<>(false, "Internal server error", null);
        }
    }
}
	
package com.project.sunauloNepal.services;

import com.project.sunauloNepal.ENUM.EmailStatus;
import com.project.sunauloNepal.ENUM.IdentityStatus;
import com.project.sunauloNepal.entities.AuthorityProfile;
import com.project.sunauloNepal.entities.User;
import com.project.sunauloNepal.entities.UserKYC;
import com.project.sunauloNepal.exception.BadRequestException;
import com.project.sunauloNepal.repository.AuthorityProfileRepository;
import com.project.sunauloNepal.repository.UserKYCRepository;
import com.project.sunauloNepal.repository.UserRepository;
import com.project.sunauloNepal.responseDTO.*;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final UserKYCRepository userKYCRepository;
    private final AuthorityProfileRepository authorityRepository;
    private final NotificationService notificationService;
    private final MailService mailService;

    // ---------------- User KYC ----------------
    public List<UserKycListDto> getPendingUsersKYC() {
        return userKYCRepository.findByStatus(IdentityStatus.PENDING)
                .stream()
                .map(kyc -> new UserKycListDto(
                        kyc.getId(),
                        kyc.getUser().getFullName(),
                        kyc.getUser().getEmail(),
                        kyc.getStatus()
                ))
                .toList();
    }

    public List<UserKycListDto> getVerifiedUsersKYC() {
        return userKYCRepository.findByStatus(IdentityStatus.VERIFIED)
                .stream()
                .map(kyc -> new UserKycListDto(
                        kyc.getId(),
                        kyc.getUser().getFullName(),
                        kyc.getUser().getEmail(),
                        kyc.getStatus()
                ))
                .toList();
    }

    public long countUsersByStatus(IdentityStatus status) {
        return userRepository.countByIdentityStatus(status);
    }

    public UserKycDetailDto getUserKycDetail(Long kycId) {
        UserKYC kyc = userKYCRepository.findById(kycId)
                .orElseThrow(() -> new BadRequestException("User KYC not found"));

        return new UserKycDetailDto(
                kyc.getId(),
                kyc.getUser().getFullName(),
                kyc.getUser().getEmail(),
                kyc.getDocumentFrontImage(),
                kyc.getDocumentBackImage(),
                kyc.getUserPhoto(),
                kyc.getStatus(),
                kyc.getSubmittedAt()
        );
    }

    public void approveUserKYC(Long kycId) {
        UserKYC userKYC = userKYCRepository.findById(kycId)
                .orElseThrow(() -> new BadRequestException("User KYC not found"));

        User user = userKYC.getUser();
        user.setIdentityStatus(IdentityStatus.VERIFIED);
        userRepository.save(user);

        String message = "Your KYC has been approved. You can now access full features.";
        notificationService.sendNotification(user, "KYC Approved", message);
        mailService.sendApprovalEmail(user);
    }

    public void rejectUserKYC(Long kycId, String reason) {
        UserKYC userKYC = userKYCRepository.findById(kycId)
                .orElseThrow(() -> new BadRequestException("User KYC not found"));

        User user = userKYC.getUser();
        user.setIdentityStatus(IdentityStatus.REJECTED);
        userRepository.save(user);

        String message = "Your KYC has been rejected. Reason: " + reason;
        notificationService.sendNotification(user, "KYC Rejected", message);
        mailService.sendRejectionEmail(user, reason);
    }

    // ---------------- Authority ----------------
    public List<AuthorityListDto> getPendingAuthorities() {
        return authorityRepository.findByUser_IdentityStatus(IdentityStatus.PENDING)
                .stream()
                .map(auth -> new AuthorityListDto(
                        auth.getId(),
                        auth.getUser().getFullName(),
                        auth.getUser().getEmail(),
                        auth.getAuthorityType(),
                        auth.getUser().getIdentityStatus()
                ))
                .toList();
    }

    public List<AuthorityListDto> getVerifiedAuthorities() {
        return authorityRepository.findByUser_IdentityStatus(IdentityStatus.VERIFIED)
                .stream()
                .map(auth -> new AuthorityListDto(
                        auth.getId(),
                        auth.getUser().getFullName(),
                        auth.getUser().getEmail(),
                        auth.getAuthorityType(),
                        auth.getUser().getIdentityStatus()
                ))
                .toList();
    }

    public long countAuthoritiesByStatus(IdentityStatus status) {
        return authorityRepository.countByUser_IdentityStatus(status);
    }

    public AuthorityDetailDto getAuthorityDetail(Long id) {
        AuthorityProfile auth = authorityRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Authority not found"));

        return new AuthorityDetailDto(
                auth.getId(),
                auth.getUser().getFullName(),
                auth.getUser().getEmail(),
                auth.getAuthorityType(),
                auth.getPhoto(),
                auth.getCitizenshipFrontImage(),
                auth.getCitizenshipBackImage(),
                auth.getAuthorityIdentityCardImage(),
                auth.getPhoneNumber(),
                auth.getFullAddress(),
                auth.getLatitude(),
                auth.getLongitude(),
                auth.getUser().getIdentityStatus()
        );
    }

    public void approveAuthority(Long id) {
        AuthorityProfile authority = authorityRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Authority not found"));

        User user = authority.getUser();
        user.setEmailStatus(EmailStatus.VERIFIED);
        user.setIdentityStatus(IdentityStatus.VERIFIED);
        userRepository.save(user);

        String message = "Your Authority profile has been approved.";
        notificationService.sendNotification(user, "Authority Approved", message);
        mailService.sendApprovalEmail(user);
    }

    public void rejectAuthority(Long id, String reason) {
        AuthorityProfile authority = authorityRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Authority not found"));

        User user = authority.getUser();
        user.setIdentityStatus(IdentityStatus.REJECTED);
        userRepository.save(user);

        String message = "Your Authority profile has been rejected. Reason: " + reason;
        notificationService.sendNotification(user, "Authority Rejected", message);
        mailService.sendRejectionEmail(user, reason);
    }
}

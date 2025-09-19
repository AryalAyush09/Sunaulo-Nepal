package com.project.sunauloNepal.services;

import com.project.sunauloNepal.ENUM.OtpPurpose;
import com.project.sunauloNepal.entities.User;

public interface MailService {
    void sendOtpEmail(User user, String otp, OtpPurpose purpose);
    void sendApprovalEmail(User user);   // KYC/Authority approved
    void sendRejectionEmail(User user, String reason); // KYC/Authority rejected
    void sendGeneralEmail(User user, String subject, String content);
}

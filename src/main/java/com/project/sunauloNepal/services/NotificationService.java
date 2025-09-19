package com.project.sunauloNepal.services;

import com.project.sunauloNepal.entities.AuthorityProfile;
import com.project.sunauloNepal.entities.Complaint;
import com.project.sunauloNepal.entities.Notification;
import com.project.sunauloNepal.entities.User;
import com.project.sunauloNepal.repository.NotificationRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final SmsService smsService;
    private final WhatsAppService whatsAppService;
//    private final FcmService fcmService;
    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    
    public void sendNotification(User user, String title, String message) {
        Notification notification = Notification.builder()
                .user(user)
                .title(title)
                .message(message)
                .isSeen(false)
                .createdAt(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);

        // real-time push via WebSocket
        messagingTemplate.convertAndSendToUser(
                user.getId().toString(),
                "/queue/notifications",
                notification
        );
    }

    public Page<Notification> getNotifications(Long userId, Pageable pageable) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    public void notifyAuthority(Complaint complaint) {
        AuthorityProfile authority = complaint.getAuthority();
        if(authority == null) {
            log.warn("No authority assigned for complaint {}", complaint.getId());
            return;
        }

        String message = "New Complaint: " + complaint.getTranscribedText();

        // 1️⃣ WebSocket
        try {
            messagingTemplate.convertAndSendToUser(
                authority.getUser().getId().toString(),
                "/queue/notifications",
                message
            );
        } catch (Exception e) {
            log.error("WebSocket notification failed: {}", e.getMessage());
        }

        // 2️⃣ Push Notification (FCM)
//        if(authority.getDeviceTokens() != null) {
//            try {
//                fcmService.sendNotification(authority.getDeviceTokens(), "New Complaint", message);
//            } catch (Exception e) {
//                log.error("FCM push failed: {}", e.getMessage());
//            }
//        }

        // 3️⃣ SMS (fallback)
        if(authority.getPhoneNumber() != null) {
            try {
                smsService.sendSms(authority.getPhoneNumber(), message);
            } catch (Exception e) {
                log.error("SMS send failed: {}", e.getMessage());
            }
        }

        // 4️⃣ WhatsApp for urgent complaints (urgency >=4)
        if(authority.getPhoneNumber() != null && complaint.getUrgency() != null && complaint.getUrgency() >= 4) {
            try {
                whatsAppService.sendWhatsAppMessage(authority.getPhoneNumber(), "Emergency Complaint: " + complaint.getTranscribedText());
            } catch (Exception e) {
                log.error("WhatsApp send failed: {}", e.getMessage());
            }
        }
    }
    
    
    
    public void markAsSeen(List<Long> ids, Long userId) {
        notificationRepository.markAsSeen(ids, userId);
    }

    public Long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsSeenFalse(userId);
    }
}

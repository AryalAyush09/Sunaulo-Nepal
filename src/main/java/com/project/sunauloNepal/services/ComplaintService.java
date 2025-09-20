package com.project.sunauloNepal.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.sunauloNepal.ENUM.AuthorityType;
import com.project.sunauloNepal.ENUM.ComplaintCategory;
import com.project.sunauloNepal.ENUM.ComplaintStatus;
import com.project.sunauloNepal.entities.AuthorityProfile;
import com.project.sunauloNepal.entities.Complaint;
import com.project.sunauloNepal.entities.Conversation;
import com.project.sunauloNepal.entities.User;
import com.project.sunauloNepal.exception.BadRequestException;
import com.project.sunauloNepal.repository.AuthorityCoveragePolygonRepository;
import com.project.sunauloNepal.repository.AuthorityProfileRepository;
import com.project.sunauloNepal.repository.ComplaintRepository;
import com.project.sunauloNepal.repository.UserRepository;
import com.project.sunauloNepal.requestDTO.ComplaintDto;
import com.project.sunauloNepal.responseDTO.ApiResponse;
import com.project.sunauloNepal.responseDTO.WebhookPayload;
import com.project.sunauloNepal.responseDTO.WebhookResponseDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Comparator;


@Service
@RequiredArgsConstructor
@Slf4j
public class ComplaintService {
    private final ComplaintRepository complaintRepository;
    private final AuthorityProfileRepository authorityProfileRepository;
    private final UserRepository userRepository;
    private final FileService fileService;
    private final WebhookService webhookService;
    private final AuthorityCoveragePolygonRepository coverageRepo;
//  private final GeoService geoService;
    private final SimpMessagingTemplate messagingTemplate; // WebSocket
    private final WebPushService webPushService;                 // Mobile Push
    private final SmsService smsService;  
    private final ChatService chatService;
    private final WhatsAppService whatsAppService;
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ApiResponse<Map<String, Object>> saveComplaint(Long userId, ComplaintDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found"));
      
        List<String> savedMediaPaths = handleMediaFiles(dto);
        
        Complaint complaint = Complaint.builder()
                .transcribedText(dto.getText())
                .mediaPaths(savedMediaPaths)
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .fullAddress(dto.getFullAddress())
                .user(user)
                .createdAt(LocalDateTime.now())
                .status(ComplaintStatus.PENDING)
                .build();

        Complaint saved = complaintRepository.save(complaint);

        AuthorityProfile assignedAuthority = null;
        ComplaintCategory category = null;
        String webhookMessage = null;
//
//        try {
//            WebhookPayload payload = new WebhookPayload();
//            payload.setComplaintId(saved.getId());
//            payload.setText(saved.getTranscribedText());
//
//            ApiResponse<String> webhookResponse = webhookService.sendComplaintWebhook(payload);
//
//            if (webhookResponse.isSuccess() && webhookResponse.getData() != null) {
//                WebhookResponseDTO responseDTO = objectMapper.readValue(webhookResponse.getData(), WebhookResponseDTO.class);
//                webhookMessage = responseDTO.getOutput().getMessage();
//
//                //  Assignment happens here
//                handleN8nCallback(responseDTO);
//
//                assignedAuthority = complaintRepository.findById(saved.getId())
//                        .orElseThrow(() -> new BadRequestException("Complaint not found after assignment"))
//                        .getAuthority();
//
//                category = complaintRepository.findById(saved.getId()).get().getCategory();
//            } else {
//                return ApiResponse.<Map<String, Object>>builder()
//                        .success(false)
//                        .message("Complaint saved but webhook call failed or returned invalid response")
//                        .build();
//            }
//        } catch (Exception ex) {
//            log.error("Error handling webhook or authority assignment", ex);
//            return ApiResponse.<Map<String, Object>>builder()
//                    .success(false)
//                    .message("Complaint saved but authority assignment failed: " + ex.getMessage())
//                    .build();
//        }
//
//        //  Response if all good
//        Map<String, Object> responseData = new HashMap<>();
//        responseData.put("complaintId", saved.getId());
//        responseData.put("status", ComplaintStatus.ASSIGNED);
//        responseData.put("category", category);
//        responseData.put("n8nMessage", webhookMessage);
//
//        if (assignedAuthority != null) {
//            responseData.put("authorityFullName", assignedAuthority.getFullName());
//            responseData.put("authorityContact", assignedAuthority.getPhoneNumber());
//        }
//
//        return ApiResponse.<Map<String, Object>>builder()
//                .success(true)
//                .message("Complaint registered and assigned successfully")
//                .data(responseData)
//                .build();
//    }

        try {
            if (dto.getAuthorityType() == null) {
                //  Complaint from voice → send to n8n
                webhookMessage = processViaWebhook(saved);
                assignedAuthority = saved.getAuthority();
                category = saved.getCategory();
            } else {
                // Complaint from ward-dashboard → direct assignment
            	AuthorityType authType = dto.getAuthorityType(); // ✅ already enum
                category = ComplaintCategory.valueOf(authType.name());
                complaint.setCategory(category);

                assignedAuthority = assignDirectToAuthority(complaint, dto.getAuthorityId());

                complaint.setStatus(ComplaintStatus.ASSIGNED);
                complaintRepository.save(complaint);

                webhookMessage = "Complaint assigned directly to authority.";
                notifyAuthority(assignedAuthority, complaint, null); // no n8n response
            }
        } catch (Exception ex) {
            log.error("Error handling complaint assignment", ex);
            return ApiResponse.<Map<String, Object>>builder()
                    .success(false)
                    .message("Complaint saved but assignment failed: " + ex.getMessage())
                    .build();
        }

        // 4. Response
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("complaintId", saved.getId());
        responseData.put("status", ComplaintStatus.ASSIGNED);
        responseData.put("category", category);
        responseData.put("n8nMessage", webhookMessage);

        if (assignedAuthority != null) {
            responseData.put("authorityFullName", assignedAuthority.getFullName());
            responseData.put("authorityContact", assignedAuthority.getPhoneNumber());
        }

        return ApiResponse.<Map<String, Object>>builder()
                .success(true)
                .message("Complaint registered and assigned successfully")
                .data(responseData)
                .build();
    }
    
    @Transactional
    private AuthorityProfile assignDirectToAuthority(Complaint complaint, Long authorityId) {
        AuthorityProfile authority;

        if (authorityId != null) {
        	log.error("Trying to assign complaint to Authority ID: {}", authorityId);
//            authority = authorityProfileRepository.findById(authorityId)
//                    .orElseThrow(() -> new BadRequestException("Authority not found"));
        	authority = authorityProfileRepository.findByUserId(authorityId)
        	        .orElseThrow(() -> new BadRequestException("Authority not found"));

        } else {
            //  WARD complaint → polygon based only
            List<AuthorityProfile> authorities =
                    authorityProfileRepository.findAllByAuthorityType(AuthorityType.WARD);

            List<Long> polygonMatchedIds = coverageRepo.findAuthoritiesByCategoryAndLocation(
                    complaint.getLongitude(),
                    complaint.getLatitude(),
                    AuthorityType.WARD.name()
            );

            authority = authorities.stream()
                    .filter(auth -> polygonMatchedIds.contains(auth.getId()))
                    .findFirst()
                    .orElseThrow(() -> new BadRequestException("No ward authority found for this location"));
        }

        complaint.setAuthority(authority);
        complaint.setStatus(ComplaintStatus.ASSIGNED);
        complaintRepository.save(complaint);

        return authority;
    }
    
    private List<String> handleMediaFiles(ComplaintDto dto) {
        List<String> savedMediaPaths = new ArrayList<>();

        if (dto.getMediaPaths() != null) {
            for (MultipartFile file : dto.getMediaPaths()) {
                if (!file.isEmpty()) {
                    String filePath;
                    String contentType = file.getContentType();

                    if (contentType.startsWith("image/")) {
                        filePath = fileService.saveImageFile(file, "complaints/media");
                    } else if (contentType.startsWith("video/")) {
                        filePath = fileService.saveVideoFile(file, "complaints/media");
                    } else {
                        throw new BadRequestException("Unsupported media type: " + contentType);
                    }

                    savedMediaPaths.add(filePath);
                }
            }
        }

        return savedMediaPaths;
    }

private String processViaWebhook(Complaint complaint) throws Exception {
    WebhookPayload payload = new WebhookPayload();
    payload.setComplaintId(complaint.getId());
    payload.setText(complaint.getTranscribedText());

    ApiResponse<String> webhookResponse = webhookService.sendComplaintWebhook(payload);

    if (!webhookResponse.isSuccess() || webhookResponse.getData() == null) {
        throw new BadRequestException("Webhook failed or returned empty response");
    }

    WebhookResponseDTO responseDTO = objectMapper.readValue(webhookResponse.getData(), WebhookResponseDTO.class);
    handleN8nCallback(responseDTO);

    return responseDTO.getOutput().getMessage();
}

    // Handles n8n response and updates the complaint
    @Transactional
    private void handleN8nCallback(WebhookResponseDTO dto) {
    	WebhookResponseDTO.Output output = dto.getOutput(); 	
        Complaint complaint = complaintRepository.findById(output.getComplaintId())
                .orElseThrow(() -> new BadRequestException("Complaint not found"));

        
        User user = complaint.getUser();
        // Example: update status and any other fields from n8n
        
        ComplaintCategory category;
        try {
              category = ComplaintCategory
            		.valueOf(output.getAuthorityType().toUpperCase());
            complaint.setCategory(category);
        } catch (IllegalArgumentException ex) {
            log.warn("Cannot map n8n response to ComplaintCategory: {}",
            		output.getAuthorityType());
            // Optional: either throw or set a default category
            throw new BadRequestException("Invalid complaint category received: "
            + output.getAuthorityType());
        }
//        List<Complaint> recentComplaints = complaintRepository
//                .findByUserIdAndCategoryAndCreatedAtAfter(
//                        user.getId(),
//                        category,
//                        LocalDateTime.now().minusHours(8)
//                );
//
//        if (!recentComplaints.isEmpty()) {
//            throw new BadRequestException(
//                    "You already submitted a complaint in this category recently. Please wait for response."
//            );
//        }  
        
        complaint.setCategory(category);
        complaint.setStatus(ComplaintStatus.ASSIGNED);
        complaintRepository.save(complaint);
        // You can also update authorityType, assignedAuthority, etc. here
        AuthorityProfile assignedAuthority = assignComplaintToAuthority(complaint);
         
        Conversation conversation = chatService.getOrCreateConversation(
                complaint, complaint.getUser(), assignedAuthority);

        chatService.sendMessage(
                conversation.getId(),   // correct id now
                "SYSTEM",
                "Your complaint has been assigned to " + assignedAuthority.getFullName()
        );

        log.info("Complaint {} assigned to authority {} with status {}", 
                complaint.getId(), assignedAuthority.getId(), complaint.getStatus());
        notifyAuthority(assignedAuthority, complaint, output);

    }  
    
    private void notifyAuthority(AuthorityProfile authority, Complaint complaint, 
    		WebhookResponseDTO.Output n8nData) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("complaintId", complaint.getId());
        payload.put("userText", complaint.getTranscribedText());   // Nepali text from user
        payload.put("n8nText", n8nData != null ? n8nData.getMessage() : "");               // English text from N8N/AI
        payload.put("fullAddress", complaint.getFullAddress());    // user address
        payload.put("userPhone", complaint.getUser().getPhoneNumber());
        payload.put("proof", complaint.getMediaPaths());
          
        //  WebSocket Broadcast
        messagingTemplate.convertAndSend(
                "/topic/authority/" + authority.getId() + "/complaints",
                payload
            );
  
        //  Mobile Push Notification
        ObjectMapper mapper = new ObjectMapper();

        if (authority.getDeviceTokens() != null) {
            for (String subscriptionJson : authority.getDeviceTokens()) {
                try {
                    Map<String, Object> subscription = mapper.readValue(subscriptionJson, new TypeReference<Map<String, Object>>() {});
                    webPushService.sendNotification(payload, subscription);
                } catch (Exception ex) {
                    log.error("Error sending web push to authority {}: {}", authority.getId(), ex.getMessage(), ex);
                }
            }
        }

        //  SMS via Sparrow
//        if (authority.getPhoneNumber() != null) {
//            String smsMessage = "New complaint assigned in your area. "
//                              + "Category: " + complaint.getCategory().name() + ". "
//                              + "Check the SunauloNepal for details.";
//            log.info("Sending SMS to {}: {}", authority.getPhoneNumber(), smsMessage);
//            smsService.sendSms(authority.getPhoneNumber(), smsMessage);
//        }
        
     // WhatsApp via Twilio (using same phone number field)
        if (authority.getPhoneNumber() != null) {
            String toWhatsapp = "whatsapp:" + authority.getPhoneNumber(); // e.g., whatsapp:+9779812345678

            String whatsappMsg = "📢 New Complaint Alert!\n"
                    + "Category: " + complaint.getCategory().name() + "\n"
                    + "Address: " + complaint.getFullAddress() + "\n"
                    + "User Phone: " + complaint.getUser().getPhoneNumber() + "\n\n"
                    + "Check the SunauloNepal system for full details.";
            try {
            	whatsAppService.sendWhatsAppMessage(toWhatsapp, whatsappMsg);
           
                log.info("WhatsApp message sent to authority {}", authority.getId());
            } catch (Exception e) {
                log.error("Error sending WhatsApp to authority {}: {}", authority.getId(), e.getMessage(), e);
            }
        }

        log.info("Notifications sent to authority {} via WebSocket, Push, and SMS", authority.getId());
    }  
    
    @Transactional
    public AuthorityProfile assignComplaintToAuthority(Complaint complaint) {
        validateComplaintCategory(complaint);

        //  Fetch authorities by category
        List<AuthorityProfile> authorities = getAuthoritiesByCategory(complaint);

        //  Try polygon-based match (skip for emergencies)
        AuthorityProfile selectedAuthority = null;
        if (!isEmergencyCategory(complaint.getCategory())) {
            selectedAuthority = findAuthorityByPolygon(complaint, authorities);
        }

        //  If no polygon match or emergency → fallback to nearest
        if (selectedAuthority == null) {
            selectedAuthority = findNearestAuthority(complaint, authorities);
        }

        //  Update complaint
        assignComplaint(complaint, selectedAuthority);

        return selectedAuthority;
    }

    private boolean isEmergencyCategory(ComplaintCategory category) {
        return category == ComplaintCategory.POLICE || 
        		category == ComplaintCategory.FIRE;
    }

    private void validateComplaintCategory(Complaint complaint) {
        if (complaint.getCategory() == null) {
            throw new BadRequestException("Complaint category is missing");
        }
    }

    private List<AuthorityProfile> getAuthoritiesByCategory(Complaint complaint) {
        List<AuthorityProfile> authorities = authorityProfileRepository
                .findAllByAuthorityType(AuthorityType.valueOf(complaint.getCategory().name()));

        if (authorities.isEmpty()) {
            throw new BadRequestException("No authorities found for this complaint category");
        }
        return authorities;
    }

    private AuthorityProfile findAuthorityByPolygon(Complaint complaint, List<AuthorityProfile> authorities) {
        List<Long> polygonMatchedIds = coverageRepo.findAuthoritiesByCategoryAndLocation(
                complaint.getLongitude(),
                complaint.getLatitude(),
                complaint.getCategory().name()
        );

        return authorities.stream()
                .filter(auth -> polygonMatchedIds.contains(auth.getId()))
                .findFirst()
                .orElse(null);
    }

    private AuthorityProfile findNearestAuthority(Complaint complaint, List<AuthorityProfile> authorities) {
        return authorities.stream()
                .min(Comparator.comparingDouble(auth ->
                        calculateDistance(
                                complaint.getLatitude(),
                                complaint.getLongitude(),
                                auth.getLatitude(),
                                auth.getLongitude()
                        )))
                .orElse(null);
    }

    private void assignComplaint(Complaint complaint, AuthorityProfile selectedAuthority) {
        complaint.setAuthority(selectedAuthority);
        complaint.setStatus(ComplaintStatus.ASSIGNED);
        complaintRepository.save(complaint);
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    private WebhookPayload buildWebhookPayload(Complaint saved, User user) {
        WebhookPayload payload = new WebhookPayload();
        payload.setText(saved.getTranscribedText());
        return payload;
    }

}	
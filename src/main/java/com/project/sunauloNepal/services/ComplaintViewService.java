//package com.project.sunauloNepal.services;
//
//import java.util.List;
//
//import org.springframework.stereotype.Service;
//
//import com.project.sunauloNepal.entities.Complaint;
//import com.project.sunauloNepal.repository.ComplaintRepository;
//import com.project.sunauloNepal.repository.MessageRepository;
//import com.project.sunauloNepal.requestDTO.ComplaintDto;
//import com.project.sunauloNepal.responseDTO.ChatMessageDto;
//
//import lombok.RequiredArgsConstructor;
//
//@Service
//@RequiredArgsConstructor
//public class ComplaintViewService {
//
//    private final ComplaintRepository complaintRepo;
//    private final MessageRepository messageRepo;
//
//    public ComplaintDto getComplaintForAuthority(Long complaintId) {
//        Complaint complaint = complaintRepo.findById(complaintId)
//                .orElseThrow(() -> new RuntimeException("Complaint not found"));
//
//        return ComplaintDto.builder()
//                .complaintId(complaint.getId())
//                .description(complaint.getDescription())
//                .userName(complaint.getUser().getFullName())
//               .phoneNumber(complaint.getUser().getPhoneNumber())
//                .photoUrl(complaint.getPhotoUrl())
//                .videoUrl(complaint.getVideoUrl())
//                .latitude(complaint.getLatitude())
//                .longitude(complaint.getLongitude())
//                .messages(toDto(messageRepo.findByComplaintId(complaintId)))
//                .build();
//    }
//
//    public PublicComplaintDto getComplaintForPublic(Long complaintId) {
//        Complaint complaint = complaintRepo.findById(complaintId)
//                .orElseThrow(() -> new RuntimeException("Complaint not found"));
//
//        return PublicComplaintDto.builder()
//                .complaintId(complaint.getId())
//                .description(complaint.getDescription())
//                .latitude(complaint.getLatitude())
//                .longitude(complaint.getLongitude())
//                .messages(toDto(messageRepo.findByComplaintId(complaintId)))
//                .build();
//    }
//
//    private List<ChatMessageDto> toDto(List<Message> messages) {
//        return messages.stream()
//               .map(m -> ChatMessageDto.builder()
//                        .senderType(m.getSenderType())
//                        .content(m.getContent())
//                        .build())
//               .toList();
//    }
//}
//
//
//

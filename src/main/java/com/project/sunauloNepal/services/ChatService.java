package com.project.sunauloNepal.services;

import com.project.sunauloNepal.entities.AuthorityProfile;
import com.project.sunauloNepal.entities.Complaint;
import com.project.sunauloNepal.entities.Conversation;
import com.project.sunauloNepal.entities.Message;
import com.project.sunauloNepal.entities.User;
import com.project.sunauloNepal.repository.ConversationRepository;
import com.project.sunauloNepal.repository.MessageRepository;
import com.project.sunauloNepal.repository.UserRepository;
import com.project.sunauloNepal.responseDTO.ChatMessageDto;
import com.project.sunauloNepal.responseDTO.ConversationMessagesDto;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ConversationRepository conversationRepo;
    private final MessageRepository messageRepo;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepo; 

//   
//      Get or create a conversation for a complaint between user and authority
//     
    public Conversation getOrCreateConversation(Complaint complaint, User user, 
    		AuthorityProfile authority) {
        return conversationRepo.findByComplaintId(complaint.getId())
                .orElseGet(() -> {
                    Conversation conv = Conversation.builder()
                    		 .complaint(complaint)
                             .user(user)
                             .authority(authority)
                             .build();
                    return conversationRepo.save(conv);
                });
    }
    
    public Message sendMessage(ChatMessageDto dto) {
        Conversation conv = conversationRepo.findById(dto.getConversationId())
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        Message msg = Message.builder()
                .conversation(conv)
                .senderType(dto.getSenderType())  // "USER" or "AUTHORITY"
                .content(dto.getContent())
                .timestamp(LocalDateTime.now())
                .build();

        Message saved = messageRepo.save(msg);

        // enrich DTO before sending back
        dto.setTimestamp(saved.getTimestamp());
        dto.setConversationId(conv.getId());

        // WebSocket broadcast
        messagingTemplate.convertAndSend("/topic/conversation/" + conv.getId(), dto);

        return saved;
    }

    /**
     * Save message and broadcast via WebSocket
     */
    public Message sendMessage(Long conversationId, String senderType, String content) {
        Conversation conv = conversationRepo.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        Message msg = Message.builder()
                .conversation(conv)
                .senderType(senderType) // "USER" or "AUTHORITY"
                .content(content)
                .timestamp(LocalDateTime.now())
                .build();

        Message saved = messageRepo.save(msg);

        // broadcast to topic for real-time chat
        ChatMessageDto dto = ChatMessageDto.builder()
                .senderType(senderType)
                .content(content)
                .timestamp(saved.getTimestamp())
                .build();

        messagingTemplate.convertAndSend("/topic/conversation/" + conversationId, dto);

        return saved;
    }
    
    /**
     * Authority broadcast to all users in coverage
     */
    public void broadcastMessageFromAuthority(Long authorityId, String content) {
        // 1. get all users inside authority coverage
        List<User> usersInCoverage = userRepo.findUsersWithinCoverage(authorityId);

        // 2. loop through each user → get or create conversation
        for (User u : usersInCoverage) {
            Conversation conv = conversationRepo.findByUserIdAndAuthorityId(u.getId(), authorityId)
                    .orElseGet(() -> {
                        Conversation c = Conversation.builder()
                                .user(u)
                                .authority(new AuthorityProfile(authorityId)) // minimal ref
                                .build();
                        return conversationRepo.save(c);
                    });

            // 3. save message
            Message msg = Message.builder()
                    .conversation(conv)
                    .senderType("AUTHORITY")
                    .content(content)
                    .timestamp(LocalDateTime.now())
                    .build();
            Message saved = messageRepo.save(msg);

            // 4. broadcast to websocket topic
            ChatMessageDto dto = ChatMessageDto.builder()
                    .senderType("AUTHORITY")
                    .content(content)
                    .timestamp(saved.getTimestamp())
                    .build();

            messagingTemplate.convertAndSend("/topic/conversation/" + conv.getId(), dto);
        }
    }
    
    public Conversation getConversationById(Long conversationId) {
        return conversationRepo.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));
    }
    
    public ConversationMessagesDto getConversationData(Long conversationId) {
        Conversation conv = getConversationById(conversationId);
        List<Message> messages = getConversationMessages(conversationId);

        List<ChatMessageDto> messageDtos = messages.stream()
                .map(m -> ChatMessageDto.builder()
                        .conversationId(conv.getId())
                        .senderType(m.getSenderType())
                        .content(m.getContent())
                        .timestamp(m.getTimestamp())
                        .build())
                .toList();

        return new ConversationMessagesDto(
                conv.getId(),
                conv.getAuthority().getId(),
                conv.getUser().getId(),
                messageDtos
        );
    }


//      Get all messages for a conversation     
    public List<Message> getConversationMessages(Long conversationId) {
        return messageRepo.findByConversationIdOrderByTimestampAsc(conversationId);
    }
}
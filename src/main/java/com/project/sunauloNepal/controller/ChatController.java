package com.project.sunauloNepal.controller;

import com.project.sunauloNepal.entities.Conversation;
import com.project.sunauloNepal.entities.Message;
import com.project.sunauloNepal.responseDTO.ChatMessageDto;
import com.project.sunauloNepal.responseDTO.ConversationMessagesDto;
import com.project.sunauloNepal.services.ChatService;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    // Client sends chat message here
//    @MessageMapping("/sendMessage")
//    public void receiveMessage(ChatMessageDto message) {
//        chatService.sendMessage(
//                message.getConversationId(),
//                message.getSenderType(),
//                message.getContent()
//        );
//    }

    
    @MessageMapping("/sendMessage")
    @SendToUser("/queue/sent")
    public ChatMessageDto receiveMessage(ChatMessageDto message) {
        chatService.sendMessage(message);
        return message; // includes timestamp after save
    }

    
    // Client requests all messages for a conversation
    @MessageMapping("/getConversation")
    @SendToUser("/queue/conversation")
    public ConversationMessagesDto getConversation(Long conversationId) {
        Conversation conversation = chatService.getConversationById(conversationId);
        List<Message> messages = chatService.getConversationMessages(conversationId);

        return new ConversationMessagesDto(conversation, messages);
    }

}

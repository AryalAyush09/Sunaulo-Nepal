package com.project.sunauloNepal.responseDTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

import com.project.sunauloNepal.entities.Conversation;
import com.project.sunauloNepal.entities.Message;

@Data
@AllArgsConstructor
public class ConversationMessagesDto {
    private Long conversationId;
    private Long authorityId;
    private Long userId;
    private List<ChatMessageDto> messages;

    //  Custom constructor for easy conversion
    public ConversationMessagesDto(Conversation conversation, List<Message> messages) {
        this.conversationId = conversation.getId();
        this.authorityId = conversation.getAuthority().getId();
        this.userId = conversation.getUser().getId();

        this.messages = messages.stream()
                .map(m -> ChatMessageDto.builder()
                        .conversationId(conversation.getId())
                        .senderType(m.getSenderType())
                        .content(m.getContent())
                        .timestamp(m.getTimestamp())
                        .build()
                )
                .collect(Collectors.toList());
    }
}

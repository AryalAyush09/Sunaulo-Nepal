package com.project.sunauloNepal.responseDTO;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

//@Data
//@Builder
//public class ChatMessageDto {
//    private String senderType; // "AUTHORITY" or "USER"
//    private String content;    // message text
//    private LocalDateTime timestamp; // message sent time
//}
//

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDto {
    private Long conversationId;
    private Long senderId;
//    private Long receiverId;
    private String senderType;
    private String content;
    private LocalDateTime timestamp;
}

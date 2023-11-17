package com.talk.notificationservice.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupMessageRepliedEvent {

    private Message message; // the message that is being replied to
    private Replier sender;

    @Data
    public static class Message {
        private String id;
        private String senderId;
        private String content;
        private String createdAt;
        private String conversationId;
        private long messageNo;
    }


    @Data
    public static class Replier {
        private String id;
        private String username;
        private String displayName;
        private String avatar;
        private String email;
    }

}

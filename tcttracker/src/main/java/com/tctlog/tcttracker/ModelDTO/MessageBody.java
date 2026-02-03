package com.tctlog.tcttracker.ModelDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageBody {
    private String subject;
    private String from;
    private String body;
    private String sender;
    private Boolean isRead;
    private String ReceivedAt;

}

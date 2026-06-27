package com.example.notifyMe;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SendMessageRequest {

    private String chat_id;
    private String text;
}

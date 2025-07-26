package com.chatbot.thinker.service.openai.dto.request;

import java.util.ArrayList;
import java.util.List;

import com.chatbot.thinker.service.openai.interfaces.Message;

public class CreateThreadRequestDto {
    public List<Message> messages = new ArrayList<Message>();
}

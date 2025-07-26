package com.chatbot.thinker.service.openai.dto.request;

import java.util.ArrayList;
import java.util.List;

import com.chatbot.thinker.service.openai.interfaces.CreateRun;
import com.chatbot.thinker.service.openai.interfaces.Message;

public class CreateThreadAndRunRequestDto {
    public String assistant_id;
    public List<Message> messages = new ArrayList<>();
    public List<CreateRun.Tool> tools = new ArrayList<>();
}

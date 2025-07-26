package com.chatbot.thinker.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import com.chatbot.thinker.config.AppConfigProperties;
import com.chatbot.thinker.service.openai.OpenAIClient;
import com.chatbot.thinker.service.openai.dto.request.CreateThreadAndRunRequestDto;
import com.chatbot.thinker.service.openai.interfaces.Message;

@Component
public class WebSocketServer implements WebSocketHandler {
    private final AppConfigProperties appConfig;

    public WebSocketServer(AppConfigProperties appConfig) {
        this.appConfig = appConfig;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("Cliente conectado: " + session.getId());
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        String userMessage = (String) message.getPayload();

        OpenAIClient openAIClient_Thinker = new OpenAIClient(session, appConfig.getOpenaiApikeyId());

        OpenAIClient openAIClient_Consumer = new OpenAIClient(session, appConfig.getOpenaiApikeyId());

        OpenAIClient openAIClient_Validator = new OpenAIClient(session, appConfig.getOpenaiApikeyId());

        Message newMessage_Thinker = new Message();
        newMessage_Thinker.content = "User Awnser: " + userMessage;
        newMessage_Thinker.role = "user";

        CreateThreadAndRunRequestDto createThreadAndRunRequestDto_Thinker = new CreateThreadAndRunRequestDto();
        createThreadAndRunRequestDto_Thinker.messages.add(newMessage_Thinker);
        createThreadAndRunRequestDto_Thinker.assistant_id = appConfig.getThinkerId();

        openAIClient_Thinker.createThreadAndRunWithStream(createThreadAndRunRequestDto_Thinker, data_thinker -> {
            // session.sendMessage(new
            // TextMessage(data_thinker.delta.content.getFirst().text.value));

            if (data_thinker.object.contains("thread.message") && data_thinker.status.contains("completed")) {
                session.sendMessage(new TextMessage("Reasoning: " + data_thinker.content.getFirst().text.value));

                Message newMessage_Consumer = new Message();
                newMessage_Consumer.content = "Reasoning: " + data_thinker.content.getFirst().text.value;
                newMessage_Consumer.role = "assistant";

                CreateThreadAndRunRequestDto createThreadAndRunRequestDto_Consumer = new CreateThreadAndRunRequestDto();
                createThreadAndRunRequestDto_Consumer.messages.add(newMessage_Consumer);
                createThreadAndRunRequestDto_Consumer.messages.add(newMessage_Thinker);
                createThreadAndRunRequestDto_Consumer.assistant_id = appConfig.getConsumerId();

                openAIClient_Consumer.createThreadAndRunWithStream(createThreadAndRunRequestDto_Consumer,
                        data_consumer -> {
                            // session.sendMessage(new
                            // TextMessage(data_consumer.delta.content.getFirst().text.value));

                            if (data_consumer.object.contains("thread.message")
                                    && data_consumer.status.contains("completed")) {
                                session.sendMessage(new TextMessage(
                                        "First Response: " + data_consumer.content.getFirst().text.value));

                                Message newMessage_Validator = new Message();
                                newMessage_Validator.content = "First Response: "
                                        + data_consumer.content.getFirst().text.value;
                                newMessage_Validator.role = "assistant";

                                CreateThreadAndRunRequestDto createThreadAndRunRequestDto_Validator = new CreateThreadAndRunRequestDto();
                                createThreadAndRunRequestDto_Validator.messages.add(newMessage_Thinker);
                                createThreadAndRunRequestDto_Validator.messages.add(newMessage_Consumer);
                                createThreadAndRunRequestDto_Validator.messages.add(newMessage_Validator);
                                createThreadAndRunRequestDto_Validator.assistant_id = appConfig.getValidatorId();

                                openAIClient_Validator.createThreadAndRunWithStream(
                                        createThreadAndRunRequestDto_Validator,
                                        data_validator -> {
                                            // session.sendMessage(new
                                            // TextMessage(data_validator.delta.content.getFirst().text.value));

                                            if (data_validator.object.contains("thread.message")
                                                    && data_validator.status.contains("completed")) {
                                                session.sendMessage(
                                                        new TextMessage("Validator Response: "
                                                                + data_validator.content.getFirst().text.value));

                                                Message newMessage = new Message();
                                                newMessage.content = "Validator Response: "
                                                        + data_consumer.content.getFirst().text.value;
                                                newMessage.role = "assistant";

                                                CreateThreadAndRunRequestDto createThreadAndRunRequestDto = new CreateThreadAndRunRequestDto();
                                                createThreadAndRunRequestDto.messages.add(newMessage_Thinker);
                                                createThreadAndRunRequestDto.messages
                                                        .add(newMessage_Consumer);
                                                createThreadAndRunRequestDto.messages
                                                        .add(newMessage_Validator);
                                                createThreadAndRunRequestDto.assistant_id = appConfig.getConsumerId();

                                                openAIClient_Consumer.createThreadAndRunWithStream(
                                                        createThreadAndRunRequestDto,
                                                        data -> {
                                                            // session.sendMessage(new
                                                            // TextMessage(data.delta.content.getFirst().text.value));

                                                            if (data.object.contains("thread.message")
                                                                    && data.status.contains("completed")) {
                                                                session.sendMessage(new TextMessage(
                                                                        "Response: " + data.content
                                                                                .getFirst().text.value));

                                                            }
                                                        });
                                            }
                                        });
                            }
                        });
            }
        });
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.out.println("Erro de transporte: " + exception.getMessage());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        System.out.println("Conexão fechada com: " + session.getId());
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}

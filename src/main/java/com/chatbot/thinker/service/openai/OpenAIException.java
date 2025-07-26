package com.chatbot.thinker.service.openai;

public class OpenAIException extends Exception {
    public OpenAIException(String message) {
        super(message);
    }

    public OpenAIException(String message, Throwable cause) {
        super(message, cause);
    }
}
package com.chatbot.thinker.service.openai.interfaces;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

public class ThreadRun {
    @JsonProperty("id")
    public String id = new String();

    @JsonProperty("run_id")
    public String runId = new String();

    @JsonProperty("attachments")
    public List<Object> attachments;

    @JsonProperty("type")
    public String type = new String();

    @JsonProperty("object")
    public String object = new String();

    @JsonProperty("assistant_id")
    public String assistantId = new String();

    @JsonProperty("thread_id")
    public String threadId = new String();

    @JsonProperty("status")
    public String status = new String();
    
    @JsonProperty("created_at")
    public Long createdAt;

    @JsonProperty("incomplete_at")
    public Long incompleteAt;

    @JsonProperty("started_at")
    public Long startedAt;

    @JsonProperty("expires_at")
    public Long expiresAt;

    @JsonProperty("cancelled_at")
    public Long cancelledAt;

    @JsonProperty("failed_at")
    public Long failedAt;

    @JsonProperty("completed_at")
    public Long completedAt;

    @JsonProperty("required_action")
    public String requiredAction = new String();

    @JsonProperty("last_error")
    public String lastError = new String();

    @JsonProperty("model")
    public String model = new String();

    @JsonProperty("instructions")
    public String instructions = new String();

    @JsonProperty("tools")
    public List<Tool> tools;

    @JsonProperty("tool_resources")
    public Object toolResources;

    @JsonProperty("metadata")
    public Map<String, Object> metadata;

    @JsonProperty("temperature")
    public Double temperature;

    @JsonProperty("top_p")
    public Double topP;

    @JsonProperty("reasoning_effort")
    public Integer reasoningEffort;

    @JsonProperty("max_completion_tokens")
    public Integer maxCompletionTokens;

    @JsonProperty("max_prompt_tokens")
    public Integer maxPromptTokens;

    @JsonProperty("truncation_strategy")
    public TruncationStrategy truncationStrategy;

    @JsonProperty("incomplete_details")
    public IncompleteDetails incompleteDetails;

    @JsonProperty("usage")
    public Usage usage;

    @JsonProperty("response_format")
    public Object responseFormat;

    @JsonProperty("tool_choice")
    public String toolChoice = new String();

    @JsonProperty("parallel_tool_calls")
    public Boolean parallelToolCalls;

    @JsonProperty("step_details")
    public StepDetails stepDetails;

    @JsonProperty("content")
    public List<MessageContent> content;

    @JsonProperty("role")
    public String role = new String();

    @JsonProperty("delta")
    public Delta delta;

    // Getters e setters...

    public static class TruncationStrategy {
        @JsonProperty("type")
        public String type = new String();

        @JsonProperty("last_messages")
        public List<Map<String, Object>> lastMessages;

        // Getters e setters...
    }

    public static class IncompleteDetails {
        // Definição para incomplete_details
    }

    public static class Usage {
        @JsonProperty("prompt_tokens")
        public Integer promptTokens;

        @JsonProperty("completion_tokens")
        public Integer completionTokens;

        @JsonProperty("total_tokens")
        public Integer totalTokens;

        @JsonProperty("prompt_token_details")
        public Object prompt_token_details;

        @JsonProperty("completion_tokens_details")
        public CompletionTokensDetails completionTokensDetails;

        // Getters e setters...
    }

    public static class CompletionTokensDetails{
        @JsonProperty("reasoning_tokens")
        public Integer reasoningTokens;
    }

    public static class StepDetails {
        @JsonProperty("type")
        public String type = new String();

        @JsonProperty("message_creation")
        public MessageCreation messageCreation;

        public static class MessageCreation {
            @JsonProperty("message_id")
            public String messageId = new String();

            // Getters e setters...
        }

        // Getters e setters...
    }

    public static class MessageContent {
        @JsonProperty("index")
        public Integer index;

        @JsonProperty("type")
        public String type = new String();

        @JsonProperty("text")
        public TextContent text;

        public static class TextContent {
            @JsonProperty("value")
            public String value = new String();

            @JsonProperty("annotations")
            public List<Map<String, Object>> annotations;

            // Getters e setters...
        }

        // Getters e setters...
    }

    public static class Delta {
        @JsonProperty("content")
        public List<MessageContent> content;

        // Getters e setters...
    }

    public static class Tool {
        @JsonProperty("type")
        public String type = new String();

        @JsonProperty("function")
        public Function function;

        // Getters and setters
    }

    public static class Function {
        @JsonProperty("name")
        public String name = new String();

        @JsonProperty("description")
        public String description = new String();

        @JsonProperty("parameters")
        public Parameters parameters;
    }

    public static class Parameters {
        @JsonProperty("type")
        public String type = new String();

        @JsonProperty("properties")
        public Map<String, Property> properties;

        @JsonProperty("required")
        public List<String> required;
    }

    public static class Property {
        @JsonProperty("type")
        public String type = new String();

        @JsonProperty("description")
        public String description = new String();

        @JsonProperty("enum")
        public List<String> enumValues;
    }
}

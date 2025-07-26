package com.chatbot.thinker.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.constraints.NotBlank;

@Validated
@ConfigurationProperties(prefix = "app")
public class AppConfigProperties {
    @NotBlank
    private String thinkerId;
    private String consumerId;
    private String validatorId;
    private String openaiApikey;

    // Getters e Setters
    public String getThinkerId() {
        return thinkerId;
    }

    public void setThinkerId(String thinkerId) {
        this.thinkerId = thinkerId;
    }
    
    public String getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(String consumerId) {
        this.consumerId = consumerId;
    }

    public String getValidatorId() {
        return validatorId;
    }

    public void setValidatorId(String validatorId) {
        this.validatorId = validatorId;
    }

    public String getOpenaiApikeyId() {
        return openaiApikey;
    }

    public void setOpenaiApikey(String openaiApikey) {
        this.openaiApikey = openaiApikey;
    }
}
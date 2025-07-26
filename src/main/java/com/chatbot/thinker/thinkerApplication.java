package com.chatbot.thinker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.chatbot.thinker.config.AppConfigProperties;

@SpringBootApplication
@EnableConfigurationProperties(AppConfigProperties.class)
public class thinkerApplication {

	public static void main(String[] args) {
		SpringApplication.run(thinkerApplication.class, args);
	}

}

package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import dev.langchain4j.model.openai.OpenAiChatModel;

@Configuration
public class MyConfig {
	
	@Bean
	OpenAiChatModel rudeAiChatModel() {
		return OpenAiChatModel.builder()
				.baseUrl("http://langchain4j.dev/demo/openai/v1")
				.modelName("gpt-4o-mini")
				.apiKey("demo")
				.build();
	}
}

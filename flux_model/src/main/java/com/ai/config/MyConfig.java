package com.ai.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.request.ChatRequestParameters;
import dev.langchain4j.model.chat.request.ResponseFormat;
import dev.langchain4j.model.chat.request.ResponseFormatType;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;

@Configuration
public class MyConfig {
	
	@Bean
	public static StreamingChatModel openAiStreamingChatModel() {
		
		return OpenAiStreamingChatModel.builder()
				.baseUrl("http://langchain4j.dev/demo/openai/v1")
				.apiKey("demo")
				.modelName("gpt-4o-mini")
				.defaultRequestParameters(ChatRequestParameters.builder()
						.responseFormat(ResponseFormat.builder()
								.type(ResponseFormatType.TEXT)
								.build())
						.build())
				.build();
	}
	@Bean
	public static ChatModel openAiChatModel() {
		return OpenAiChatModel.builder()
				.baseUrl("http://langchain4j.dev/demo/openai/v1")
				.apiKey("demo")
				.modelName("gpt-4o-mini")
				.defaultRequestParameters(ChatRequestParameters.builder()
						.responseFormat(ResponseFormat.builder()
								.type(ResponseFormatType.TEXT)
								.build())
						.build())
				.logRequests(true)
				.logResponses(true)
				.build();
	}
}

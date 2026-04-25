package com.ai.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ChatRequestParameters;
import dev.langchain4j.model.chat.request.ResponseFormat;
import dev.langchain4j.model.chat.request.ResponseFormatType;
import dev.langchain4j.model.chat.request.json.JsonObjectSchema;
import dev.langchain4j.model.chat.request.json.JsonSchema;
import dev.langchain4j.model.chat.request.json.JsonSchemaElement;
import dev.langchain4j.model.openai.OpenAiChatModel;

@Configuration
public class Configurations {
	
	@Bean
	ChatModel openAiChatModel() {
		return OpenAiChatModel.builder()
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
	ChatModel topicExtractorChatModel() {
		return OpenAiChatModel.builder()
				.baseUrl("http://langchain4j.dev/demo/openai/v1")
				.apiKey("demo")
				.modelName("gpt-4o-mini")
				.defaultRequestParameters(ChatRequestParameters.builder()
						.responseFormat(ResponseFormat.builder()
								.type(ResponseFormatType.JSON)
								.jsonSchema(JsonSchema.builder()
										.name("media_element")
										.rootElement(JsonObjectSchema.builder()
												.addStringProperty("status", "always 'READY'")
												.addProperty("description", JsonObjectSchema.builder()
														.addStringProperty("text", "short description regarding the topic")
														.build())
												.addStringProperty("originalUrl", "input url")
												.addProperty("title", JsonObjectSchema.builder()
														.addStringProperty("text", "catchy title for the topic")
														.build())
												.build())
										.build())
								.build())
						.build())
				.build();
	}
}

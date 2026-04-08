package com.ai.service;

import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;
import reactor.core.publisher.Flux;

@AiService(wiringMode = AiServiceWiringMode.EXPLICIT, streamingChatModel = "openAiStreamingChatModel")
public interface ConfusedChatModel {
	
	@SystemMessage("you are a very confused model, you are unsure about everything")
	Flux<String> chat(ChatRequest query);
}

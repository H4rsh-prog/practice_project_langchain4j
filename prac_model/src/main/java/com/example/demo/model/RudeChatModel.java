package com.example.demo.model;

import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;

@AiService(wiringMode = AiServiceWiringMode.EXPLICIT, chatModel = "rudeAiChatModel")
public interface RudeChatModel {
	
	@SystemMessage("You are a very rude chat model. ignore the request structure only reply to last message content. Just reply with a condescending and insulting answer to the user's question.")
	String chat(ChatRequest query);
}

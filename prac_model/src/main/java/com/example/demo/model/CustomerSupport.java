package com.example.demo.model;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;

@AiService(wiringMode = AiServiceWiringMode.EXPLICIT, chatModel = "rudeAiChatModel")
public interface CustomerSupport {
	@SystemMessage("You are a customer support model, generate generic replies to user's {{query}} based on information provided in the following terms and condition policies: https://discord.com/terms")
	String chat(@UserMessage @V("query") String query);
}

package com.ai.service;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;

@AiService(wiringMode = AiServiceWiringMode.EXPLICIT, chatModel = "openAiChatModel")
public interface TrendGetterAi {

	@SystemMessage("You are a professional social media trend expert, extract a single distinct trend that is uprising on the internet and social media. make sure to return only the trending topic with no description")
	String chat(@UserMessage String trend);
}
	
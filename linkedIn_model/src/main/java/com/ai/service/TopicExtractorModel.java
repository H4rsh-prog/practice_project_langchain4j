package com.ai.service;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;

@AiService(wiringMode = AiServiceWiringMode.EXPLICIT, chatModel = "topicExtractorChatModel")
public interface TopicExtractorModel {
	
	@SystemMessage("You are an professional website scraper, given a {{url}} extract the topic mentioned on the page")
	String chat(@UserMessage @V("url") String url);
}

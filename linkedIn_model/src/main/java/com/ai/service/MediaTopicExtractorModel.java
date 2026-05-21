package com.ai.service;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;

@AiService(wiringMode = AiServiceWiringMode.EXPLICIT, chatModel = "mediaTopicExtractorChatModel")
public interface MediaTopicExtractorModel {
	
	@SystemMessage("You are an professional media analyser, given a {{media}} extract the topic mentioned on the page")
	String chat(@UserMessage @V("media") dev.langchain4j.data.message.UserMessage media);
}

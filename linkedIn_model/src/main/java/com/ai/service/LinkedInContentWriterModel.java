package com.ai.service;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;

@AiService(wiringMode = AiServiceWiringMode.EXPLICIT, chatModel = "openAiChatModel")
public interface LinkedInContentWriterModel {

	@SystemMessage("You are a professional social media content writer for LinkedIn posts, while keeping a professional tone generate content based on given {{topic}} with minimal formatting and not exceeding 8 lines, do not acknowledge any urls or input of any kind")
	String chat(@UserMessage @V("topic") String topic);
	
	@SystemMessage("You are a professional social media content writer for LinkedIn posts, while keeping a professional tone generate content upon analysing given {{content}} with minimal formatting and not exceeding 8 lines")
	String chat(@UserMessage @V("content") dev.langchain4j.data.message.UserMessage content);
}

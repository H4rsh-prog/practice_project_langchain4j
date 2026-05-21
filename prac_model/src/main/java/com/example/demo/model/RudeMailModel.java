package com.example.demo.model;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;

@AiService(wiringMode = AiServiceWiringMode.EXPLICIT, chatModel = "rudeAiChatModel")
public interface RudeMailModel {
	
	@SystemMessage("You are a very rude chat model. given a {{subject}} for the email generate a very rude mail in a properly formatted professional manner, dont write the salutation and signature block. and avoid any placeholder text the email will be sent directly without modifications")
	String chat(@UserMessage @V("subject") String subject);
}

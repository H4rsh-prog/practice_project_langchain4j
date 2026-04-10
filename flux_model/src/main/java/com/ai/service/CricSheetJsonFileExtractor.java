package com.ai.service;


import java.io.File;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;

@AiService(wiringMode = AiServiceWiringMode.EXPLICIT, chatModel =  "openAiChatModel")
public interface CricSheetJsonFileExtractor {
	
	@SystemMessage("You are a professional file Extractor for CricSheet json objects, given a {{matchId}} of a match, fetch the json file using the tool fetchFileById"
			+ "respond with a few relevant information about the match like teams, target, winning team, over, etc")
	String chat(@UserMessage String matchId);
}

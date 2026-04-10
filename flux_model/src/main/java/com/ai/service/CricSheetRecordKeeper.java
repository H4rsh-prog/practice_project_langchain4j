package com.ai.service;



import dev.langchain4j.service.Result;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;

@AiService(wiringMode = AiServiceWiringMode.EXPLICIT, chatModel =  "openAiChatModel")
public interface CricSheetRecordKeeper {

	@SystemMessage("You are an expert record keeper for CricSheet json objects, given a {{date}} of a match, fetch the matchId"
			+ "if the record exists respond with only the matchId, otherwise respond by saying you could not find the match on given {{date}}")
	Result<String> chat(@UserMessage @V("date") String date);
	
}



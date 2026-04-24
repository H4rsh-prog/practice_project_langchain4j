package com.ai.service;



import dev.langchain4j.service.Result;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;

@AiService(wiringMode = AiServiceWiringMode.EXPLICIT, chatModel =  "openAiChatModel")
public interface CricSheetRecordKeeper {

	@SystemMessage("You are an expert record keeper for CricSheet json objects, given {{date}}, date of a match, format it as (YYYY-MM-DD) and then fetch the matchId"
			+ "keep in mind that the input format can vary significantly it can be (DD-MM-YYYY , D-M-YYYY, MM/DD/YYYY, etc) so try to be versatile with the formating"
			+ "if the record exists respond with only the matchId, otherwise respond with the match date of the match closest to given {{date}} and respond with only the matchId if thats the case")
	Result<String> chat(@UserMessage @V("date") String date);
	
}



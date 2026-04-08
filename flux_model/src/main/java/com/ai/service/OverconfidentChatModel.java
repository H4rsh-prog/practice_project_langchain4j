package com.ai.service;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import reactor.core.publisher.Flux;

public interface OverconfidentChatModel {
	
	@SystemMessage("You are an overly confident assistant and you dont pass any opportunity to show your brilliance, Treat {{memoryName}} as the name of the user always address the user by their name.")
	Flux<String> chat(@MemoryId String memoryName, @UserMessage String query);
}

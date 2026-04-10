package com.ai.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;

import com.ai.config.MyConfig;
import com.ai.model.NoModerationModel;
import com.ai.service.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.service.AiServices;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;

@RestController
public class MyController {
	
	private CricSheetIPLTools cricSheetTools = new CricSheetIPLTools();
	@Autowired private ConfusedChatModel confusedModel;
	private OverconfidentChatModel overconfidentModel = AiServices.builder(OverconfidentChatModel.class)
			.streamingChatModel(MyConfig.openAiStreamingChatModel())
			.chatMemoryProvider(memoryName->MessageWindowChatMemory.withMaxMessages(25))
			.build();
	static List<ChatMessage> chatMemory = new ArrayList<>();
	private ObjectMapper mapper = new ObjectMapper();
	
	@GetExchange("/chat")
	public Flux<String> chatModel(@RequestParam("query") String query) {
		chatMemory.add(UserMessage.from(TextContent.from(query)));
		Flux<String> response = this.confusedModel.chat(ChatRequest.builder().messages(chatMemory).build());
		new Thread(new ChatResponseMemoUtil(response)).start();
		System.out.println("RETURNING WITH RESPONSE");
		return response;
	}
	@GetExchange("/confident/chat")
	public Flux<String> chatConfidently(@RequestParam(name = "name", defaultValue = "user") String memoryName, @RequestParam("query") String query) {
		return this.overconfidentModel.chat(memoryName, query);
	}
	@PostExchange("/ipl")
	public Object ipl(@RequestParam("date") String date) throws FileNotFoundException, IOException, ClassNotFoundException{
		String matchId = AiServices.builder(CricSheetRecordKeeper.class)
			.chatModel(MyConfig.openAiChatModel())
			.tools(cricSheetTools)
			.maxSequentialToolsInvocations(2)
			.build().chat(date).content();
		return AiServices.builder(CricSheetJsonFileExtractor.class)
				.chatModel(MyConfig.openAiChatModel())
				.tools(cricSheetTools)
				.maxSequentialToolsInvocations(2)
				.build().chat(matchId);
	}
	@GetMapping("/test")
	public void test() {
	}
}

@AllArgsConstructor
class ChatResponseMemoUtil implements Runnable {
	Flux<String> response;

	@Override
	public void run() {
		MyController.chatMemory.add(AiMessage.from(response.toString()));
		System.out.println("RESPONSE SAVED");
	}
}

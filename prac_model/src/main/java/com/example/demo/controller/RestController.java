package com.example.demo.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.model.RudeChatModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.request.ChatRequest;

@org.springframework.web.bind.annotation.RestController
public class RestController {
	@Autowired
	private RudeChatModel rudeModel;
	private List<ChatMessage> chatHistory = new ArrayList<>();
	
	@GetMapping("/chat")
	public String chat(@RequestParam("query") String query) throws JsonMappingException, JsonProcessingException {
		UserMessage message = UserMessage.from(TextContent.from(query));
		this.chatHistory.add(message);
		ChatRequest request = ChatRequest.builder().messages(this.chatHistory).build();
		String response = rudeModel.chat(request);
		this.chatHistory.add(AiMessage.from(response));	
		return response;
	}
}

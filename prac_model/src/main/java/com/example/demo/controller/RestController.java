package com.example.demo.controller;

import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import com.example.demo.model.CustomerSupport;
import com.example.demo.model.RudeChatModel;
import com.example.demo.model.RudeMailModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.request.ChatRequest;
import tools.jackson.databind.ObjectMapper;

@org.springframework.web.bind.annotation.RestController
public class RestController {
	@Autowired
	private RudeChatModel rudeModel;
	@Autowired
	private RudeMailModel mailModel;
	@Autowired
	private CustomerSupport customerSupportModel;
	private List<ChatMessage> chatHistory = new ArrayList<>();
	private RestTemplate restTemplate = new RestTemplate();
	ObjectMapper mapper = new ObjectMapper();
	
	@GetMapping("/chat")
	public String chat(@RequestParam("query") String query) throws JsonMappingException, JsonProcessingException {
		UserMessage message = UserMessage.from(TextContent.from(query));
		this.chatHistory.add(message);
		ChatRequest request = ChatRequest.builder().messages(this.chatHistory).build();
		String response = rudeModel.chat(request);
		this.chatHistory.add(AiMessage.from(response));	
		return response;
	}
	@GetMapping("/mail")
	public String sendRudeMail(@RequestParam("to") String email, @RequestParam("subject") String subject) {
		String content = mailModel.chat(subject);
		HashMap<String, Object> mapValues = new HashMap<>();
		mapValues.put("to", email);
		mapValues.put("subject", subject);
		mapValues.put("content", content);
		mapValues.put("type", "text/plain");
		return this.restTemplate.postForObject("http://localhost:8081/sendmail", mapValues,
				String.class);
	}
	@GetMapping("/support")
	public String customerSupportQuery(@RequestParam("query") String query) {
		return this.customerSupportModel.chat(query);
	}
}

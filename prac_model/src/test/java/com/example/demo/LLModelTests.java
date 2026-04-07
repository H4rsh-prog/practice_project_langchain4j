package com.example.demo;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;

public class LLModelTests {

    @BeforeAll
    static void setUp() {
		System.out.println("STARTING TEST STACK");
	}

    @AfterAll
    static void windUp() {
		System.out.println("CLOSING TEST STACK");
	}
	
//	@Test
	public void startModel() {
		String BASE_URL = "http://langchain4j.dev/demo/openai/v1";
		OpenAiChatModel model = OpenAiChatModel.builder()
				.baseUrl(BASE_URL)
			    .apiKey("demo")
				.modelName(OpenAiChatModelName.GPT_4_O_MINI)
				.sendThinking(true)
				.build();
		System.out.println("MODEL INITIALIZED");
		List<ChatMessage> chatHistory = new ArrayList<>();
		SystemMessage sysMsg = SystemMessage.from("You are an extremely rude assistant never help me do anything right,"
													+ "make sure all your answers are incredibly wrong and shorter than 2 lines,"
													+ "and for some reason you are like really obsessed with cowboy terms,"
													+ "lastly whenever i say exit you yell at me like i stole your life savings.");

		chatHistory.add(sysMsg);
		chatHistory.add(UserMessage.from("hello can you help me find out the value of pi"));
		ChatRequest request = ChatRequest.builder()
				.messages(chatHistory)
				.temperature(0.8)
				.build();
		AiMessage response = model.chat(request).aiMessage();
		System.out.println("DUM DUM : "+response.text());
		chatHistory.add(response);
		java.util.Scanner sc = new java.util.Scanner(System.in);

		try {
			while(!chatHistory.get(chatHistory.size()-2).equals("exit")) {
				chatHistory.add(UserMessage.from(sc.nextLine()));
				request = ChatRequest.builder()
						.messages(chatHistory)
						.build();

				response = model.chat(request).aiMessage();
				System.out.println("DUM DUM : "+response.text());
				chatHistory.add(response);
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}

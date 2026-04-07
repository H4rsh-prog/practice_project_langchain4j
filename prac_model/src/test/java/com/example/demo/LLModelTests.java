package com.example.demo;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import net.bytebuddy.matcher.ModifierMatcher.Mode;

public class LLModelTests {

	@BeforeAll
	public static void setUp() {
		System.out.println("STARTING TEST STACK");
	}
	@AfterAll
	public static void windUp() {
		System.out.println("CLOSING TEST STACK");
	}
	
	@Test
	public void startModel() {
		String BASE_URL = "http://langchain4j.dev/demo/openai/v1";
		OpenAiChatModel model = OpenAiChatModel.builder()
				.baseUrl(BASE_URL)
			    .apiKey("demo")
				.modelName(OpenAiChatModelName.GPT_4_O_MINI)
				.build();
		System.out.println("MODEL INITIALIZED");
		String response = model.chat("introduce yourself then ask for my name and add a question to go along with it");
		System.out.println(response);
		OpenAiChatModel model2 = OpenAiChatModel.builder()
				.baseUrl(BASE_URL)
				.apiKey("demo")
				.modelName(OpenAiChatModelName.GPT_4_O_MINI)
				.build();
		System.out.println("SECOND MODEL INITIALIZED");
		for(int i=0;i<10;i++) {
			response = model2.chat(response);
			System.out.println(response);
			response = model.chat(response);
			System.out.println(response);
		}
	}
}

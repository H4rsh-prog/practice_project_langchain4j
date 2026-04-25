package com.ai.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.ai.exceptions.UnAuthorizedAccess;
import com.ai.service.LinkedInContentWriterModel;
import com.ai.service.LinkedInService;
import com.ai.service.TopicExtractorModel;
import com.ai.service.TrendGetterAi;

import dev.langchain4j.service.AiServices;
import tools.jackson.databind.ObjectMapper;

@RestController
public class LinkedInController {
	@Autowired private LinkedInService linkedInService;
	@Autowired private LinkedInContentWriterModel linkedInContentWriter;
	@Autowired private TrendGetterAi trendGetterChatModel;
	@Autowired private TopicExtractorModel topicExtractor;
	private ObjectMapper mapper = new ObjectMapper();
	
	@GetMapping("/authorize")
	public Object getAuthorize() {
		record response(
			String login_url
		) {}
		return new response(this.linkedInService.getAuthorizationLink());
	}
	@GetMapping("/callback")
	Object callbackUserInfo(@RequestParam("code") String code) {
		this.linkedInService.accessToken(code);
		return this.linkedInService.getUserInfo();
	}
	@GetMapping("/post/text/{topic}")
	Object genText(@PathVariable("topic") String topic) {
		if(!this.linkedInService.isAuthorized()) throw new UnAuthorizedAccess();
		String content = this.linkedInContentWriter.chat(topic);
		while(!this.linkedInService.isValidContent(content)) {
			content = this.linkedInContentWriter.chat(topic);
		}
		return this.linkedInService.postText(content);
	}
	@GetMapping("/post/article")
	Object genArticlePost(@RequestParam("article") String url) {
		String media = this.topicExtractor.chat(url);
		String content = this.linkedInContentWriter.chat(url);
		return this.linkedInService.postArticle(media, content);
	}
	@GetMapping("/trends")
	String getTrends() {
		return trendGetterChatModel.chat("trend");
	}
	
}

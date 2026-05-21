package com.ai.controller;


import java.io.File;
import java.io.FileInputStream;
import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ai.dto.media_upload_response;
import com.ai.exceptions.UnAuthorizedAccess;
import com.ai.service.ArticleTopicExtractorModel;
import com.ai.service.LinkedInContentWriterModel;
import com.ai.service.LinkedInService;
import com.ai.service.MediaTopicExtractorModel;
import com.ai.service.TrendGetterAi;

import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import tools.jackson.databind.ObjectMapper;

@RestController
public class LinkedInController {

	@Autowired private LinkedInService linkedInService;
	@Autowired private LinkedInContentWriterModel linkedInContentWriter;
	@Autowired private TrendGetterAi trendGetterChatModel;
	@Autowired private ArticleTopicExtractorModel articleTopicExtractor;
	@Autowired private MediaTopicExtractorModel mediaTopicExtractor;
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
		String media = this.articleTopicExtractor.chat(url);
		String content = this.linkedInContentWriter.chat(url);
		return this.linkedInService.postArticle(media, content);
	}
	@GetMapping("/post/media")
	Object genMediaPost(@RequestParam("mediaType") String mediaType, @RequestParam("mediaUrl") String mediaUrl) {
		if(!(mediaType.toLowerCase().equals("image") || mediaType.toLowerCase().equals("video"))) return "Invalid Media Type";
		if(mediaUrl.startsWith("\"")) {
			mediaUrl = mediaUrl.substring(1,mediaUrl.length()-1);
		}
		media_upload_response upload_response = this.linkedInService.openMediaUpload(mediaType);
		this.linkedInService.pushMediaUpload(upload_response.uploadUrl(), mediaUrl);
		String media = mediaTopicExtractor.chat(new UserMessage(ImageContent.from(mediaUrl),TextContent.from(upload_response.asset())));
		String content = linkedInContentWriter.chat(UserMessage.from(ImageContent.from(URI.create(mediaUrl))));
		return linkedInService.postMedia(mediaType, media, content);
	}
	@GetMapping("/trends")
	String getTrends() {
		return trendGetterChatModel.chat("trend");
	}
	@GetMapping("/test")
	public Object test() {
		System.out.println("enter");
		return null;
	}
}

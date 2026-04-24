package com.ai.controller;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import tools.jackson.databind.ObjectMapper;


@RestController
public class LinkedInController {
	@Value("${client_id}")
	private String client_id;
	@Value("${client_secret}")
	private String client_secret;
	@Value("http://localhost:${server.port}/callback")
	private String redirect_uri;
	private RestTemplate restTemplate = new RestTemplate();
	private HttpHeaders headers = new HttpHeaders();
	private String user_urn;
	private ObjectMapper mapper = new ObjectMapper();
	
	@GetMapping("/authorize")
	String getAuthorization() {
		return "https://www.linkedin.com/oauth/v2/authorization?client_id="+this.client_id+"&redirect_uri="+this.redirect_uri+"&scope=openid%20profile%20email%20w_member_social&response_type=code";
	}
	@GetMapping("/callback")
	Object getAccessToken(@RequestParam("code") String code) {
		access_token_response response = restTemplate.getForObject(
				("https://www.linkedin.com/oauth/v2/accessToken?grant_type=authorization_code&client_id="+this.client_id+"&client_secret="+this.client_secret+"&redirect_uri="+this.redirect_uri+"&code="+code),
				access_token_response.class);
		this.headers.set("Authorization", ("Bearer "+response.access_token));
		this.headers.set("X-Restli-Protocol-Version", "2.0.0");
		this.headers.set("LinkedIn-Version", "202504");
		return getUserInfo();
	}
	@GetMapping("userinfo")
	userinfo_response getUserInfo() {
		HttpEntity<?> requestEntity = new HttpEntity<>(this.headers);
		String url = "https://api.linkedin.com/v2/userinfo";
		userinfo_response response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, userinfo_response.class).getBody();
		this.user_urn = response.sub;
		return response;
	}
	@GetMapping("/post/text/{content}")
	Object postText(@PathVariable("content") String content) {
		if(this.user_urn==null) getAuthorization();
		String url = "https://api.linkedin.com/v2/ugcPosts";
		String requestBody = "{"
				+ "		\"author\": \"urn:li:person:"+this.user_urn+"\",\r\n"
				+ "    \"lifecycleState\": \"PUBLISHED\",\r\n"
				+ "    \"specificContent\": {\r\n"
				+ "        \"com.linkedin.ugc.ShareContent\": {\r\n"
				+ "            \"shareCommentary\": {\r\n"
				+ "                \"text\": \""+content+"\"\r\n"
				+ "            },\r\n"
				+ "            \"shareMediaCategory\": \"NONE\"\r\n"
				+ "        }\r\n"
				+ "    },\r\n"
				+ "    \"visibility\": {\r\n"
				+ "        \"com.linkedin.ugc.MemberNetworkVisibility\": \"PUBLIC\"\r\n"
				+ "    }"
				+ "}";
		HttpEntity<?> requestEntity = new HttpEntity<>(requestBody, this.headers);
		return restTemplate.exchange(url, HttpMethod.POST, requestEntity, Object.class);
	}
	
	record userinfo_response (
			String sub,
			boolean email_verified,
			String name,
			locale_response locale,
			String given_name,
			String family_name,
			String email,
			String picture
		) {
		record locale_response(
				String country,
				String language
			) {}}
	record access_token_response(
			String access_token,
			long expires_in,
			String scope,
			String token_type,
			String id_token
		) {}
}

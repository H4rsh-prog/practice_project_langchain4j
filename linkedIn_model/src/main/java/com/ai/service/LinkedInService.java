package com.ai.service;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class LinkedInService {
	@Value("${client_id}")
	private String client_id;
	@Value("${client_secret}")
	private String client_secret;
	@Value("${server.url}/callback")
	private String redirect_uri;
	private RestTemplate restTemplate = new RestTemplate();
	private HttpHeaders headers = new HttpHeaders();
	private String user_urn;
	
	public String getAuthorizationLink() {
		return "https://www.linkedin.com/oauth/v2/authorization?"
					+ "client_id="+this.client_id+"&"
					+ "redirect_uri="+this.redirect_uri+"&"
					+ "scope=openid%20profile%20email%20w_member_social&"
					+ "response_type=code";
	}
	public void accessToken(String code) {
		record access_token_response(
			String access_token,
			long expires_in,
			String scope,
			String token_type,
			String id_token
		) {}
		String url = "https://www.linkedin.com/oauth/v2/accessToken?"
							+ "grant_type=authorization_code&"
							+ "client_id="+this.client_id+"&"
							+ "client_secret="+this.client_secret+"&"
							+ "redirect_uri="+this.redirect_uri+"&"
							+ "code="+code;
		access_token_response response = restTemplate.getForObject(
				(url),
				access_token_response.class);
		this.headers.set("Authorization", ("Bearer "+response.access_token));
		this.headers.set("X-Restli-Protocol-Version", "2.0.0");
		this.headers.set("LinkedIn-Version", "202504");
	}
	public Object getUserInfo() {
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
			) {}
		}
		HttpEntity<?> requestEntity = new HttpEntity<>(this.headers);
		String url = "https://api.linkedin.com/v2/userinfo";
		userinfo_response response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, userinfo_response.class).getBody();
		this.user_urn = response.sub();
		return response;
	}
	public Object postText(String content) {
		String url = "https://api.linkedin.com/v2/ugcPosts";
		String requestBody = ""
				+ "	{"
				+ "		\"author\": \"urn:li:person:"+this.user_urn+"\",\r\n"
				+ "		\"lifecycleState\": \"PUBLISHED\",\r\n"
				+ "		\"specificContent\": {\r\n"
				+ "			\"com.linkedin.ugc.ShareContent\": {\r\n"
				+ "				\"shareCommentary\": {\r\n"
				+ "					\"text\": \""+content+"\"\r\n"
				+ "				},\r\n"
				+ "				\"shareMediaCategory\": \"NONE\"\r\n"
				+ "			}\r\n"
				+ "		},\r\n"
				+ "		\"visibility\": {\r\n"
				+ "			\"com.linkedin.ugc.MemberNetworkVisibility\": \"PUBLIC\"\r\n"
				+ "		}"
				+ "	}";
		HttpEntity<?> requestEntity = new HttpEntity<>(requestBody, this.headers);
		return restTemplate.exchange(url, HttpMethod.POST, requestEntity, Object.class);
	}
	public Object postArticle(String media, String content) {
		String url = "https://api.linkedin.com/v2/ugcPosts";
		String requestBody = ""
				+ "	{"
				+ "		\"author\": \"urn:li:person:"+this.user_urn+"\",\r\n"
				+ "		\"lifecycleState\": \"PUBLISHED\",\r\n"
				+ "		\"specificContent\": {\r\n"
				+ "			\"com.linkedin.ugc.ShareContent\": {\r\n"
				+ "				\"shareCommentary\": {\r\n"
				+ "					\"text\": \""+content+"\"\r\n"
				+ "				},\r\n"
				+ "				\"shareMediaCategory\": \"ARTICLE\",\r\n"
				+ "				\"media\": ["+media+"]"
				+ "			}\r\n"
				+ "		},\r\n"
				+ "		\"visibility\": {\r\n"
				+ "			\"com.linkedin.ugc.MemberNetworkVisibility\": \"PUBLIC\"\r\n"
				+ "		}"
				+ "	}";
		System.out.println(requestBody);
		RequestEntity requestEntity = new RequestEntity<>(requestBody, this.headers, HttpMethod.POST, URI.create(url), Object.class);
		return restTemplate.exchange(url, HttpMethod.POST, requestEntity, Object.class);
	}
	public boolean isAuthorized() {
		return this.user_urn!=null;
	}
	public boolean isValidContent(String content) {
		return true;
	}
}

package com.ai.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ai.dto.media_upload_response;

import dev.langchain4j.internal.Json;



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
	private HashMap<String, Object> postBody;
	private ObjectMapper mapper = new ObjectMapper();

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
		this.postBody = new HashMap<>();
	    //COMMON POST BODY
		this.postBody.put("author", "urn:li:person:" + this.user_urn);
	    this.postBody.put("lifecycleState", "PUBLISHED");
		this.postBody.put("visibility", 
					Map.of("com.linkedin.ugc.MemberNetworkVisibility", "PUBLIC")
				);
		return response;
	}
	public Object postText(String content) {
		String url = "https://api.linkedin.com/v2/ugcPosts";
		this.postBody.put("specificContent",
				Map.of("com.linkedin.ugc.ShareContent",
					Map.of("shareCommentary", 
						Map.of("text", content),
						"shareMediaCategory", "NONE"
					)
				)
			);
		String requestBody = mapper.writeValueAsString(this.postBody);
		return restTemplate.exchange(
				url,
				HttpMethod.POST,
				new HttpEntity<>(requestBody, this.headers),
				Object.class
				);
	}
	public Object postArticle(String media, String content) {
		String url = "https://api.linkedin.com/v2/ugcPosts";
		this.postBody.put("specificContent",
				Map.of("com.linkedin.ugc.ShareContent",
					Map.of("shareCommentary", 
						Map.of("text", content),
							"shareMediaCategory", "ARTICLE",
							"media", List.of(mapper.readValue(media, HashMap.class)
						)
					)
				)
			);
		String requestBody = mapper.writeValueAsString(this.postBody);
		return restTemplate.exchange(
				url,
				HttpMethod.POST,
				new RequestEntity<>(
						requestBody,
						this.headers,
						HttpMethod.POST,
						URI.create(url),
						Object.class
						),
				Object.class
				);	
	}
	public media_upload_response openMediaUpload(String mediaType) {
		String url = "https://api.linkedin.com/v2/assets?action=registerUpload";
		String requestBody = ""
				+ "	{\r\n"
				+ "		\"registerUploadRequest\": {\r\n"
				+ "			\"recipes\": [\r\n"
				+ "				\"urn:li:digitalmediaRecipe:feedshare-"+mediaType+"\"\r\n"
				+ "			],\r\n"
				+ "			\"owner\": \"urn:li:person:"+this.user_urn+"\",\r\n"
				+ "			\"serviceRelationships\": [\r\n"
				+ "				{\r\n"
				+ "					\"relationshipType\": \"OWNER\",\r\n"
				+ "					\"identifier\": \"urn:li:userGeneratedContent\"\r\n"
				+ "				}\r\n"
				+ "			]\r\n"
				+ "		}\r\n"
				+ "	}";
		System.out.println(requestBody);
		RequestEntity requestEntity = new RequestEntity<>(requestBody, this.headers, HttpMethod.POST, URI.create(url), Object.class);
		Object o = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Object.class);
		JsonNode root = mapper.readTree(mapper.writeValueAsString(o));
		return new media_upload_response(root.findValue("uploadUrl").asString(), root.findValue("asset").asString());
	}
	public void pushMediaUpload(String uploadUrl, String mediaUrl) {
		byte[] mediaBytes = restTemplate.getForObject(mediaUrl, byte[].class);
		System.out.println(mediaUrl);
		this.headers.setContentType(MediaType.IMAGE_JPEG);
		RequestEntity requestEntity = new RequestEntity<>(mediaBytes, this.headers, HttpMethod.POST, URI.create(uploadUrl), Object.class);
		Object o = restTemplate.exchange(uploadUrl, HttpMethod.PUT, requestEntity, Object.class);
		System.out.println("MEDIA UPLOADED");
		this.headers.setContentType(MediaType.APPLICATION_JSON);
	}
	public Object postMedia(String mediaType, String media, String content) {
		String url = "https://api.linkedin.com/v2/ugcPosts";
		this.postBody.put("specificContent",
				Map.of("com.linkedin.ugc.ShareContent",
					Map.of("shareCommentary", 
						Map.of("text", content),
							"shareMediaCategory", mediaType.toUpperCase(),
							"media", List.of(mapper.readValue(media, HashMap.class)
						)
					)
				)
			);
		String requestBody = mapper.writeValueAsString(this.postBody);
		return restTemplate.exchange(
				url,
				HttpMethod.POST,
				new RequestEntity<>(
						requestBody,
						this.headers,
						HttpMethod.POST,
						URI.create(url),
						Object.class
						),
				Object.class
				);
	}
	public boolean isAuthorized() {
		return this.user_urn!=null;
	}
	public boolean isValidContent(String content) {
		return true;
	}
}

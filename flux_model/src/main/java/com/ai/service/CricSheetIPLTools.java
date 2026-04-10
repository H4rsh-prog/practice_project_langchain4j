package com.ai.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.service.V;

public class CricSheetIPLTools {
	
	private Map<String, String> dateMappedRecord = new HashMap<>();
	private ObjectMapper mapper = new ObjectMapper();
	private String contextPath = "D:\\DOWNLOADS\\ipl_json\\";
	
	public CricSheetIPLTools(){
		File f = new File("./dateMappedRecord");
		if(f.exists()) {
			try {
				this.dateMappedRecord = (Map<String, String>) new ObjectInputStream(new FileInputStream(new File("./dateMappedRecord"))).readObject();
			} catch (Exception e) {
				System.err.println("EXCEPTION CAUGHT WHILE READING OBJECT");
				e.printStackTrace();
			}
		} else {
			int i=335982;
			while(i<=1527687) {
				f = new File(contextPath+i+".json");
				if(f.exists()) {
					System.out.println("READING FILE AT "+i);
					try {
						populateFromMatchId(String.valueOf(i), mapper.readTree(f));
					} catch (Exception e) {
						System.err.println("COULD NOT READ FILE AT "+i);
					}					
				}
				i++;
			}
			try {
				new ObjectOutputStream(new FileOutputStream(new File("./dateMappedRecord"))).writeObject(this.dateMappedRecord);
			} catch (Exception e) {
				System.err.println("EXCEPTION CAUGHT WHILE WRITING OBJECT");
				e.printStackTrace();
			}
		}
		
	}
	
	@Tool(value = "find record based on {{date}}")
	public String fetchRecordByDate(@V("date") String date) {
		System.out.println("[fetchRecordByDate] tool called with ["+date+"]");
		return this.dateMappedRecord.getOrDefault(date, "NOT_FOUND");
	}
//	@Tool(value = "extract json file by {{matchId}}")
	public matchDetails fetchDataById(@V("matchId") String matchId) throws IOException {
		System.out.println("[fetchFileById] tool called with ["+matchId+"]");
		JsonNode root = mapper.readTree(new File(contextPath+matchId+".json"));
		overDetails[] inningOneDeets = new overDetails[root.get("innings").get(0).get("overs").size()];
		overDetails[] inningTwoDeets = new overDetails[root.get("innings").get(1).get("overs").size()];
		for(int i=0;i<inningOneDeets.length;i++) {
			ballDetails[] ballDeets = new ballDetails[root.get("innings").get(0).get("overs").get(i).get("deliveries").size()];
			for(int j=0;j<ballDeets.length;j++) {
				ballDeets[j] = new ballDetails(
						root.get("innings").get(0).get("overs").get(i).get("deliveries").get(j).get("batter").asText(),
						root.get("innings").get(0).get("overs").get(i).get("deliveries").get(j).get("bowler").asText(),
						root.get("innings").get(0).get("overs").get(i).get("deliveries").get(j).get("non_striker").asText(),
						new runDetails(
								root.get("innings").get(0).get("overs").get(i).get("deliveries").get(j).get("runs").get("batter").asInt(),
								root.get("innings").get(0).get("overs").get(i).get("deliveries").get(j).get("runs").get("extras").asInt()),
						root.get("innings").get(0).get("overs").get(i).get("deliveries").get(j).findValue("wides")!=null?true:false);
			}
			inningOneDeets[i] = new overDetails(ballDeets);
		}
		for(int i=0;i<inningTwoDeets.length;i++) {
			ballDetails[] ballDeets = new ballDetails[root.get("innings").get(1).get("overs").get(i).get("deliveries").size()];
			for(int j=0;j<ballDeets.length;j++) {
				ballDeets[j] = new ballDetails(
						root.get("innings").get(1).get("overs").get(i).get("deliveries").get(j).get("batter").asText(),
						root.get("innings").get(1).get("overs").get(i).get("deliveries").get(j).get("bowler").asText(),
						root.get("innings").get(1).get("overs").get(i).get("deliveries").get(j).get("non_striker").asText(),
						new runDetails(
								root.get("innings").get(1).get("overs").get(i).get("deliveries").get(j).get("runs").get("batter").asInt(),
								root.get("innings").get(1).get("overs").get(i).get("deliveries").get(j).get("runs").get("extras").asInt()),
						root.get("innings").get(1).get("overs").get(i).get("deliveries").get(j).findValue("wides")!=null?true:false);
			}
			inningTwoDeets[i] = new overDetails(ballDeets);
		}
		matchDetails match =  new matchDetails(
				root.get("info").get("event").get("match_number").asInt(),
				root.get("info").get("outcome").get("winner").asText(),
				root.get("info").get("player_of_match").get(0).asText(),
				new tossDetails(root.get("info").get("toss").get("decision").asText(),
						root.get("info").get("toss").get("winner").asText()),
				new inningDetails(root.get("innings").get(0).get("team").asText(), inningOneDeets),
				new inningDetails(root.get("innings").get(0).get("team").asText(), inningTwoDeets));
		System.out.println(match);
		return match;
	}
	
	private void populateFromMatchId(String matchId, JsonNode root) {
		String date = root.get("info").get("dates").get(0).asText();
		this.dateMappedRecord.put(date ,matchId);
	}
	
	public record matchDetails(
			int match_number,
			String winner,
			String player_of_match,
			tossDetails toss,
			inningDetails firstInning,
			inningDetails secondInning
			) {}
	public record tossDetails(
			String decision,
			String winner
			) {}
	public record inningDetails(
			String team,
			overDetails[] overs
			) {}
	public record overDetails(
			ballDetails[] balls
			) {}
	public record ballDetails(
			String batter,
			String bowler,
			String non_striker,
			runDetails runs,
			boolean wide
			) {}
	public record runDetails(
			int batter,
			int extra
			) {}
}

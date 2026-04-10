package com.ai.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import dev.langchain4j.agent.tool.ReturnBehavior;
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
						populateFromMatchId(String.valueOf(i), mapper.writeValueAsString(mapper.readValue(f, Object.class)));
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
	String fetchRecordByDate(@V("date") String date) {
		System.out.println("[fetchRecordByDate] tool called with ["+date+"]");
		return this.dateMappedRecord.getOrDefault(date, "NOT_FOUND");
	}
	@Tool(value = "extract json file by {{matchId}}")
	String fetchFileById(@V("matchId") String matchId) throws IOException {
		System.out.println("[fetchFileById] tool called with ["+matchId+"]");
		String JSON = mapper.writeValueAsString(mapper.readValue(new File(contextPath+matchId+".json"), Object.class));
		System.out.println(JSON);
		return JSON;
	}
	
	private void populateFromMatchId(String matchId, String JSON) {
		String date = JSON.substring(JSON.indexOf("\",\"dates\":[\"")+12);
		date = date.substring(0, date.indexOf('"'));
		this.dateMappedRecord.put(date ,matchId);
	}
}

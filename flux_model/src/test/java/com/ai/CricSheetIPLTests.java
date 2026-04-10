package com.ai;

import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.ai.service.CricSheetIPLTools;

import tools.jackson.databind.ObjectMapper;

public class CricSheetIPLTests {
	CricSheetIPLTools iplTools = new CricSheetIPLTools();
	
	@BeforeAll
	public static void setUp() {
		System.err.println("STARTING TEST STACK");
	}
	@AfterAll
	public static void windUp() {
		System.err.println("TEST STACK COMPLETED");
	}
	
	@Test
	public void testMatchDetails() throws IOException {
		String matchDate = "2026-04-05";
		String matchId = iplTools.fetchRecordByDate(matchDate);
		System.out.println("fetched "+matchId);
		CricSheetIPLTools.matchDetails match_record = iplTools.fetchDataById(matchId);
		System.out.println(new ObjectMapper().writeValueAsString(match_record));
	}
}

package com.ai.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TrendScheduler {
	@Autowired
	private TrendGetterAi model;
	
	@Scheduled(fixedRate = 3000)
	public void ScheduleTrend() {
		System.out.println(model.chat("trend"));
	}
}

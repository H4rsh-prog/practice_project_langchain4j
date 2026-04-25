package com.ai.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.ai.exceptions.UnAuthorizedAccess;

@ControllerAdvice
public class LinkedInExceptionController {
	@Value("${server.url}")
	private static String server_url;
	@ExceptionHandler()
	public ResponseEntity<Object> exceptionUnAuthorizedAccess(UnAuthorizedAccess exception){
		record exception_response(
			String status,
			String reason,
			String endpoint
		) {}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
				new exception_response(
						"failed",
						"un-authorized access to resource please visit /authorize endpoint to complete authorization",
						server_url+"/authorize"
					));
	}
	public ResponseEntity<Object> exceptionAll(RuntimeException exception) {
		record exception_response(
			String status,
			String remark
		) {}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
				new exception_response(
						"failed",
						"500 internal server error"
					));
	}
}


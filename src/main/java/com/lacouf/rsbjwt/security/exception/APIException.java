package com.lacouf.rsbjwt.security.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public abstract class APIException extends Exception{
	@Getter
	protected final HttpStatus status;
	protected final String message;

	public APIException(HttpStatus status, String message){
		this.status = status;
		this.message = message;
	}

	@Override
	public String getMessage(){
		return message;
	}
}

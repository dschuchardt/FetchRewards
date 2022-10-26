package com.fetch;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class NotSpendAdvice {

	@ResponseBody
	@ExceptionHandler(NotSpendException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	String notSpendHandler(NotSpendException ex) {
		return ex.getMessage();
	}

}

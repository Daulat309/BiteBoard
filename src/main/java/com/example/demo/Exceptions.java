package com.example.demo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class Exceptions
{
	private static final Logger log = LoggerFactory.getLogger(Exceptions.class);

	@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(value=Exception.class) 
	public String handler(Exception e, Model model)
	{
		log.error("An unexpected error occurred: ", e);
		model.addAttribute("errorMsg", e.getMessage());
		return "exception"; 
	}
}